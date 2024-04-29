package com.boogipop.easyrasp.utils;

public interface BasicLogger {
    public void trace(Object... arg);

    public void warn(Object... arg);

    public void info(Object... arg);

    public void error(Object... arg);
}
