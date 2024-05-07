package com.toyrasp.plugin.annotations;

import java.lang.annotation.*;

/**
 * PreMethod Monitor
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PreMethodHook {
}
