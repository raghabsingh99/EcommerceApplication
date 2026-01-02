package com.dailycodebuffer.PaymentService.service;

import com.dailycodebuffer.PaymentService.model.PaymentRequest;
import com.dailycodebuffer.PaymentService.model.PaymentResponse;

public interface PaymentService {
    Long doPayment(PaymentRequest request);

    PaymentResponse getPaymentDetailsByOrderId(String orderId);
}
