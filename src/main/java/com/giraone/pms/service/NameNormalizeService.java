package com.giraone.pms.service;

import java.util.List;

public interface NameNormalizeService {

    List<String> split(String name);
    String normalize(String name);
    String reduceSimplePhonetic(String name);
    String phonetic(String name);
}
