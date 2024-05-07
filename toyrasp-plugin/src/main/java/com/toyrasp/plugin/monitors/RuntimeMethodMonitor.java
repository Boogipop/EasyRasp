package com.toyrasp.plugin.monitors;

import com.toyrasp.plugin.annotations.FrozenHook;
import com.toyrasp.plugin.annotations.PreMethodHook;
import com.toyrasp.plugin.annotations.RaspHook;

/**
 * Not finished yet,only hook one single exec method which's type is String.class
 */
@RaspHook(hookClass = Runtime.class, methodName = "exec", paramTypes = {
        String.class }, isFrozen = true, ignoreOverloading = true)
public class RuntimeMethodMonitor implements BasicMonitors {
    /**
     * modify the blacklist by yourself XD
     */
    private static final String[] blacklist = { "test", "test" };
    /**
     * Method Name can not be changed
     * Will be changed in next version
     * Will add Source Annotiations
     */

    @Deprecated
    private static final String FrozenSource = "new %s().%s();";
    @Deprecated
    private static final String HookSource = "new %s($args).%s();";

    @Override
    public void HookMethod(Object... args) {

    }

    @Override
    @PreMethodHook
    public void HookMethod(String cmd) {
        for (String banword : blacklist) {
            if (cmd.contains(banword)) {
                throw new RuntimeException("Evil Command Detected!!!");
            }
        }
    }

    @Override
    public void HookMethod() {

    }

    @Override
    @FrozenHook
    public void FrozenMethod() {
        throw new RuntimeException("Runtime.exec() methods have been hooked!");
    }
}
