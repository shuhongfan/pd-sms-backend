package com.itheima.controller;

import com.itheima.lock.RedisLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StockController {
    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 不使用锁
     * @return
     */
    @GetMapping("/stock1")
    public String stock1(){
        int stock = Integer.parseInt(redisTemplate.opsForValue().get("stock"));
        if(stock > 0){
            stock --;
            redisTemplate.opsForValue().set("stock",stock+"");
            System.out.println("库存扣减成功，剩余库存：" + stock);
        }else {
            System.out.println("库存不足！！！");
        }
        return "OK";
    }

    /**
     * 使用Java的锁机制
     * @return
     */
    @GetMapping("/stock2")
    public String stock2(){
        synchronized (this){
            int stock = Integer.parseInt(redisTemplate.opsForValue().get("stock"));
            if(stock > 0){
                stock --;
                redisTemplate.opsForValue().set("stock",stock+"");
                System.out.println("库存扣减成功，剩余库存：" + stock);
            }else {
                System.out.println("库存不足！！！");
            }
        }
        return "OK";
    }

    @Autowired
    private RedisLock redisLock;

    /**
     * 使用Redis分布式锁---无阻塞
     * @return
     */
    @GetMapping("/stock3")
    public String stock3(){
        //尝试获取锁
        String mylock = redisLock.tryLock("MYLOCK", 2000);

        if(mylock != null){
            //获取到了锁
            int stock = Integer.parseInt(redisTemplate.opsForValue().get("stock"));
            if(stock > 0){
                stock --;
                redisTemplate.opsForValue().set("stock",stock+"");
                System.out.println("库存扣减成功，剩余库存：" + stock);
            }else {
                System.out.println("库存不足！！！");
            }
            redisLock.unlock("MYLOCK",mylock);
        }

        return "OK";
    }

    /**
     * 使用Redis分布式锁---有阻塞
     * @return
     */
    @GetMapping("/stock4")
    public String stock4(){
        //尝试获取锁
        String mylock = redisLock.lock("MYLOCK",2000,1000);

        if(mylock != null){
            //获取到了锁
            int stock = Integer.parseInt(redisTemplate.opsForValue().get("stock"));
            if(stock > 0){
                stock --;
                redisTemplate.opsForValue().set("stock",stock+"");
                System.out.println("库存扣减成功，剩余库存：" + stock);
            }else {
                System.out.println("库存不足！！！");
            }
            redisLock.unlock("MYLOCK",mylock);
        }

        return "OK";
    }
}