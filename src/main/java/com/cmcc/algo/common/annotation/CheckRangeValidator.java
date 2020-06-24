package com.cmcc.algo.common.annotation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class CheckRangeValidator implements ConstraintValidator<CheckRange, Integer> {
    private CheckRange checkRange;

    @Override
    public void initialize(CheckRange constraintAnnotation) {
        this.checkRange = constraintAnnotation;
    }

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext constraintContext) {
        if (value == null) {
            return true;
        }
        int[] values = checkRange.values();
        if (values.length == 0) {
            return true;
        }
        for (int v : values) {
            if (value == v) {
                return true;
            }
        }
        return false;
    }

}
