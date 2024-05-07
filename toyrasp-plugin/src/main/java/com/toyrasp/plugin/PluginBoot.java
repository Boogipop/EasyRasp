package com.toyrasp.plugin;

import com.toyrasp.plugin.annotations.FrozenHook;
import com.toyrasp.plugin.annotations.PreMethodHook;
import com.toyrasp.plugin.annotations.RaspHook;
import com.toyrasp.plugin.transform.SimpleTransformer;
import com.toyrasp.plugin.utils.RaspLogger;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class PluginBoot {
    private static final RaspLogger logger = new RaspLogger();

    private static final boolean flag = false;

    static {
        logger.init();
        String banner = "\n" +
                "  ______                _____                                                                    \n" +
                " |  ____|              |  __ \\                                                                   \n" +
                " | |__   __ _ ___ _   _| |__) |__ _ ___ _ __                                                     \n" +
                " |  __| / _` / __| | | |  _  // _` / __| '_ \\                                                    \n" +
                " | |___| (_| \\__ \\ |_| | | \\ \\ (_| \\__ \\ |_) |                                                   \n" +
                " |______\\__,_|___/\\__, |_|  \\_\\__,_|___/ .__/                                                    \n" +
                "                   __/ |               | |                                                       \n" +
                "                _ |___/                |_|_          ____                    _                   \n" +
                "     /\\        | | | |                |  _ \\        |  _ \\                  (_)                  \n" +
                "    /  \\  _   _| |_| |__   ___  _ __  | |_) |_   _  | |_) | ___   ___   __ _ _ _ __   ___  _ __  \n" +
                "   / /\\ \\| | | | __| '_ \\ / _ \\| '__| |  _ <| | | | |  _ < / _ \\ / _ \\ / _` | | '_ \\ / _ \\| '_ \\ \n" +
                "  / ____ \\ |_| | |_| | | | (_) | |    | |_) | |_| | | |_) | (_) | (_) | (_| | | |_) | (_) | |_) |\n" +
                " /_/    \\_\\__,_|\\__|_| |_|\\___/|_|    |____/ \\__, | |____/ \\___/ \\___/ \\__, |_| .__/ \\___/| .__/ \n" +
                "                                              __/ |                     __/ | | |         | |    \n" +
                "                                             |___/                     |___/  |_|         |_|    \n";
        System.out.println(banner);
    }

    public static void init(String agentArgs, Instrumentation inst) throws UnmodifiableClassException, ClassNotFoundException {
        logger.info("Premain Start........");
        // 定义注解扫描器
        Reflections reflecs = new Reflections("com.toyrasp.plugin", Scanners.TypesAnnotated, Scanners.MethodsAnnotated);
        // 扫描应用了指定注解的类
        Set<Class<?>> hookSet = reflecs.getTypesAnnotatedWith(RaspHook.class);
        Set<Method> preMethodSet = reflecs.getMethodsAnnotatedWith(PreMethodHook.class);
        Set<Method> frozenMethodSet = reflecs.getMethodsAnnotatedWith(FrozenHook.class);
        List<HashMap<?, ?>> raspList = new ArrayList<>();
        ArrayList<Class<?>> targetClassList = new ArrayList<>();

        for (Class<?> handlerClass : hookSet) {
            String preMethodName;
            String frozenMethodName;
            HashMap<String, Object> map = new HashMap<>();
            RaspHook MonitorAnnotation = handlerClass.getAnnotation(RaspHook.class);

            for (Method method : preMethodSet) {
                if (method.getDeclaringClass() == handlerClass) {
                    preMethodName = method.getName();
                    map.put("preMethod", preMethodName);
                }
            }

            for (Method method : frozenMethodSet) {
                if (method.getDeclaringClass() == handlerClass) {
                    frozenMethodName = method.getName();
                    map.put("frozenMethod", frozenMethodName);
                }
            }

            boolean isIgnore = MonitorAnnotation.ignoreOverloading();
            boolean isCons = MonitorAnnotation.isConstructor();
            Class<?> targetClass = MonitorAnnotation.hookClass();
            Class<?>[] paramType = MonitorAnnotation.paramTypes();
            boolean isFrozen = MonitorAnnotation.isFrozen();
            String methodName = MonitorAnnotation.methodName();
            // 加载目标类，防止 agent Javassits 无法找到
            // PluginBoot.class.getClassLoader().loadClass(targetClass.getName());
            map.put("hookClass", targetClass);
            map.put("raspClass", handlerClass);
            map.put("isCons", isCons);
            map.put("isIgnore", isIgnore);
            map.put("paramType", paramType);
            map.put("methodName", methodName);
            map.put("isFrozen", isFrozen);
            raspList.add(map);
            targetClassList.add(targetClass);
        }

        SimpleTransformer simpleTransformer = new SimpleTransformer(raspList);
        inst.addTransformer(simpleTransformer, true);
        for (Class<?> targetClass : targetClassList) {
            inst.retransformClasses(targetClass);
        }
    }
}
