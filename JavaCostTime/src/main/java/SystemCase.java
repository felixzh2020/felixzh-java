import java.util.concurrent.TimeUnit;

public class SystemCase {
    public static void main(String[] args) throws Exception {
        long start = System.currentTimeMillis();
        TimeUnit.SECONDS.sleep(3L);
        System.out.println("Cost Time: " + (System.currentTimeMillis() - start) + "ms");
    }
}
