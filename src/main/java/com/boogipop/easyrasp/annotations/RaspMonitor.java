package com.boogipop.easyrasp.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface RaspMonitor {
    //目标拦截类
    Class targetClass();
    //目标拦截方法
    String methodName() default "";

    //拦截方法是否为构造方法
    boolean isConstructor() default false;
    //是否忽略方法重载（无视参数，拦截所有同名方法）
    boolean IgnoreOverloading() default false;
    //目标拦截方法参数类型
    Class[] paramTypes();

    boolean isFrozen() default false;

}
