import org.codehaus.janino.SimpleCompiler;

import java.nio.file.Files;
import java.nio.file.Paths;

public class DynamicClassCase {
    public static void main(String[] args) throws Exception {
        String javaFilePath = "D:\\idea\\felixzh-java\\Janino\\src\\main\\java\\DynamicClass.java";
        String javaFileContent = new String(Files.readAllBytes(Paths.get(javaFilePath)));
        SimpleCompiler simpleCompiler = new SimpleCompiler();
        // 编译静态Java文件
        simpleCompiler.cook(javaFileContent);
        // 加载动态编译的类
        ClassLoader classLoader = simpleCompiler.getClassLoader();
        // 创建动态编译类的实例
        Class<?> dynamicClass = classLoader.loadClass(DynamicClass.class.getName());
        Object instance = dynamicClass.getDeclaredConstructor().newInstance();
        // 调用动态编译类的方法
        String result = (String) dynamicClass.getMethod("sayHello", String.class).invoke(instance, "FelixZh");
        System.out.println(result);
    }
}
