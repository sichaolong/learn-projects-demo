package scl.jmhdemo;

import org.junit.jupiter.api.Test;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @projectName: learn-projects-demo
 * @package: scl.jmhdemo
 * @className: JmhHelloTest
 * @author: sichaolong
 * @description: TODO
 * @date: 2023/6/18 21:05
 * @version: 1.0
 */

@SpringBootTest
@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@Fork(1)
@Warmup(iterations = 1)
@Measurement(iterations = 2)
public class JmhHelloTest {

    private ConfigurableApplicationContext context;

    // 启动时候执行，集成spring测试
    @Setup(Level.Trial)
    public void init(){
        context = SpringApplication.run(JmhDemoApplication.class);
    }


    @Test
    void test(){
        System.out.println("此方法不涉及计算，不会测量性能");
        System.out.println(context);
    }

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


}
