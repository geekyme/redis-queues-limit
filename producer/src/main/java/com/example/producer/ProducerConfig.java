package com.example.producer;

import javax.annotation.PostConstruct;

import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProducerConfig {
  Logger logger = LoggerFactory.getLogger(ProducerConfig.class);

  @Value("${application.airasia_token_bucket}")
  private int airAsiaTokenBucketCount;

  @Value("${application.lionair_token_bucket}")
  private int lionAirTokenBucketCount;

  @Autowired
  private RedissonClient redisson;

  @PostConstruct
  public void initTokenBuckets() {
    initTokenBucket(redisson, airAsiaTokenBucketCount, "airasia_token_bucket");
    initTokenBucket(redisson, lionAirTokenBucketCount, "lionair_token_bucket");
  }
  
  private void initTokenBucket(RedissonClient redisson, int count, String queueId) {
    RSemaphore s = redisson.getSemaphore(queueId);
    s.trySetPermits(count);

    logger.info(String.format("Init queue token bucket %s with %d permits", queueId, count));
  }
}
