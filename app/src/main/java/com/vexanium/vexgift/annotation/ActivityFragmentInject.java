package com.vexanium.vexgift.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by mac on 11/16/17.
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface ActivityFragmentInject {
    int contentViewId() default -1;
    int toolbarTitle() default -1;
    int toolbarIndicator() default -1;
}
