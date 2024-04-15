package org.example.pheatomreference;

import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;

/**
 * @author sichaolong
 * @createdate 2024/3/11 09:10
 */
public class PhantomReferenceTest {

    public static void main(String[] args) {

        PhantomReferenceTest phantomReferenceTest = new PhantomReferenceTest();
        phantomReferenceTest.test();

    }


    public void test(){
        MonitorInfoTest monitorInfoTest = new MonitorInfoTest();
        System.out.println("1 ===========>");
        monitorInfoTest.printMemoryInfo();

        ReferenceQueue<byte[]> queue = new ReferenceQueue<>();
        PhantomReference<byte[]> phantomReference = new PhantomReference<>(
            new byte[1024 * 1024 * 2], queue);

        PhantomReference<byte[]> phantomReference2 = new PhantomReference<>(
            new byte[1024 * 1024 * 2], queue);

        System.out.println("2 ===========>");
        monitorInfoTest.printMemoryInfo();
        // System.gc();
        try {
            Thread.sleep(100L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        System.out.println("虚引用队列元素：" + queue.poll());
//         phantomReference = null;
//         phantomReference2 = null;
        System.out.println("3 ===========>");
        monitorInfoTest.printMemoryInfo();

        byte[] data = new byte[1024 * 1024 * 4];
        // 如果是 byte[] data = new byte[1024 * 1024 * 2]，即使不发生FullGC，但是也会清理一个虚引用对象
        // 如果是 byte[] data = new byte[1024 * 1024 * 3]，即使不发生FullGC，也不会清理虚引用对象
        // 但是换成 byte[] data = new byte[1024 * 1024 * 4]，会发生FullGC，会清理虚引用对象

        System.out.println("虚引用队列元素1：" + queue.poll());
        System.out.println("虚引用队列元素2：" + queue.poll());

        System.out.println("4 ===========>");
        monitorInfoTest.printMemoryInfo();


    }

}
