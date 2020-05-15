package com.example.producer;

import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProducerController {
  @Autowired
  RedissonClient redisson;

  @GetMapping("/hello")
  public String hello() {
    return "Hello Producer";
  }

  @PostMapping("/queueJob")
  public void queueJob(@RequestBody Job job) {
    RScoredSortedSet<String> set = redisson.getScoredSortedSet(job.getQueueId());
    set.add(job.getScore(), job.getName());
  }
}
