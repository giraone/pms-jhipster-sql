package com.giraone.pms.domain;

import com.giraone.pms.domain.enumeration.StringSearchMode;
import com.giraone.pms.domain.filter.EmployeeFilter;
import com.giraone.pms.domain.filter.EmployeeFilterPair;
import com.giraone.pms.service.NameNormalizeService;
import com.giraone.pms.service.impl.NameNormalizeServiceImpl;
import org.junit.Assert;
import org.junit.Test;

public class EmployeeFilterTest {

    private NameNormalizeService nameNormalizeService = new NameNormalizeServiceImpl();

    @Test
    public void buildQueryValueNull() {

        EmployeeFilter filter = new EmployeeFilter();
        EmployeeFilterPair pair = filter.buildQueryValue(nameNormalizeService);
        Assert.assertNull(pair);
    }

    @Test
    public void buildQueryValue_EXACT() {

        EmployeeFilter filter = new EmployeeFilter("Heinz", StringSearchMode.EXACT);
        EmployeeFilterPair pair = filter.buildQueryValue(nameNormalizeService);
        Assert.assertNull(pair.getKey());
        Assert.assertEquals("Heinz", pair.getValue());
    }

    @Test
    public void buildQueryValue_PREFIX() {

        EmployeeFilter filter = new EmployeeFilter("Heinz", StringSearchMode.PREFIX);
        EmployeeFilterPair pair = filter.buildQueryValue(nameNormalizeService);
        Assert.assertNull(pair.getKey());
        Assert.assertEquals("Heinz", pair.getValue());
    }

    @Test
    public void buildQueryValue_LOWERCASE() {

        EmployeeFilter filter = new EmployeeFilter("Heinz", StringSearchMode.LOWERCASE);
        EmployeeFilterPair pair = filter.buildQueryValue(nameNormalizeService);
        Assert.assertEquals("SL", pair.getKey());
        Assert.assertEquals("heinz", pair.getValue());
    }

    @Test
    public void buildQueryValue_PREFIX_LOWERCASE() {

        EmployeeFilter filter = new EmployeeFilter("Heinz", StringSearchMode.PREFIX_LOWERCASE);
        EmployeeFilterPair pair = filter.buildQueryValue(nameNormalizeService);
        Assert.assertEquals("SL", pair.getKey());
        Assert.assertEquals("heinz%", pair.getValue());
    }

    @Test
    public void buildQueryValue_REDUCED() {

        EmployeeFilter filter = new EmployeeFilter("Thiel", StringSearchMode.REDUCED);
        EmployeeFilterPair pair = filter.buildQueryValue(nameNormalizeService);
        Assert.assertEquals("SN", pair.getKey());
        Assert.assertEquals("til", pair.getValue());
    }

    @Test
    public void buildQueryValue_PREFIX_REDUCED() {

        EmployeeFilter filter = new EmployeeFilter("Thiel", StringSearchMode.PREFIX_REDUCED);
        EmployeeFilterPair pair = filter.buildQueryValue(nameNormalizeService);
        Assert.assertEquals("SN", pair.getKey());
        Assert.assertEquals("til%", pair.getValue());
    }

    @Test
    public void buildQueryValuePhonetic() {

        EmployeeFilter filter = new EmployeeFilter("Heinz", StringSearchMode.PHONETIC);
        EmployeeFilterPair pair = filter.buildQueryValue(nameNormalizeService);
        Assert.assertEquals("SP", pair.getKey());
        Assert.assertEquals("HNS", pair.getValue());
    }
}
