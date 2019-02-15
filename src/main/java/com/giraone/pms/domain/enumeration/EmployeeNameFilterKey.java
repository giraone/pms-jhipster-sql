package com.giraone.pms.domain.enumeration;

/**
 * The string search mode enumeration.
 */
public enum EmployeeNameFilterKey {

    /** Surname lowercase */
    LS,
    /** Surname fully normalized */
    NS,
    /** Surname phonetic */
    PS,
    /** Given name lowercase */
    LG,
    /** Given name fully normalized */
    NG,
    /** Given name phonetic */
    PG
}
