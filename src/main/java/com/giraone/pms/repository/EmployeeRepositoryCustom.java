package com.giraone.pms.repository;

import java.util.List;

// The name must end in "Custom" - see https://jira.spring.io/browse/DATACMNS-1348?attachmentViewMode=list
public interface EmployeeRepositoryCustom<Employee> {

    //-- OVERWRITTEN from Jpa or CrudRepository ------------------------------------------------------------------------

    /**
     * Customized save method.
     * Saves a given entity. Use the returned instance for further operations as the save operation might have changed the
     * entity instance completely.
     *
     * @param employee must not be {@literal null}.
     * @return the saved entity will never be {@literal null}.
     */
    Employee save(Employee employee);

    /**
     * Customized saveAll method.
     * Saves all given entities.
     *
     * @param employees must not be {@literal null}.
     * @return the saved entities will never be {@literal null}.
     * @throws IllegalArgumentException in case the given entity is {@literal null}.
     */
    List<Employee> saveAllCustom(Iterable<Employee> employees); // We cannot use saveAll because of same erase on generic!

    //-- ADDITIONAL ----------------------------------------------------------------------------------------------------

    /**
     * Additional method to perform re-indexing on an employee list
     *
     * @param employeeList the employee list to be re-indexed (the entities itself is untouched)
     * @param skipDeletion if true it is assumed, that there was no index yet and the delete action of existing
     *                     indixes is not performed. This is a performance optimization.
     * @return the number of re-indexed employees
     */
    int reIndex(List<Employee> employeeList, boolean skipDeletion);
}
