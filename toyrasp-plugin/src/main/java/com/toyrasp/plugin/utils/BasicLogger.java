package com.toyrasp.plugin.utils;

public interface BasicLogger {
    void trace(Object... arg);

    void warn(Object... arg);

    void info(Object... arg);

    void error(Object... arg);
}
