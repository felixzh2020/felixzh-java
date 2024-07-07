package CompletableFutureCases;

import java.util.Date;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * @author FelixZh
 * @desc
 * CompletableFuture是Java 8引入的一个功能强大的类，用于处理异步编程。
 * 它不仅提供了一种方式来表示异步计算，还提供了丰富的API来进行复杂的异步 编排 和 处理。
 * CompletableFuture编排好任务的执行方式后，任务会按照规划好的方式一步一步执行，不需要让业务线程去频繁的等待。
 * 比如说任务A，任务B，还有任务C。其中任务B还有任务C执行的前提是任务A先完成，再执行任务B和任务C。

 * CompletionStage接口定义了任务编排的方法，执行某一阶段，可以向下执行后续阶段。
 * 异步执行默认线程池是ForkJoinPool.commonPool()，为了业务之间互不影响，且便于定位问题，强烈推荐使用自定义线程池。

 * 依赖关系
 * thenApply()：把前面任务的执行结果，交给后面的Function
 * thenCompose()：用来连接两个有依赖关系的任务，结果由第二个任务返回

 * and集合关系
 * thenCombine()：合并任务，有返回值
 * thenAcceptBoth()：两个任务执行完成后，将结果交给thenAcceptBoth处理，无返回值
 * runAfterBoth()：两个任务都执行完成后，执行下一步操作(Runnable类型任务)

 * or聚合关系
 * applyToEither()：两个任务哪个执行的快，就使用哪一个结果，有返回值
 * acceptEither()：两个任务哪个执行的快，就消费哪一个结果，无返回值
 * runAfterEither()：任意一个任务执行完成，进行下一步操作(Runnable类型任务)

 * 并行执行
 * allOf()：当所有给定的 CompletableFuture 完成时，返回一个新的 CompletableFuture
 * anyOf()：当任何一个给定的CompletablFuture完成时，返回一个新的CompletableFuture

 * 结果处理
 * whenComplete：当任务完成时，将使用结果(或 null)和此阶段的异常(或 null如果没有)执行给定操作
 * exceptionally：返回一个新的CompletableFuture，当前面的CompletableFuture完成时，它也完成，当它异常完成时，给定函数的异常触发这个CompletableFuture的完成
 */
public class Case1 {
    public static void main(String[] args) {
        // 线程池
        Executor executor = Executors.newFixedThreadPool(10);

        // Task在线程池异步执行完成，返回一个新的 CompletableFuture
        CompletableFuture<String> completableFuture1 = CompletableFuture.supplyAsync(() -> {
            System.out.println(Thread.currentThread().getName());
            return "ok";
        }, executor);

        // 同步 处理completableFuture1结果，无后续返回
        completableFuture1.thenAccept(result -> {
            System.out.println("sync " + result);
        });

        // 异步 处理completableFuture1结果，无后续返回
        completableFuture1.thenAcceptAsync(result -> {
            System.out.println("async " + result);
        }, executor);

        // 同步 处理completableFuture1结果，继续返回CompletableFuture
        CompletableFuture<String> completableFuture2 = completableFuture1.thenApply(result -> new Date() + " " + result);

        // 异步 处理completableFuture1结果，继续返回CompletableFuture
        CompletableFuture<String> completableFuture3 = completableFuture1.thenApplyAsync(result -> new Date() + " " + result, executor);

        // 不处理completableFuture3结果，执行新的任务，无后续返回
        completableFuture3.thenRun(() -> {
            System.out.println("ignore result");
        });

        // 异常处理  handle方法用于处理正常结果和异常情况
        completableFuture2.handle((result, ex) -> {
            if (ex != null) {
                return ex;
            }

            return result;
        }).thenAccept(result -> {
            System.out.println("handle " + result);
        });

        // 异常处理
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            if (Math.random() > 0.5) {
                throw new RuntimeException("out");
            }

            return "success";
        }, executor);

        future.exceptionally(Throwable::getMessage)
                .thenAccept(System.out::println);


    }
}
