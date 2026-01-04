package com.dailycodebuffer.OrderService.service;

import com.dailycodebuffer.OrderService.entity.Order;
import com.dailycodebuffer.OrderService.exception.CustomException;
import com.dailycodebuffer.OrderService.external.client.PaymentService;
import com.dailycodebuffer.OrderService.external.client.ProductService;
import com.dailycodebuffer.OrderService.external.request.PaymentRequest;
import com.dailycodebuffer.OrderService.external.response.PaymentResponse;
import com.dailycodebuffer.OrderService.model.OrderRequest;
import com.dailycodebuffer.OrderService.model.OrderResponse;
import com.dailycodebuffer.OrderService.repository.OrderRepository;
import com.dailycodebuffer.ProductService.model.ProductResponse;
import jakarta.validation.constraints.Null;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.List;

@Service
@Log4j2
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private ProductService productService;
    @Autowired
    private PaymentService paymentService;
    @Autowired
    private RestTemplate restTemplate;

    @Override
    public long placeOrder(OrderRequest orderRequest) {
        log.info("Placing order request: {}", orderRequest);
        productService.reducedQuantity(orderRequest.getProductId(), orderRequest.getQuantity());

        log.info("Creating Order with Status CREATED");
        Order order = Order.builder()
                .amount(orderRequest.getTotalAmount())
                .orderStatus("CREATED")
                .productId(orderRequest.getProductId())
                .orderDate(Instant.now())
                .quantity(orderRequest.getQuantity())
                .build();

        order = orderRepository.save(order);
        log.info("Calling payment service to complete the payment");

        PaymentRequest paymentRequest = PaymentRequest.builder()
                .orderId(order.getId())
                .amount(orderRequest.getTotalAmount())
                .paymentMode(orderRequest.getPaymentMode())
                .build();

        String orderStatus = null;
        try {
            paymentService.doPayment(paymentRequest);
            log.info("payment done Successfully.Changing the order status to Placed");
            orderStatus = "PLACED";
        } catch (Exception e) {
            log.error("Error occured in payment. Changing the order status to PAYMENT_FAILED");
            orderStatus = "PAYMENT_FAILED";
        }

        order.setOrderStatus(orderStatus);
        orderRepository.save(order);
        log.info("Order placed successfully with Order Id: {}", order.getId());
        return order.getId();
    }

    @Override
    public OrderResponse getOrderDetails(long orderId) {
        log.info("Get Order details for OrderId: {}", orderId);
        Order order = orderRepository.findById(orderId).orElseThrow(() ->
                new CustomException("Order not found for the order id", "NOT_FOUND", 404));

        log.info("Invoking product service to fetch the product");
        ProductResponse productResponse = null;
        try {
            productResponse = restTemplate.getForObject(
                    "http://PRODUCT-SERVICE/" + order.getProductId(),
                    ProductResponse.class
            );
        } catch (Exception e) {
            log.error("Product service call failed", e);
        }
        log.info("Getting payment infor from payment service");
        PaymentResponse paymentResponse = null;

        try {
            paymentResponse = restTemplate.getForObject(
                    "http://PAYMENT-SERVICE/payment/order/" + order.getId(),
                    PaymentResponse.class
            );
        } catch (Exception e) {
            log.warn("Payment service unavailable for order {}", order.getId());
        }


        OrderResponse.ProductDetails productDetails = null;

        if (productResponse != null) {
            productDetails = OrderResponse.ProductDetails.builder()
                    .productName(productResponse.getProductName())
                    .productId(productResponse.getProductId())
                    .price(productResponse.getPrice())
                    .quantity(productResponse.getQuantity())
                    .build();
        }

        OrderResponse.PaymentDetails paymentDetails = null;

        if (paymentResponse != null) {
            paymentDetails = OrderResponse.PaymentDetails.builder()
                    .paymentId(paymentResponse.getPaymentId())
                    .paymentMode(paymentResponse.getPaymentMode())
                    .paymentStatus(paymentResponse.getStatus())
                    .paymentDate(paymentResponse.getPaymentDate())
                    .build();
        }



        OrderResponse orderResponse = OrderResponse.builder()
                .orderId(order.getId())
                .orderStatus(order.getOrderStatus())
                .orderDate(order.getOrderDate())
                .amount(order.getAmount())
                .productDetails(productDetails)
                .paymentDetails(paymentDetails)
                .build();
        return orderResponse;
    }

    @Override
    public List<OrderResponse> getAllOrders() {
        log.info("Get All Orders details for OrderId ");

        List<Order> orders = orderRepository.findAll();
        return orders.stream().map(order -> {
            ProductResponse productResponse = restTemplate.getForObject("https://PRODUCT-SERVICE/product/" + order.getProductId(), ProductResponse.class);

            OrderResponse.ProductDetails productDetails = OrderResponse.ProductDetails.builder()
                    .productId(productResponse.getProductId())
                    .productName(productResponse.getProductName())
                    .price(productResponse.getPrice())
                    .quantity(productResponse.getQuantity())
                    .build();

            OrderResponse.PaymentDetails paymentDetails = null;

            try {
                PaymentResponse paymentResponse = restTemplate.getForObject("http://PAYMENT-SERVICE/payment/order/" + order.getId(),
                        PaymentResponse.class
                );

                paymentDetails = OrderResponse.PaymentDetails.builder()
                        .paymentId(paymentResponse.getPaymentId())
                        .paymentMode(paymentResponse.getPaymentMode())
                        .paymentStatus(paymentResponse.getStatus())
                        .paymentDate(paymentResponse.getPaymentDate())
                        .build();
            } catch (Exception e) {
                log.info("No payment found for order: {}", order.getId());
            }

            return OrderResponse.builder()
                    .orderId(order.getId())
                    .orderStatus(order.getOrderStatus())
                    .orderDate(order.getOrderDate())
                    .amount(order.getAmount())
                    .productDetails(productDetails)
                    .paymentDetails(paymentDetails)
                    .build();


        }).toList();
    }
}
