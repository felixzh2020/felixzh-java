package com.felixzh;

public class MyClass {
    public MyClass() {
        System.out.println("2. Constructor Function: " + this.getClass().getName());
    }

    public static void hello() {
        System.out.println("2. hello: " + MyClass.class.getName());
    }

    public void bye() {
        System.out.println("2. bye: " + MyClass.class.getName());
    }

    private void good() {
        System.out.println("2. good: " + MyClass.class.getName());
    }

    public static void main(String... args) {
        System.out.println(MyClass.class.getClassLoader().getClass());
        // class jdk.internal.loader.ClassLoaders$AppClassLoader

        System.out.println("===================================================");
        // 查看ClassLoader关系
        ClassLoader classLoader = MyClass.class.getClassLoader();
        while (classLoader != null) {
            System.out.println(classLoader);
            classLoader = classLoader.getParent();
        }
        /**
         * jdk8输出
         * class sun.misc.Launcher$AppClassLoader
         * ===================================================
         * sun.misc.Launcher$AppClassLoader@18b4aac2
         * sun.misc.Launcher$ExtClassLoader@4aa298b7
         *
         * 更高版本的已经不一样了，比如jdk22
         * class sun.misc.Launcher$AppClassLoader
         * ===================================================
         * sun.misc.Launcher$AppClassLoader@18b4aac2
         * sun.misc.Launcher$PlatformClassLoader@4aa298b7
         * */
    }
}
