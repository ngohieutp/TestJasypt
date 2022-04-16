package com.mt.data.repository.support;

public class EntityGraphOption {

    private String name;
    private EntityGraphType entityGraphType;

    public EntityGraphOption(String name, EntityGraphType entityGraphType) {
        this.name = name;
        this.entityGraphType = entityGraphType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public EntityGraphType getEntityGraphType() {
        return entityGraphType;
    }

    public void setEntityGraphType(EntityGraphType entityGraphType) {
        this.entityGraphType = entityGraphType;
    }
}
