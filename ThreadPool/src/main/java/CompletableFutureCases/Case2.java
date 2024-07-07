package CompletableFutureCases;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * @author FelixZh
 * @desc 假设开发一个在线购物平台，用户下单时需要进行以下操作：
 * 1. 验证用户信息。
 * 2. 检查库存。
 * 3. 处理支付。
 * 4. 生成订单。
 * 上述操作尽可能并行执行，以提高系统的响应速度。
 * 注意：并行
 */
public class Case2 {

    public static void main(String[] args) {
        Executor executor = Executors.newFixedThreadPool(10);
        String userId = "user01";
        String itemId = "beer";
        CompletableFuture<Void> orderFuture = CompletableFuture.supplyAsync(() -> checkUser(userId), executor).thenCombineAsync(CompletableFuture.supplyAsync(() -> checkInventory(itemId), executor), (checkUserRes, checkInventoryRes) -> {
                    if (checkUserRes && checkInventoryRes) {
                        return processPayment(userId, itemId);
                    } else {
                        throw new RuntimeException("check failed");
                    }
                }, executor)
                .thenApplyAsync(paymentProcessed -> generateOrder(userId, itemId), executor)
                .thenAcceptAsync(order -> System.out.println("Order completed: " + order), executor)
                .exceptionally(ex -> {
                    System.err.println("Order processing failed: " + ex.getMessage());
                    return null;
                });

        // 等待所有操作完成
        orderFuture.join();

        System.out.println("main thread ......");


    }

    private static boolean checkUser(String userId) {
        // 模拟用户登录
        System.out.println("Success: " + userId);
        return true;
    }

    private static boolean checkInventory(String itemId) {
        // 模拟库存盘查
        System.out.println("Success: " + itemId);
        return true;
    }

    private static boolean processPayment(String userId, String itemId) {
        // 模拟支付处理
        System.out.println("Processing payment for user: " + userId + " and item: " + itemId);
        return true;
    }

    private static String generateOrder(String userId, String itemId) {
        // 模拟订单生成
        System.out.println("Generating order for user: " + userId + " and item: " + itemId);
        return "Order01";
    }
}
