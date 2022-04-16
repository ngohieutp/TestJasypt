package com.mt.data;

public class Constants {
    public final static int PAGING_INDEX = 1;
    public final static int PAGING_SIZE = 20;
    public final static int MAX_RETRY = 3;

    public static final String QUERY_SELECT = "Select e";
    public static final String QUERY_SELECT_DISTINCT = "Select Distinct e";
    public static final String QUERY_FROM_AND_WHERE_FORMAT = " From %s e Where 1=1 ";
    public static final String QUERY_FROM_AND_FETCH_JOIN = " From %s e LEFT JOIN FETCH e.%s ";
    public static final String DELETE_MULTI_FORMAT = "Delete From %s e Where e.%s in ?1";
    public static final String UPDATE_MULTI_FORMAT = "Update %s Set %s = ?1 Where %s in ?2";
    public static final String QUERY_OPERATION_EQ = "=";

    public static final String QUERY_OPERATION_LIKE = "LIKE";
    public static final String QUERY_OPERATION_EQUAL = "=";
    public static final String QUERY_OPERATION_IN = "IN";
    public static final String QUERY_ORDER = " Order by e.%s %s ";
    public static final String QUERY_APPEND_CONDITION = " AND e.%s %s :%s ";
    public static final String QUERY_APPEND_CONDITION_IGNORE_CASE = " AND lower(e.%s) %s lower(:%s) ";
    public static final String QUERY_APPEND_OR_CONDITION = " OR e.%s %s :%s ";
    public static final String QUERY_FORMAT_FIND_BY_FIELD = "Select e from %s e Where e.%s %s ?1";
    public static final String QUERY_FORMAT_FIND_BY_FIELD_IGNORE_CASE = "Select e from %s e Where lower(e.%s) %s lower(?1)";
    public static final String QUERY_FORMAT_ADDITION_SENDER = " AND e.status IN :status AND e.triedTime < :triedTime ";

    public static final String STRING_EMPTY = "";
    public static final String STRING_PERCENT = "%";
    public static final String STRING_DOT = ".";

    /**
     * Defined customize string value
     */
    public static final String QUERY_VALUE_LIKE_CONTAIN = "%s%s%s";
    public static final String QUERY_FIELD_BY_FK = "%s.%s";

    public static final String ROLE_HO = "HO";
    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_EKYC_HO = "EKYC_HO";
    public static final String ROLE_EKYC_DVTK = "EKYC_DVTK";
    public static final String ROLE_EKYC_DVTK247 = "EKYC_DVTK247";
    public static final String ROLE_OD_HO = "OD_HO";
    public static final String ROLE_GNHM_HO = "GNHM_HO";
    public static final String ROLE_AUTO_HO = "AUTO_HO";
    public static final String ROLE_OD_CGPD = "OD_CGPD";
    public static final String ROLE_OD_SALE = "OD_SALE";
    public static final String ROLE_CUSTOMER = "CUSTOMER";
    public static final String ROLE_DEALER = "DEALER";
    public static final String ROLE_CSO = "CSO";
    public static final String ROLE_BMO = "BMO";

    public static final String FULLNAME_COMMIT_KEY = "fullName";
    public static final String USERNAME_COMMIT_KEY = "username";

    public static final String APPLICANT_ACCOUNT_TYPE = "1001";
    public static final String VIETNAM = "Việt Nam";

    public static final String ROLE_SALE = "SALE";

    public static final String ROLE_MKT = "MKT";

}
