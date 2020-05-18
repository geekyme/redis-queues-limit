package com.example.consumer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AirAsiaJob implements Runnable {
  public static String queueId = "airasia";
  private Logger logger = LoggerFactory.getLogger(AirAsiaJob.class);
  private ExecutorService executor;
  private RedissonClient redisson;
  private AtomicInteger airasiaConcurrency;

  public AirAsiaJob(ExecutorService executor, RedissonClient redisson, AtomicInteger airasiaConcurrency) {
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
      
      airasiaConcurrency.incrementAndGet();
      logger.info("processing " + item);
      
      try {
        Thread.sleep(1000); // artificial processing delay
      } catch (Exception e) {
        logger.info("processing error " + item);
      }
      s.release();

      airasiaConcurrency.decrementAndGet();
    } else {
      logger.info("not processing");
    }
    fetchNext();
  }
  public void fetchNext() {
    executor.submit(
      new AirAsiaJob(executor, redisson, airasiaConcurrency)
    );
  }
}
