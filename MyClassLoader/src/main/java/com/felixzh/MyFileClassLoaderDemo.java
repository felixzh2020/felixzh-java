package com.felixzh;

import java.lang.reflect.Method;

/**
 * @author felixzh
 * 注意：所谓双亲委派机制 父类加载器(Parent ClassLoader)是一个逻辑概念，而非继承关系中的父类(即extends关系)。
 * ExtClassLoader是AppClassLoader的父类加载器，是通过委派链(Delegation Chain)建立的层级关系，而非类的继承关系。
 * JDK源码中的sun.misc.Launcher类负责初始化类加载器，从关键代码来看，ExtClassLoader的实例被显示传递为AppClassLoader的构造方法参数，建立了父子委派关系：
 *
 *   public Launcher() {
 *         // Create the extension class loader
 *         ClassLoader extcl;
 *         try {
 *             extcl = ExtClassLoader.getExtClassLoader();
 *         } catch (IOException e) {
 *             throw new InternalError(
 *                 "Could not create extension class loader", e);
 *         }
 *
 *         // Now create the class loader to use to launch the application
 *         try {
 *             loader = AppClassLoader.getAppClassLoader(extcl);
 *
 *   .......
 *
 *   return new AppClassLoader(urls, extcl);
 *
 *
 *   而 Bootstrap ClassLoader是C++实现的，JVM内置
 *
 * */
public class MyFileClassLoaderDemo {
    public static void main(String[] args) throws Exception {
        // 方式1 默认使用系统类加载器 AppClassLoader 为父加载器，使用双亲委派机制
        MyFileClassLoader myFileClassLoader = new MyFileClassLoader("c:/MyClass");

        // 方式2 不使用系统类加载器为父加载器，打破双亲委派机制
        //MyFileClassLoader myFileClassLoader = new MyFileClassLoader("c:/MyClass", null);

        // 加载类
        Class<?> clazz = myFileClassLoader.loadClass("com.felixzh.MyClass");

        Object instance = clazz.getDeclaredConstructor().newInstance();

        // public 静态方法
        Method method = clazz.getMethod("hello");
        method.invoke(instance);

        // private 对象方法
        Method method2 = clazz.getDeclaredMethod("good");
        method2.setAccessible(true);
        method2.invoke(instance);

        // public 对象方法
        Method method1 = clazz.getDeclaredMethod("bye");
        method1.invoke(instance);
    }
}
