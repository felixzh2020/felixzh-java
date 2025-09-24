import java.util.ArrayList;
import java.util.List;

public class ErasureDemo {
    public static void main(String[] args) {
        List<String> stringList = new ArrayList<>();
        List<Integer> integerList = new ArrayList<>();

        System.out.println(stringList.getClass() == integerList.getClass());
        // true 类型擦除后两者都是List的原始类型

        // 源列表 使用 ? extends T （生产者）
        List<Integer> intList = new ArrayList<>();
        intList.add(1);
        intList.add(2);

        // 目的表 使用 ? super T (消费者)
        List<Number> numberList = new ArrayList<>();

        copy(intList, numberList);

        System.out.println(numberList);
    }

    public static <T> void copy(List<? extends T> src, List<? super T> dest) {
        for (int i = 0; i < src.size(); i++) {
            dest.add(src.get(i));
        }
    }
}
