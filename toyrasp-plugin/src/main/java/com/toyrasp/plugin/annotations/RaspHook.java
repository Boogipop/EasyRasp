package com.toyrasp.plugin.annotations;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RaspHook {
    // 目标拦截类
    Class<?> hookClass();

    // 目标拦截方法
    String methodName() default "";

    // 拦截方法是否为构造方法
    boolean isConstructor() default false;

    // 是否忽略方法重载（无视参数，拦截所有同名方法）
    boolean ignoreOverloading() default false;

    // 目标拦截方法参数类型
    Class<?>[] paramTypes();

    boolean isFrozen() default false;
}
