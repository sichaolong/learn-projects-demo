package scl.jmhdemo;

import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

/**
 * @projectName: learn-projects-demo
 * @package: scl.jmhdemo
 * @className: JmhHello
 * @author: sichaolong
 * @description: JMH 基准测试入门
 * @date: 2023/6/18 20:47
 * @version: 1.0
 */
@BenchmarkMode(Mode.AverageTime) // 表示统计平均响应时间，不仅可以用在类上，也可用在测试方法上。
@State(Scope.Thread) // 每个进行基准测试的线程都会独享一个对象示例。
@Fork(1) // 表示开启一个线程进行测试。
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 1) // 微基准测试前进行1次预热执行，也可用在测试方法上。
@Measurement(iterations = 1) // 进行 1 次微基准测试，也可用在测试方法上。
public class JmhHello {

    String string = "";
    StringBuilder stringBuilder = new StringBuilder();

    @Benchmark
    public String stringAdd() {
        for (int i = 0; i < 1000; i++) {
            string = string + i;
        }
        return string;
    }

    @Benchmark
    public String stringBuilderAppend() {
        for (int i = 0; i < 1000; i++) {
            stringBuilder.append(i);
        }
        return stringBuilder.toString();
    }

    /**
     * 结果分析
     * 在 JMH 基准测试中，每次运行测试方法都会生成一个 Score 值。Score 值代表了方法运行时间的度量值，其具体含义取决于所使用的测量模式。
     *
     * 在 Throughput（吞吐量）模式下，Score 值表示每秒钟可以执行的操作次数，即方法的吞吐量。Score 值越高，表示方法的吞吐量越大，性能越好。
     *
     * 在 AverageTime（平均时间）和 SampleTime（采样时间）模式下，Score 值表示方法的平均执行时间。Score 值越低，表示方法的平均执行时间越短，性能越好。
     *
     * 在 SingleShotTime（单次执行时间）模式下，Score 值表示方法的单次执行时间。Score 值越低，表示方法的单次执行时间越短，性能越好。
     *
     * Score 值是基准测试结果的一个重要指标，可以用来比较不同方法或不同实现之间的性能差异。需要注意的是，Score 值一般只是基准测试结果的一个方面，还需要结合其他指标和实际应用场景来进行综合分析和评估。
     * @param args
     * @throws RunnerException
     */

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(JmhHello.class.getSimpleName())
                .build();
        new Runner(opt).run();
    }

    /**
     * // 开始测试 stringAdd 方法
     * # JMH version: 1.23
     * # VM version: JDK 1.8.0_181, Java HotSpot(TM) 64-Bit Server VM, 25.181-b13
     * # VM invoker: D:\develop\Java\jdk8_181\jre\bin\java.exe
     * # VM options: -javaagent:C:\ideaIU-2020.1.3.win\lib\idea_rt.jar=50363:C:\ideaIU-2020.1.3.win\bin -Dfile.encoding=UTF-8
     * # Warmup: 3 iterations, 10 s each  // 预热运行三次
     * # Measurement: 5 iterations, 10 s each // 性能测试5次
     * # Timeout: 10 min per iteration  // 超时时间10分钟
     * # Threads: 1 thread, will synchronize iterations  // 线程数量为1
     * # Benchmark mode: Average time, time/op  // 统计方法调用一次的平均时间
     * # Benchmark: net.codingme.jmh.JmhHello.stringAdd // 本次执行的方法
     *
     * # Run progress: 0.00% complete, ETA 00:02:40
     * # Fork: 1 of 1
     * # Warmup Iteration   1: 95.153 ms/op  // 第一次预热，耗时95ms
     * # Warmup Iteration   2: 108.927 ms/op // 第二次预热，耗时108ms
     * # Warmup Iteration   3: 167.760 ms/op // 第三次预热，耗时167ms
     * Iteration   1: 198.897 ms/op  // 执行五次耗时度量
     * Iteration   2: 243.437 ms/op
     * Iteration   3: 271.171 ms/op
     * Iteration   4: 295.636 ms/op
     * Iteration   5: 327.822 ms/op
     *
     *
     * Result "net.codingme.jmh.JmhHello.stringAdd":
     *   267.393 ±(99.9%) 189.907 ms/op [Average]
     *   (min, avg, max) = (198.897, 267.393, 327.822), stdev = 49.318  // 执行的最小、平均、最大、误差值
     *   CI (99.9%): [77.486, 457.299] (assumes normal distribution)
     *
     * // 开始测试 stringBuilderAppend 方法
     * # Benchmark: net.codingme.jmh.JmhHello.stringBuilderAppend
     *
     * # Run progress: 50.00% complete, ETA 00:01:21
     * # Fork: 1 of 1
     * # Warmup Iteration   1: 1.872 ms/op
     * # Warmup Iteration   2: 4.491 ms/op
     * # Warmup Iteration   3: 5.866 ms/op
     * Iteration   1: 6.936 ms/op
     * Iteration   2: 8.465 ms/op
     * Iteration   3: 8.925 ms/op
     * Iteration   4: 9.766 ms/op
     * Iteration   5: 10.143 ms/op
     *
     *
     * Result "net.codingme.jmh.JmhHello.stringBuilderAppend":
     *   8.847 ±(99.9%) 4.844 ms/op [Average]
     *   (min, avg, max) = (6.936, 8.847, 10.143), stdev = 1.258
     *   CI (99.9%): [4.003, 13.691] (assumes normal distribution)
     *
     *
     * # Run complete. Total time: 00:02:42
     *
     * REMEMBER: The numbers below are just data. To gain reusable insights, you need to follow up on
     * why the numbers are the way they are. Use profilers (see -prof, -lprof), design factorial
     * experiments, perform baseline and negative tests that provide experimental control, make sure
     * the benchmarking environment is safe on JVM/OS/HW level, ask for reviews from the domain experts.
     * Do not assume the numbers tell you what you want them to tell.
     * // 测试结果对比
     * Benchmark                     Mode  Cnt    Score     Error  Units
     * JmhHello.stringAdd            avgt    5  267.393 ± 189.907  ms/op
     * JmhHello.stringBuilderAppend  avgt    5    8.847 ±   4.844  ms/op
     *
     * Process finished with exit code 0
     */
}

