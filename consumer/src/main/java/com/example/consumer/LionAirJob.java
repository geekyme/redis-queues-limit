package com.example.consumer;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LionAirJob implements Runnable {
  public static String queueId = "lionair";
  private static long delayMs = 300;
  private Logger logger = LoggerFactory.getLogger(LionAirJob.class);
  private ScheduledExecutorService executor;
  private RedissonClient redisson;
  private AtomicInteger lionairConcurrency;

  public LionAirJob(ScheduledExecutorService executor, RedissonClient redisson, AtomicInteger lionairConcurrency) {
    this.executor = executor;
    this.redisson = redisson;
    this.lionairConcurrency = lionairConcurrency;
  }

  @Override
  public void run() {
    RSemaphore s = redisson.getSemaphore(String.format("%s_token_bucket", queueId));
    boolean result = s.tryAcquire();

    if (result) {
      RScoredSortedSet<String> set = redisson.getScoredSortedSet(queueId);
      
      String item = set.pollLast();

      if (item != null) {
        lionairConcurrency.incrementAndGet();
        logger.info("processing " + item);
        
        try {
          Thread.sleep(delayMs); // artificial processing delay
        } catch (Exception e) {
          logger.info("processing error " + item);
        }
        s.release();
  
        lionairConcurrency.decrementAndGet();
      }

      // get next job immediately
      fetchNext(0);
    } else {
      logger.info("not processing");
      // get next job later
      fetchNext(delayMs);
    }
  }

  private void fetchNext(long delayMs) {
    executor.schedule(new LionAirJob(executor, redisson, lionairConcurrency), delayMs, TimeUnit.MILLISECONDS);
  }
}
