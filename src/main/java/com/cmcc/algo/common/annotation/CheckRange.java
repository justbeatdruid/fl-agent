package com.cmcc.algo.common.annotation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CheckRangeValidator.class)
@Documented
public @interface CheckRange {

    // @Constraint要求必须有以下三个方法
    String message() default "'${validatedValue}' not int {values}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    // 以下方法为其他自定义方法
    int[] values() default {};
}
