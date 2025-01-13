import org.codehaus.commons.compiler.CompilerFactoryFactory;
import org.codehaus.commons.compiler.IExpressionEvaluator;

public class ExpressionEvaluatorCase {
    /**
     * Janino is a super-small, super-fast Java compiler.
     * <p>
     * Janino can not only compile a set of source files to a set of class files like JAVAC,
     * but also compile a Java expression, a block, a class body, one .java file or a set of .java files in memory,
     * load the bytecode and execute it directly in the running JVM.
     * <p>
     * JANINO is integrated with Apache Commons JCI ("Java Compiler Interface") and JBoss Rules / Drools.
     * <p>
     * JANINO can also be used for static code analysis or code manipulation.
     */
    public static void main(String[] args) throws Exception {
        // Convert command line argument to call argument "total".
        Object[] arguments = {90.0, 9};

        // Create "ExpressionEvaluator" object.
        IExpressionEvaluator ee = CompilerFactoryFactory
                .getDefaultCompilerFactory(ExpressionEvaluatorCase.class.getClassLoader())
                .newExpressionEvaluator();
        ee.setExpressionType(boolean.class);
        ee.setParameters(new String[]{"total", "count"}, new Class[]{double.class, int.class});
        ee.cook("count == 10 || total >= 100.0 && count > 8");

        // Evaluate expression with actual parameter values.
        Object res = ee.evaluate(arguments);

        // Print expression result.
        System.out.println("Result = " + res);
    }
}
