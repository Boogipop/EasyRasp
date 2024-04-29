# EasyRasp
Easy Java-Rasp ，一款简单的轻量级Java-Rasp框架🔥

<img width="1006" alt="image" src="https://github.com/Boogipop/EasyRasp/assets/114604850/5e847c05-57be-45c8-b0af-286931ec1298">

示例代码：

<img width="1559" alt="image" src="https://github.com/Boogipop/EasyRasp/assets/114604850/5db25a3b-8b4c-4fb8-9f28-6cc74c24e1d8">

拦截结果：

<img width="1092" alt="image" src="https://github.com/Boogipop/EasyRasp/assets/114604850/272c03f0-57a7-435a-961a-571b88334bcf">

你可以通过注解RaspMonitor来自定义Handler，例如拦截Runtime

```java
package com.boogipop.easyrasp.monitors;


import com.boogipop.easyrasp.annotations.FrozenMonitor;
import com.boogipop.easyrasp.annotations.PreMethodMonitor;
import com.boogipop.easyrasp.annotations.RaspMonitor;


/**
 * Not finished yet,only hook one single exec method which's type is String.class
 */
@RaspMonitor(targetClass = Runtime.class,isConstructor = false,methodName = "exec",paramTypes ={String.class},isFrozen = true,IgnoreOverloading = true)
public class RuntimeMethodMonitor implements BasicMonitors{
    /**
     * modify the blacklist by yourself XD
     */
    private static String [] blacklist={"test","test"};
    /**
     * Method Name can not be changed
     * Will be changed in next version
     * Will add Source Annotiations
     */

    @Deprecated
    private static String FrozenSource="new %s().%s();";
    @Deprecated
    private static String HookSource="new %s($args).%s();";

    @Override
    public void HookMethod(Object... args) {

    }

    @Override
    @PreMethodMonitor
    public void HookMethod(String cmd) {
        for (String banword : blacklist) {
            if (cmd.contains(banword)){
                throw new RuntimeException("Evil Command Detected!!!");
            }
        }
    }

    @Override
    public void HookMethod() {

    }

    @Override
    @FrozenMonitor
    public void FrozenMethod() {
        throw new RuntimeException("Runtime.exec() methods have been hooked!");
    }
}
```
