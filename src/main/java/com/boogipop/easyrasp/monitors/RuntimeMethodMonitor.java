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
