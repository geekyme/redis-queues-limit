package com.example.consumer;

import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.micrometer.core.instrument.MeterRegistry;

@Configuration
public class ConsumerConfig {
  @Bean
  public AtomicInteger airasiaExecutions(MeterRegistry meterRegistry) {
    return meterRegistry.gauge("airasia.executions", new AtomicInteger());
  }
}
