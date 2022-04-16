package com.mt.data.repository.anotation;

import javax.persistence.criteria.JoinType;
import java.lang.annotation.*;

@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface JoinTypeDetail {
    String table();

    JoinType joinType() default JoinType.INNER;
}
