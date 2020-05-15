package com.example.producer;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Producer {
  @GetMapping("/hello")
  public String hello() {
    return "Hello Producer";
  }
}
