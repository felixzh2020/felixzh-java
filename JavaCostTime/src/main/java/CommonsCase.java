import org.apache.commons.lang.time.StopWatch;
import org.apache.commons.lang.time.DurationFormatUtils;
import java.util.concurrent.TimeUnit;

public class CommonsCase {
    public static void main(String[] args) throws Exception {
        StopWatch sw = new StopWatch();
        sw.start();
        TimeUnit.SECONDS.sleep(1L);
        sw.split();
        System.out.println(sw.getStartTime() + "," + sw.getSplitTime() + ", " + sw.toSplitString());
    }
}
