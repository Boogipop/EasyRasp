package com.boogipop.easyrasp.monitors;

/**
 * BasicMonitor
 */
public interface BasicMonitors {
    //无返回值，纯检测逻辑
    void HookMethod(Object ...args);
    void HookMethod(String cmd);
    void HookMethod();
    void FrozenMethod();
}
