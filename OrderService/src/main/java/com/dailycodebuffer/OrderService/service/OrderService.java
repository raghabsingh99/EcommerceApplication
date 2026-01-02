package com.dailycodebuffer.OrderService.service;

import com.dailycodebuffer.OrderService.model.OrderRequest;
import com.dailycodebuffer.OrderService.model.OrderResponse;

import java.util.List;

public interface OrderService {
    long placeOrder(OrderRequest orderRequest);

    OrderResponse getOrderDetails(long orderId);

    List<OrderResponse> getAllOrders();
}
