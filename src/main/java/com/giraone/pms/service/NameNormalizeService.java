package com.giraone.pms.service;

import java.util.List;

public interface NameNormalizeService {

    public List<String> normalize(String name);
    public String normalizeSingleName(String name);
    public String normalizePhoneticSingleName(String name);
}
