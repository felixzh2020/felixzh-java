package ByteBuffer;

import java.nio.ByteBuffer;

/**
 * @author FelixZh
 * <p>
 * ByteBuffer是Java NIO库中的一个抽象类，用来处理二进制数据字节流。
 * 适用于处理大文件和高并发场景，特别是在网络编程中，如实现非阻塞IO等。
 * ByteBuffer分为：DirectByteBuffer(直接缓存区) 和 HeapByteBuffer(非直接缓存区)
 * DirectByteBuffer绕过JVM堆内存，直接在系统内容操作，提升IO性能。
 */
public class ByteBufferDemo {
    public static void main(String[] args) {
        int size = 1024 * 1024; // 1MB

        // non-direct test
        ByteBuffer heapBuffer = ByteBuffer.allocate(size);
        long startTime = System.nanoTime();
        for (int i = 0; i < heapBuffer.capacity(); i++) {
            heapBuffer.put((byte) i);
        }
        heapBuffer.flip();
        while (heapBuffer.hasRemaining()) {
            heapBuffer.get();
        }
        long endTime = System.nanoTime();
        System.out.println("Non-direct buffer time: " + (endTime - startTime) + " ns");

        // direct test
        ByteBuffer directBuffer = ByteBuffer.allocateDirect(size);
        startTime = System.nanoTime();
        for (int i = 0; i < directBuffer.capacity(); i++) {
            directBuffer.put((byte) i);
        }
        directBuffer.flip();
        while (directBuffer.hasRemaining()) {
            directBuffer.get();
        }
        endTime = System.nanoTime();
        System.out.println("Non-direct buffer time: " + (endTime - startTime) + " ns");
    }
}
