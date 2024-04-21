package scl.controller;


import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import scl.mapper.UserMapper;
import scl.pojo.User;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@RestController
@Slf4j
public class UserController {

    ThreadPoolExecutor pool = new ThreadPoolExecutor(10,
            10,
            5,
            TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>());



    @RequestMapping(method = RequestMethod.GET,value = "/test")
    public String test(){
        log.info("test！");
        List<User> all = userMapper.getAll();
        log.info("当前线程:{} ,查询结果:{}",Thread.currentThread().getName(), all);
        return "test!";
    }
    @Resource
    UserMapper userMapper;


    @RequestMapping(method = RequestMethod.GET,value = "/list/{threadNum}")
    public String getUserList(@PathVariable("threadNum") Integer threadNum){
        List<CompletableFuture> futureList = new ArrayList<>();
        for (Integer i = 0; i < threadNum; i++) {

            Integer finalI = i;
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                List<User> all = userMapper.getAll();
                log.info("当前:{}，当前线程:{} ,查询结果:{}", finalI,Thread.currentThread().getName(), all);
            }, pool);
            futureList.add(future);
        }

        for (CompletableFuture future : futureList) {
            try {
                future.get();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        log.info("所以任务执行完毕！");
        return "Completed!";

    }
}
