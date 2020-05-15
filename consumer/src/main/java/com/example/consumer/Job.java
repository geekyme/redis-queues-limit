package com.example.consumer;

import lombok.Data;

@Data
public class Job {
  private String queueId;
  private double score;
  private String name;
}
