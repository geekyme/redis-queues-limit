package com.example.consumer;

import java.util.concurrent.atomic.AtomicInteger;

import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ConsumerController {
  private RedissonClient redisson;
  private AtomicInteger airasiaExecutions;

  public ConsumerController(AtomicInteger airasiaExecutions, RedissonClient redisson) {
    this.airasiaExecutions = airasiaExecutions;
    this.redisson = redisson;
  }

  @GetMapping("/hello")
  public String hello() {
    return "Hello Consumer";
  }

  @PostMapping("/pollJob/{queueId}")
  public String pollJob(@PathVariable String queueId) throws Exception {
    RSemaphore s = redisson.getSemaphore(String.format("%s_token_bucket", queueId));
    boolean result = s.tryAcquire();

    if (result) {
      RScoredSortedSet<String> set = redisson.getScoredSortedSet(queueId);
      
      String item = set.pollLast();

      if ("airasia".equals(queueId)) {
        airasiaExecutions.incrementAndGet();
      }

      Thread.sleep(3000);

      s.release();

      return item;
    } else {
      return null;
    }
  }
}
