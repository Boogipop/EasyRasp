package com.toyrasp.plugin.annotations;

import java.lang.annotation.*;

/**
 * FrozenMethod Monitor
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FrozenHook {
}
