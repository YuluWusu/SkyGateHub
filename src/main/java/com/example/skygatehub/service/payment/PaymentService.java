package com.example.skygatehub.service.payment;

import com.example.skygatehub.dto.payment.PaymentRequest;
import com.example.skygatehub.dto.payment.PaymentResponse;

/**
 * Service interface for payment operations.
 */
public interface PaymentService {
    /**
     * Create a payment request.
     */
    PaymentResponse createPayment(PaymentRequest request);

    /**
     * Get the latest payment info for an order.
     */
    PaymentResponse getLatestPaymentByOrderId(String orderId);
}
