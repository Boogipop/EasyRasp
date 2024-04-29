package com.boogipop.easyrasp.transformers;

import com.boogipop.easyrasp.utils.RaspLogger;
import com.boogipop.easyrasp.utils.ReflectionsUtil;
import javassist.ClassPool;
import javassist.CtBehavior;
import javassist.CtClass;
import javassist.CtMethod;
import org.reflections.Reflections;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class SimpleTransformer implements ClassFileTransformer {

    private List<HashMap> raspList;
    private static RaspLogger logger=new RaspLogger();
    public SimpleTransformer(List<HashMap> raspList) {
        this.raspList = raspList;
    }


    /**
     *
     * @param loader                the defining loader of the class to be transformed,
     *                              may be <code>null</code> if the bootstrap loader
     * @param className             the name of the class in the internal form of fully
     *                              qualified class and interface names as defined in
     *                              <i>The Java Virtual Machine Specification</i>.
     *                              For example, <code>"java/util/List"</code>.
     * @param classBeingRedefined   if this is triggered by a redefine or retransform,
     *                              the class being redefined or retransformed;
     *                              if this is a class load, <code>null</code>
     * @param protectionDomain      the protection domain of the class being defined or redefined
     * @param classfileBuffer       the input byte buffer in class file format - must not be modified
     *
     * @return
     * @throws IllegalClassFormatException
     */
    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        String formatName = className.replace("/", ".");
        for (HashMap raspMap : raspList) {
            try {
                //识别targetClass
                Class targetClass = (Class) raspMap.get("targetClass");
                if(targetClass.getName().equals(formatName)){
                    //基本信息获取
                    logger.info("found target class: "+formatName);
                    Class raspClass = (Class) raspMap.get("raspClass");
                    boolean isCons = (boolean) raspMap.get("isCons");
                    String preMethod = (String) raspMap.get("preMethod");
                    String frozenMethod = (String) raspMap.get("frozenMethod");
                    boolean isIgnore = (boolean) raspMap.get("isIgnore");
                    boolean isFrozen = (boolean) raspMap.get("isFrozen");
                    String methodName = (String) raspMap.get("methodName");
                    Class[] paramType = (Class[]) raspMap.get("paramType");
                    ClassPool classPool = ClassPool.getDefault();
                    CtClass ctClass = classPool.get(formatName);
                    //获取param的ctClass
                    CtClass[] paramCtClass = new CtClass[paramType.length];
                    for(int i=0;i<paramType.length;i++){
                        paramCtClass[i]=classPool.get(paramType[i].getName());
                    }
                    //初始化behavior
                    CtBehavior ctBehavior;
                    if(isCons){
                        //获取构造的behavior
                       ctBehavior=ctClass.getDeclaredConstructor(paramCtClass);
                    }else {
                        if(isIgnore){
                            //无视重载
                            if(isFrozen){
                                //使用FrozenMethod
                                CtMethod[] declaredMethods = ctClass.getDeclaredMethods(methodName);
                                for (CtMethod declaredMethod : declaredMethods) {
                                        ctBehavior=declaredMethod;
                                        ctBehavior.insertBefore(String.format(ReflectionsUtil.getSource(raspClass,"FrozenSource"),raspClass.getName(),frozenMethod));
                                }
                            }
                            else {
                                //使用PreMethod
                                CtMethod[] declaredMethods = ctClass.getDeclaredMethods(methodName);
                                for (CtMethod declaredMethod : declaredMethods) {
                                    ctBehavior=declaredMethod;
                                    ctBehavior.insertBefore(String.format(ReflectionsUtil.getSource(raspClass,"HookSource"),raspClass.getName(),preMethod));
                                }

                            }
                        }
                        else {
                            //指定某种方法
                            ctBehavior=ctClass.getDeclaredMethod(methodName,paramCtClass);
                            if(isFrozen){
                                //frozenMethod
                                ctBehavior.insertBefore(String.format(ReflectionsUtil.getSource(raspClass,"FrozenSource"),raspClass.getName(),frozenMethod));

                            }else {
                                //preMethod
                                ctBehavior.insertBefore(String.format(ReflectionsUtil.getSource(raspClass,"HookSource"),raspClass.getName(),preMethod));

                            }

                        }
                    }
                    ctClass.detach();
                    //返回修改后的bytecode
                    return ctClass.toBytecode();
                }
            } catch (Exception e) {
                logger.error("transform error ",e);
            }
        }
        return classfileBuffer;
    }



    public void setRaspList(List<HashMap> raspList) {
        this.raspList = raspList;
    }
}
