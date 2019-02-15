package com.giraone.pms.repository;

import com.giraone.pms.domain.Employee;
import com.giraone.pms.domain.EmployeeName;
import com.giraone.pms.domain.enumeration.EmployeeNameFilterKey;
import com.giraone.pms.domain.filter.EmployeeNameKeyValue;
import com.giraone.pms.service.NameNormalizeService;
import com.giraone.pms.service.impl.NameNormalizeServiceImpl;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// The name must end in "Impl" - see https://jira.spring.io/browse/DATACMNS-1348?attachmentViewMode=list
@SuppressWarnings("unused")
public class EmployeeRepositoryImpl implements EmployeeRepositoryCustom<Employee> {

    private final Logger log = LoggerFactory.getLogger(EmployeeRepositoryImpl.class);

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private EmployeeNameRepository employeeNameRepository;

    private NameNormalizeService nameNormalizeService = new NameNormalizeServiceImpl();


    /**
     * Customized save method of {@see JpaRepository} to store also the redundant employee names
     *
     * @param employee the employee entity to be saved
     * @return the stored employee entity
     */
    @Override
    public Employee save(Employee employee) {

        if (log.isDebugEnabled()) {
            log.debug("EmployeeRepositoryImpl.save " + employee.getId() + " " + employee.getSurname());
        }

        if (employee.getId() == null) {
            em.persist(employee);
        } else {
            employee = em.merge(employee);
        }

        // the redundant list of names for optimized querying (normalized, phonetic)
        final List<EmployeeName> employeeNames = this.buildNames(employee);

        if (log.isDebugEnabled()) {
            log.debug("EmployeeRepositoryImpl names=" + employeeNames);
        }
        employeeNameRepository.saveAll(employeeNames);

        return employee;
    }

    /**
     * Customized saveAll method.
     * Saves all given entities.
     *
     * @param employees must not be {@literal null}.
     * @return the saved entities will never be {@literal null}.
     * @throws IllegalArgumentException in case the given entity is {@literal null}.
     */
    @Override
    public List<Employee> saveAllCustom(Iterable<Employee> employees) {

        if (log.isDebugEnabled()) {
            log.debug("EmployeeRepositoryImpl.saveAll");
        }

        final List<Employee> ret = new ArrayList<>();
        employees.forEach(e -> ret.add(this.save(e)));
        return ret;
    }

    /**
     * Additional method to perform re-indexing on an employee list
     *
     * @param employeeList the employee list to be re-indexed (the entities itself is untouched)
     * @param skipDeletion if true it is assumed, that there was no index yet and the delete action of existing
     *                     indices is not performed. This is a performance optimization.
     * @return the number of re-indexed employees
     */
    public int reIndex(List<Employee> employeeList, boolean skipDeletion) {

        final List<Long> owners = new ArrayList<>();
        final List<EmployeeName> namesList = new ArrayList<>();
        employeeList.forEach(employee -> {
            owners.add(employee.getId());
            namesList.addAll(buildNames(employee));
        });

        if (!skipDeletion) {
            // Split into a maximum of 100 ids for the IN statement
            final List<List<Long>> ownerPartitions = Lists.partition(owners, 100);
            for (List<Long> ownerPartition : ownerPartitions) {
                this.employeeRepository.deleteByOwners(ownerPartition);
            }
        }
        employeeNameRepository.saveAll(namesList);
        return employeeList.size();
    }

    /**
     * Build the list of redundant EmployeeNames for an Employee entity
     *
     * @param employee the employee entity
     * @return list of EmployeeName entities
     */
    private List<EmployeeName> buildNames(Employee employee) {

        final List<EmployeeName> names = new ArrayList<>();
        this.buildName(employee)
            .forEach(name -> names.add(new EmployeeName(employee, name.getKey(), name.getValue())));
        return names;
    }

    private Set<EmployeeNameKeyValue> buildName(Employee employee) {

        final Set<EmployeeNameKeyValue> ret = new HashSet<>();

        final String originalSurname = employee.getSurname();
        final String normalizedSurname = nameNormalizeService.normalize(originalSurname);
        if (normalizedSurname != null) {
            ret.add(new EmployeeNameKeyValue(EmployeeNameFilterKey.LS.toString(), normalizedSurname));
            final List<String> surNames = nameNormalizeService.split(normalizedSurname);
            for (String name : surNames) {
                ret.add(new EmployeeNameKeyValue(EmployeeNameFilterKey.NS.toString(), nameNormalizeService.reduceSimplePhonetic(name)));
                ret.add(new EmployeeNameKeyValue(EmployeeNameFilterKey.PS.toString(), nameNormalizeService.phonetic(name)));
            }
        }
        final String originalGivenName = employee.getGivenName();
        final String normalizedGivenName = nameNormalizeService.normalize(originalGivenName);
        if (normalizedGivenName != null) {
            ret.add(new EmployeeNameKeyValue(EmployeeNameFilterKey.LG.toString(), normalizedGivenName));
            final List<String> givenNames = nameNormalizeService.split(normalizedGivenName);
            for (String name : givenNames) {
                ret.add(new EmployeeNameKeyValue(EmployeeNameFilterKey.NG.toString(), nameNormalizeService.reduceSimplePhonetic(name)));
                ret.add(new EmployeeNameKeyValue(EmployeeNameFilterKey.PG.toString(), nameNormalizeService.phonetic(name)));
            }
        }
        return ret;
    }

}
