import java.util.concurrent.atomic.AtomicBoolean;

public class AtomicBooleanDemo {
    public static void main(String... args) {
        AtomicBoolean flag = new AtomicBoolean(false);
        if (flag.compareAndSet(false, true)) {
            System.out.println(flag.get());
        }
    }
}
