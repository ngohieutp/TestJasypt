package com.mt.data.repository.anotation;

import com.mt.data.enums.Operator;
import com.mt.data.enums.OperatorLogical;

import java.lang.annotation.*;

@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface FindByReference {
    String tableName() default "";

    String altField() default "";

    Operator operator() default Operator.DEFAULT;

    OperatorLogical logicalOperator() default OperatorLogical.AND;

}
