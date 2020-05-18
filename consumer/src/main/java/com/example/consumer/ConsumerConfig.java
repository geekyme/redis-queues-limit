package com.example.consumer;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;

import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

  @Value("${application.lionair.threads}")
  private int lionairFetcherThreads;

  @PostConstruct
  public void initFetchers() {
    initAirAsiaFetcher(airasiaFetcherThreads);
    initLionAirFetcher(lionairFetcherThreads);
  }

  private void initAirAsiaFetcher(int threads) {
    ScheduledExecutorService executor = Executors.newScheduledThreadPool(threads);
    AtomicInteger airasiaConcurrency = meterRegistry.gauge("airasia_concurrency", new AtomicInteger(0));

    for (int i = 0; i < threads; i++) {
      executor.submit(new AirAsiaJob(executor, redisson, airasiaConcurrency));
    }
  }

  private void initLionAirFetcher(int threads) {
    ScheduledExecutorService executor = Executors.newScheduledThreadPool(threads);
    AtomicInteger lionairConcurrency = meterRegistry.gauge("lionair_concurrency", new AtomicInteger(0));

    for (int i = 0; i < threads; i++) {
      executor.submit(new LionAirJob(executor, redisson, lionairConcurrency));
    }
  }
}
