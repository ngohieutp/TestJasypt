package com.mt.data.enums;

public enum Status {
    INACTIVE("Không hoạt động"), ACTIVE("Hoạt động");
    private String name;

    Status(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
