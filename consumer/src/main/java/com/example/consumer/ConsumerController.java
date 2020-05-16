package com.example.consumer;

import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ConsumerController {
  @Autowired
  RedissonClient redisson;

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

      Thread.sleep(3000);

      s.release();

      return item;
    } else {
      return null;
    }
  }
}
