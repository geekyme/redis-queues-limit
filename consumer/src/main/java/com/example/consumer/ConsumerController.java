package com.example.consumer;

import java.util.concurrent.atomic.AtomicInteger;

import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import io.micrometer.core.instrument.Counter;

@RestController
public class ConsumerController {
  private Logger logger = LoggerFactory.getLogger(ConsumerController.class);
  private RedissonClient redisson;
  private AtomicInteger airasiaConcurrency;

  public ConsumerController(AtomicInteger airasiaConcurrency, RedissonClient redisson) {
    this.airasiaConcurrency = airasiaConcurrency;
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
      logger.info("processing");
      RScoredSortedSet<String> set = redisson.getScoredSortedSet(queueId);
      
      String item = set.pollLast();

      if ("airasia".equals(queueId)) {
        airasiaConcurrency.incrementAndGet();
      }

      Thread.sleep(100);
      s.release();

      if ("airasia".equals(queueId)) {
        airasiaConcurrency.decrementAndGet();
      }

      return item;
    } else {
      logger.info("not processing");
      return null;
    }
  }
}
