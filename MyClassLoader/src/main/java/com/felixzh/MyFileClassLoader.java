package com.felixzh;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * @author felixzh
 * 自定义FileClassLoader用于从文件系统加载指定类
 */
public class MyFileClassLoader extends ClassLoader {
    private final String classDir;

    public MyFileClassLoader(String classDir) {
        this.classDir = classDir;
    }

    // 可指定父类加载器（默认使用系统类加载器）
    public MyFileClassLoader(String classDir, ClassLoader parent) {
        super(parent);
        this.classDir = classDir;
    }

    @Override
    protected Class<?> findClass(String className) throws ClassNotFoundException {
        // 根据类名获取字节码文件路径
        String path = classDir + File.separator + className.replace(".", File.separator) + ".class";
        System.out.println("path=" + path);

        // 读取字节码文件
        try (FileInputStream fis = new FileInputStream(path);
             ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                bos.write(buffer, 0, bytesRead);
            }
            byte[] classBytes = bos.toByteArray();

            // 调用defineClass方法将字节码转换为 Class 对象
            return defineClass(className, classBytes, 0, classBytes.length);
        } catch (IOException e) {
            throw new ClassNotFoundException("Class not found: " + className, e);
        }
    }
}
