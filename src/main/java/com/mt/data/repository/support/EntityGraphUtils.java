package com.mt.data.repository.support;

public class EntityGraphUtils {

    public static EntityGraphOption fetchEntityGrapch(String name) {
        return new EntityGraphOption(name, EntityGraphType.FETCH);
    }

    public static EntityGraphOption loadEntityGrapch(String name) {
        return new EntityGraphOption(name, EntityGraphType.LOAD);
    }
}
