package com.toyrasp.plugin.transform;

import com.toyrasp.plugin.utils.RaspLogger;
import com.toyrasp.plugin.utils.ReflectionUtil;
import javassist.ClassPool;
import javassist.CtBehavior;
import javassist.CtClass;
import javassist.CtMethod;

import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;
import java.util.HashMap;
import java.util.List;

public class SimpleTransformer implements ClassFileTransformer {
    private List<HashMap<?, ?>> raspList;
    private static final RaspLogger logger = new RaspLogger();

    public SimpleTransformer(List<HashMap<?, ?>> raspList) {
        this.raspList = raspList;
    }

    /**
     * @param loader              the defining loader of the class to be
     *                            transformed,
     *                            may be <code>null</code> if the bootstrap loader
     * @param className           the name of the class in the internal form of
     *                            fully
     *                            qualified class and interface names as defined in
     *                            <i>The Java Virtual Machine Specification</i>.
     *                            For example, <code>"java/util/List"</code>.
     * @param classBeingRedefined if this is triggered by a redefine or retransform,
     *                            the class being redefined or retransformed;
     *                            if this is a class load, <code>null</code>
     * @param protectionDomain    the protection domain of the class being defined
     *                            or redefined
     * @param classfileBuffer     the input byte buffer in class file format - must
     *                            not be modified
     * @return
     */
    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain, byte[] classfileBuffer) {
        String formatName = className.replace("/", ".");
        for (HashMap<?, ?> raspMap : raspList) {
            try {
                // 识别 targetClass
                Class<?> targetClass = (Class<?>) raspMap.get("hookClass");
                if (targetClass.getName().equals(formatName)) {
                    // 基本信息获取
                    logger.info("found target class: " + formatName);
                    Class<?> raspClass = (Class<?>) raspMap.get("raspClass");
                    boolean isCons = (boolean) raspMap.get("isCons");
                    String preMethod = (String) raspMap.get("preMethod");
                    String frozenMethod = (String) raspMap.get("frozenMethod");
                    boolean isIgnore = (boolean) raspMap.get("isIgnore");
                    boolean isFrozen = (boolean) raspMap.get("isFrozen");
                    String methodName = (String) raspMap.get("methodName");
                    Class<?>[] paramType = (Class<?>[]) raspMap.get("paramType");
                    ClassPool classPool = ClassPool.getDefault();
                    classPool.insertClassPath(System.getProperty("PLUGIN_JAR_PATH"));
                    CtClass ctClass = classPool.get(formatName);
                    // 获取 param 的 ctClass
                    CtClass[] paramCtClass = new CtClass[paramType.length];
                    for (int i = 0; i < paramType.length; i++) {
                        paramCtClass[i] = classPool.get(paramType[i].getName());
                    }
                    // 初始化 behavior
                    CtBehavior ctBehavior;
                    if (isCons) {
                        // 获取构造的 behavior
                        ctBehavior = ctClass.getDeclaredConstructor(paramCtClass);
                    } else {
                        if (isIgnore) {
                            // 无视重载
                            if (isFrozen) {
                                // 使用 FrozenMethod
                                CtMethod[] declaredMethods = ctClass.getDeclaredMethods(methodName);
                                for (CtMethod declaredMethod : declaredMethods) {
                                    ctBehavior = declaredMethod;
                                    ctBehavior.insertBefore(
                                            String.format(ReflectionUtil.getSource(raspClass, "FrozenSource"),
                                                    raspClass.getName(), frozenMethod));
                                }
                            } else {
                                // 使用 PreMethod
                                CtMethod[] declaredMethods = ctClass.getDeclaredMethods(methodName);
                                for (CtMethod declaredMethod : declaredMethods) {
                                    ctBehavior = declaredMethod;
                                    ctBehavior.insertBefore(
                                            String.format(ReflectionUtil.getSource(raspClass, "HookSource"),
                                                    raspClass.getName(), preMethod));
                                }

                            }
                        } else {
                            // 指定某种方法
                            ctBehavior = ctClass.getDeclaredMethod(methodName, paramCtClass);
                            if (isFrozen) {
                                // frozenMethod
                                ctBehavior.insertBefore(
                                        String.format(ReflectionUtil.getSource(raspClass, "FrozenSource"),
                                                raspClass.getName(), frozenMethod));

                            } else {
                                // preMethod
                                ctBehavior
                                        .insertBefore(String.format(ReflectionUtil.getSource(raspClass, "HookSource"),
                                                raspClass.getName(), preMethod));

                            }

                        }
                    }
                    ctClass.detach();
                    // 返回修改后的bytecode
                    return ctClass.toBytecode();
                }
            } catch (Exception e) {
                logger.error("transform error ", e);
            }
        }
        return classfileBuffer;
    }

    public void setRaspList(List<HashMap<?, ?>> raspList) {
        this.raspList = raspList;
    }
}
