package com.dailycodebuffer.PaymentService.controller;

import com.dailycodebuffer.PaymentService.model.PaymentRequest;
import com.dailycodebuffer.PaymentService.model.PaymentResponse;
import com.dailycodebuffer.PaymentService.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payment")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping
    public ResponseEntity<Long> doPayment(@RequestBody PaymentRequest request){
        return new ResponseEntity<>(paymentService.doPayment(request), HttpStatus.OK);

    }
    @GetMapping("/order/{orderId}")
    public ResponseEntity<PaymentResponse> getPaymentDetailsByOrderId(@PathVariable String orderId){
        PaymentResponse paymentResponse = paymentService.getPaymentDetailsByOrderId(orderId);
        return new ResponseEntity<>(paymentResponse,HttpStatus.OK);
    }
}
