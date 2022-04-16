package com.mt.data.enums;

public enum YesNo {
    NO("N", "Unchecked", "0"), YES("Y", "Checked", "1");

    private String code;
    private String name;
    private String msaValue;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMsaValue() {
        return msaValue;
    }

    public void setMsaValue(String msaValue) {
        this.msaValue = msaValue;
    }

    YesNo(String code, String name, String msaValue) {
        this.code = code;
        this.name = name;
        this.msaValue = msaValue;
    }

    public static YesNo fromCode(String code) {
        if ("Y".equalsIgnoreCase(code)) return YesNo.YES;
        if ("N".equalsIgnoreCase(code)) return YesNo.NO;
        return null;
    }

    public static YesNo getEnum(String value) {
        return YesNo.valueOf(value);
    }

}
