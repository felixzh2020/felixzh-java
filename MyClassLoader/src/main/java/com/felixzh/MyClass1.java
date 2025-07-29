package com.felixzh;

public class MyClass1 {
    public MyClass1() {
        System.out.println("2. Constructor Function: " + this.getClass().getName());
    }

    public static void hello() {
        System.out.println("2. hello: " + MyClass1.class.getName());
    }

    public void bye() {
        System.out.println("2. bye: " + MyClass1.class.getName());
    }

    private void good() {
        System.out.println("2. good: " + MyClass1.class.getName());
    }

}
