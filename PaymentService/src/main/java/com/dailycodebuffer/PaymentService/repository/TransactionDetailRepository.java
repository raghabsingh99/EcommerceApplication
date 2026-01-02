package com.dailycodebuffer.PaymentService.repository;

import com.dailycodebuffer.PaymentService.entity.TransactionDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionDetailRepository extends JpaRepository<TransactionDetails,Long> {

    TransactionDetails findByOrderId(long orderId);
}
