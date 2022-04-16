package com.mt.data.repository.anotation;

import java.lang.annotation.*;

@Documented
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface TableJoinType {
    JoinTypeDetail[] joinTypeDetails(); 
}
