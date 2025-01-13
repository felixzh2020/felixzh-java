import com.google.common.base.Stopwatch;

import java.util.concurrent.TimeUnit;

public class GuavaCase {
    public static void main(String[] args) throws Exception {
        Stopwatch stopwatch = Stopwatch.createUnstarted();
        stopwatch.start();
        Thread.sleep(1000);
        stopwatch.stop();

        System.out.println("Cost Time: " + stopwatch.elapsed(TimeUnit.MILLISECONDS));
    }
}
