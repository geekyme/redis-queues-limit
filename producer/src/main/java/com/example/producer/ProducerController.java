package com.example.producer;

import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Gauge;
import io.prometheus.client.exporter.PushGateway;

@RestController
public class ProducerController {
  @Autowired
  RedissonClient redisson;

  @GetMapping("/hello")
  public String hello() {
    return "Hello Producer";
  }

  @GetMapping("/tryPushGateway")
  public String tryPushGateway() throws Exception {
    CollectorRegistry registry = new CollectorRegistry();
     Gauge duration = Gauge.build()
         .name("my_batch_job_duration_seconds").help("Duration of my batch job in seconds.").register(registry);
     Gauge.Timer durationTimer = duration.startTimer();
     try {
       // Your code here.
       Thread.sleep(1000);

       // This is only added to the registry after success,
       // so that a previous success in the Pushgateway isn't overwritten on failure.
       Gauge lastSuccess = Gauge.build()
           .name("my_batch_job_last_success").help("Last time my batch job succeeded, in unixtime.").register(registry);
       lastSuccess.setToCurrentTime();
     } finally {
       durationTimer.setDuration();
       PushGateway pg = new PushGateway("pushgateway:9091");
       pg.pushAdd(registry, "my_batch_job");
     }

     return "PUSHED!";
  }

  @PostMapping("/queueJob")
  public void queueJob(@RequestBody Job job) {
    RScoredSortedSet<String> set = redisson.getScoredSortedSet(job.getQueueId());
    set.add(job.getScore(), job.getName());
  }
}
