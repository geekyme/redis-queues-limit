package com.example.consumer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;

import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.micrometer.core.instrument.MeterRegistry;

@Configuration
public class ConsumerConfig {
  @Autowired
  private RedissonClient redisson;

  @Autowired
  private MeterRegistry meterRegistry;

  @Value("${application.airasia.threads}")
  private int airasiaFetcherThreads;

  @PostConstruct
  public void initFetchers() {
    initAirAsiaFetcher(airasiaFetcherThreads);
  }

  private void initAirAsiaFetcher(int threads) {
    ExecutorService executor = Executors.newFixedThreadPool(threads);
    AtomicInteger airasiaConcurrency = meterRegistry.gauge("airasia.concurrency", new AtomicInteger(0));

    for (int i = 0; i < threads; i++) {
      executor.submit(new AirAsiaJob(executor, redisson, airasiaConcurrency));
    }
  }
}
