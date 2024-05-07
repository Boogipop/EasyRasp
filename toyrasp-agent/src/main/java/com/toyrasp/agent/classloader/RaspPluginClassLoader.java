package com.toyrasp.agent.classloader;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.logging.Logger;

public class RaspPluginClassLoader extends URLClassLoader {
    private static final Logger logger = Logger.getLogger(RaspPluginClassLoader.class.getName());

    public RaspPluginClassLoader(String pluginJarPath) throws MalformedURLException {
        super(new URL[]{new URL(pluginJarPath)}, Thread.currentThread().getContextClassLoader());
    }

    // 破坏双亲委派机制。优先尝试自己加载目标类，加载失败再调用父 ClassLoader。
    // 据此在 ClassLoader 级别隔离 RASP 插件代码与业务代码各自的依赖。
    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        Class<?> c = findLoadedClass(name);
        if (c != null) return c;

        try {
            c = findClass(name);
            logger.info("Plugin Class Loaded -> " + name + " | ClassLoader -> " + c.getClassLoader());
        } catch (ClassNotFoundException e) {
            c = getParent().loadClass(name);
            logger.info("Parent Class Loaded -> " + name + " | ClassLoader -> " + c.getClassLoader());
        }

        return c;
    }
}
