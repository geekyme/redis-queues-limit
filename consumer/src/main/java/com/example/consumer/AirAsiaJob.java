package com.example.consumer;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AirAsiaJob implements Runnable {
  public static String queueId = "airasia";
  private static long delayMs = 1000;
  private Logger logger = LoggerFactory.getLogger(AirAsiaJob.class);
  private ScheduledExecutorService executor;
  private RedissonClient redisson;
  private AtomicInteger airasiaConcurrency;

  public AirAsiaJob(ScheduledExecutorService executor, RedissonClient redisson, AtomicInteger airasiaConcurrency) {
    this.executor = executor;
    this.redisson = redisson;
    this.airasiaConcurrency = airasiaConcurrency;
  }

  @Override
  public void run() {
    RSemaphore s = redisson.getSemaphore(String.format("%s_token_bucket", queueId));
    boolean result = s.tryAcquire();

    if (result) {
      RScoredSortedSet<String> set = redisson.getScoredSortedSet(queueId);
      
      String item = set.pollLast();

      if (item != null) {
        airasiaConcurrency.incrementAndGet();
        logger.info("processing " + item);
        
        try {
          Thread.sleep(delayMs); // artificial processing delay
        } catch (Exception e) {
          logger.info("processing error " + item);
        }
  
        airasiaConcurrency.decrementAndGet();
      }

      s.release();
      // get next job immediately
      fetchNext(0);
    } else {
      logger.info("not processing");
      // get next job later
      fetchNext(delayMs);
    }
  }

  private void fetchNext(long delayMs) {
    executor.schedule(new AirAsiaJob(executor, redisson, airasiaConcurrency), delayMs, TimeUnit.MILLISECONDS);
  }
}
