package com.github.socraticphoenix.metaprocessor;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
public @interface MetaDebug {

    Policy policy() default Policy.INFO;

    enum Policy {
        INFO,
        WARNING,
        ERROR
    }

}
