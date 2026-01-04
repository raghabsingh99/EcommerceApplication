package com.dailycodebuffer.CloudGateway.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class FallbackController {

    @GetMapping("/orderServiceFallback")
    public String orderServiceFallback(){
        return "Order Service is down!";
    }

    @GetMapping("/paymentServiceFallback")
    public String paymentServiceFallback(){
        return "Order Service is down!";
    }

    @GetMapping("/productServiceFallback")
    public String productServiceFallback(){
        return "Order Service is down!";
    }
}
