package com.felixzh.jni;

/**
 * @author FelixZh
 * @desc JNI：Java Native Interface allows Java code to interact with native applications and libraries written in
 * languages like c or c++. JNI enables Java applications to call functions written in these languages, as well as
 * to be called by native code.
 */
public class HelloNative {
    static {
        //System.loadLibrary("FelixZhHelloNative");
        System.load("/opt/FelixZhHelloNative.so");
    }

    public static native void sayHello();

    public static void main(String[] args) {
        sayHello();
    }
}
