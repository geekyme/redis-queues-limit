package com.example.consumer;

import org.redisson.api.RScoredSortedSet;
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
  public String pollJob(@PathVariable String queueId) {
    RScoredSortedSet<String> set = redisson.getScoredSortedSet(queueId);
    
    return set.pollLast();
  }
}
