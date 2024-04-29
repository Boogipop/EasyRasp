package com.boogipop.easyrasp;

import com.boogipop.easyrasp.annotations.FrozenMonitor;
import com.boogipop.easyrasp.annotations.PreMethodMonitor;
import com.boogipop.easyrasp.annotations.RaspMonitor;
import com.boogipop.easyrasp.transformers.SimpleTransformer;
import com.boogipop.easyrasp.utils.RaspLogger;
import org.reflections.Reflections;

import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.jar.JarFile;

import static org.reflections.scanners.Scanners.MethodsAnnotated;
import static org.reflections.scanners.Scanners.TypesAnnotated;

public class AgentBoot {
    private static RaspLogger logger=new RaspLogger();

    private static boolean flag=false;

    static {
        logger.init();
        String banner="\n" +
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

    public static void main(String[] args) {

    }

    /**
     *
     * @param agentArgs init param
     * @param inst
     */
    public static void premain(String agentArgs, Instrumentation inst) throws IOException, ClassNotFoundException, UnmodifiableClassException {
        logger.info("Premain Start........");
        //解决类加载问题，双亲委派
        String jarPath = AgentBoot.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        inst.appendToBootstrapClassLoaderSearch(new JarFile(jarPath));
        //扫描所有注解
        Reflections reflections = new Reflections("com.boogipop.easyrasp", TypesAnnotated, MethodsAnnotated);
        //获取所有标识注解的类
        Set<Class<?>> classSet = reflections.getTypesAnnotatedWith(RaspMonitor.class);
        //获取pre注解下的所有Method
        Set<Method> preMethodSet = reflections.getMethodsAnnotatedWith(PreMethodMonitor.class);
        //获取所有frozenMethod注解的method
        Set<Method> frozenMethodSet = reflections.getMethodsAnnotatedWith(FrozenMonitor.class);
        List raspList = new ArrayList();
        ArrayList<Class> targetClassList = new ArrayList<>();

        for (Class<?> handlerClass : classSet) {
            String preMethodName;
            String frozenMethodName;
            HashMap map = new HashMap();
            RaspMonitor MonitorAnnotation = handlerClass.getAnnotation(RaspMonitor.class);

            for (Method method : preMethodSet) {
                if (method.getDeclaringClass()==handlerClass){
                    preMethodName=method.getName();
                    map.put("preMethod",preMethodName);

                }
            }

            for (Method method : frozenMethodSet) {
                if(method.getDeclaringClass()==handlerClass){
                    frozenMethodName=method.getName();
                    map.put("frozenMethod",frozenMethodName);
                }
            }

            boolean isIgnore = MonitorAnnotation.IgnoreOverloading();
            boolean isCons = MonitorAnnotation.isConstructor();
            Class targetClass = MonitorAnnotation.targetClass();
            Class[] paramType = MonitorAnnotation.paramTypes();
            boolean isFrozen = MonitorAnnotation.isFrozen();
            String methodName = MonitorAnnotation.methodName();
            //加载目标类，防止agent Javassits无法找到
            Thread.currentThread().getContextClassLoader().loadClass(targetClass.getName());
            map.put("targetClass",targetClass);
            map.put("raspClass",handlerClass);
            map.put("isCons",isCons);
            map.put("isIgnore",isIgnore);
            map.put("paramType",paramType);
            map.put("methodName",methodName);
            map.put("isFrozen",isFrozen);
            raspList.add(map);
            targetClassList.add(targetClass);
        }

        SimpleTransformer simpleTransformer = new SimpleTransformer(raspList);
        inst.addTransformer(simpleTransformer,true);
        for (Class targetClass : targetClassList) {
                inst.retransformClasses(targetClass);
            }
     }

    /**
     *
     * @param agentArgs init param
     * @param inst
     */
    public static void agentmain(String agentArgs, Instrumentation inst){

    }
}

