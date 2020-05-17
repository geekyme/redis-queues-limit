package com.example.consumer;

import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.micrometer.core.instrument.MeterRegistry;

@Configuration
public class ConsumerConfig {
  @Bean
  public AtomicInteger airasiaConcurrency(MeterRegistry meterRegistry) {
    return meterRegistry.gauge("airasia.concurrency", new AtomicInteger(0));
  }
}
