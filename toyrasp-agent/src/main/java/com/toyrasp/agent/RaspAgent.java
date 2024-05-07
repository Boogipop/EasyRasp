package com.toyrasp.agent;

import com.toyrasp.agent.classloader.RaspPluginClassLoader;

import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Logger;

public class RaspAgent {
    public static final Logger LOGGER = Logger.getLogger(RaspAgent.class.getName());

    public static final String PLUGIN_JAR_PATH = "file://" + System.getProperty("PLUGIN_JAR_PATH");

    public static void premain(String agentArgs, Instrumentation inst) throws NoSuchMethodException, ClassNotFoundException, IOException, InvocationTargetException, IllegalAccessException {
        LOGGER.info("RASP premain start");
        try (RaspPluginClassLoader pluginClassLoader = new RaspPluginClassLoader(PLUGIN_JAR_PATH)) {
            Class<?> pluginBoot = Class.forName("com.toyrasp.plugin.PluginBoot", true, pluginClassLoader);
            pluginBoot.getMethod("init", String.class, Instrumentation.class).invoke(pluginBoot, agentArgs, inst);
        }
    }

    public static void agentmain(String agentArgs, Instrumentation inst) {
        // TODO
    }
}
