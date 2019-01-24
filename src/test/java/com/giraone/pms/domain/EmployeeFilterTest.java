package com.giraone.pms.domain;

import com.giraone.pms.domain.enumeration.StringSearchMode;
import com.giraone.pms.domain.filter.EmployeeFilter;
import com.giraone.pms.domain.filter.EmployeeFilterPair;
import com.giraone.pms.service.NameNormalizeService;
import com.giraone.pms.service.impl.NameNormalizeServiceImpl;
import org.junit.Assert;
import org.junit.Test;

import java.util.Optional;

public class EmployeeFilterTest {

    NameNormalizeService nameNormalizeService = new NameNormalizeServiceImpl();

    @Test
    public void buildQueryValueNull() {

        EmployeeFilter filter = new EmployeeFilter(Optional.empty(), StringSearchMode.EXACT, Optional.empty());
        EmployeeFilterPair pair = filter.buildQueryValue(nameNormalizeService);
        Assert.assertNull(pair);
    }

    @Test
    public void buildQueryValueExact() {

        EmployeeFilter filter = new EmployeeFilter(Optional.of("Heinz"), StringSearchMode.EXACT, Optional.empty());
        EmployeeFilterPair pair = filter.buildQueryValue(nameNormalizeService);
        Assert.assertNull(pair.getKey());
        Assert.assertEquals("Heinz", pair.getValue());
    }

    @Test
    public void buildQueryValueNormalized() {

        EmployeeFilter filter = new EmployeeFilter(Optional.of("Heinz"), StringSearchMode.NORMALIZED, Optional.empty());
        EmployeeFilterPair pair = filter.buildQueryValue(nameNormalizeService);
        Assert.assertEquals("SN", pair.getKey());
        Assert.assertEquals("heinz", pair.getValue());
    }

    @Test
    public void buildQueryValuePrefixedNormalized() {

        EmployeeFilter filter = new EmployeeFilter(Optional.of("Heinz"), StringSearchMode.PREFIX_NORMALIZED, Optional.empty());
        EmployeeFilterPair pair = filter.buildQueryValue(nameNormalizeService);
        Assert.assertEquals("SN", pair.getKey());
        Assert.assertEquals("heinz%", pair.getValue());
    }

    @Test
    public void buildQueryValuePhonetic() {

        EmployeeFilter filter = new EmployeeFilter(Optional.of("Heinz"), StringSearchMode.PHONETIC, Optional.empty());
        EmployeeFilterPair pair = filter.buildQueryValue(nameNormalizeService);
        Assert.assertEquals("SP", pair.getKey());
        Assert.assertEquals("HNS", pair.getValue());
    }
}
