package com.mt.data.enums;

public enum Gender {
    FEMALE("F", "Nữ", "1", "WOS1"), MALE("M", "Nam", "0", "MOS");

    private String code;
    private String name;
    private String b2bValue;
    private String cifValue;

    Gender(String code, String name, String b2bValue, String cifValue) {
        this.code = code;
        this.name = name;
        this.b2bValue = b2bValue;
        this.cifValue = cifValue;
    }

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

    public String getB2bValue() {
        return b2bValue;
    }

    public void setB2bValue(String b2bValue) {
        this.b2bValue = b2bValue;
    }

    public String getCifValue() {
        return cifValue;
    }

    public void setCifValue(String cifValue) {
        this.cifValue = cifValue;
    }

}
