package main;

import java.util.ArrayList;
import java.util.List;

/**
 * @author esin88
 */
public class Main {
    public static void main(String[] args) {
        final List<Long> longList = new ArrayList<>();
        final List<Number> numberList = new ArrayList<>();
        longList.add(1L);
        numberList.add(1d);

        System.out.println("Testing Long list");
        testExtends(longList);
        testSuper(longList);
        longList.forEach(System.out::println);

        System.out.println("Testing Number list");
        testExtends(numberList);
        testSuper(numberList);
        numberList.forEach(System.out::println);
    }

    private static void testExtends(List<? extends Number> list) {
        final Number number = list.get(0);
        assert number != null;
        //noinspection ObjectToString
        System.out.println(number + " " + number.getClass());
        //list.add(2);
    }

    private static void testSuper(List<? super Long> list) {
        final Object o = list.get(0);
        assert o != null;
        System.out.println(o + " " + o.getClass());
        //noinspection MagicNumber
        list.add(3L);
    }
}
