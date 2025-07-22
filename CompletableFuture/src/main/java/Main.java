import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String... args) throws Exception {

        // 1. 创建自定义线程池（避免占用默认公共线程池）
        ExecutorService executor = Executors.newFixedThreadPool(2);

        // 2. 启动异步任务（带返回值）
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            System.out.println("异步任务开始...线程: " + Thread.currentThread().getName());
            // 模拟耗时操作（如数据库查询、远程调用）
            try {
                Thread.sleep(1000);
                // 模拟随机成功/失败
                if (Math.random() < 0.5) {
                    return "SUCCESS: 任务完成";
                } else {
                    throw new RuntimeException("模拟任务失败");
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }, executor); // 指定自定义线程池[4](@ref)

        // 3. 设置非阻塞回调
        future
                .thenAcceptAsync(result -> {  // 成功回调
                    System.out.println("✅ 成功回调: " + result + " | 线程: " + Thread.currentThread().getName());
                }, executor)
                .exceptionally(ex -> {        // 失败回调
                    System.out.println("❌ 失败回调: " + ex.getMessage() + " | 线程: " + Thread.currentThread().getName());
                    return null;
                });

        // 4. 主线程继续执行（不阻塞）
        System.out.println("主线程继续执行...");

        // 5. 模拟进程常驻
        Thread.sleep(1000000);
    }
}
