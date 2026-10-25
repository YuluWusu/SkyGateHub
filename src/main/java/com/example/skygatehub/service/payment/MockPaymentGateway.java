package com.example.skygatehub.service.payment;

import com.example.skygatehub.dto.payment.PaymentRequest;
import com.example.skygatehub.dto.payment.PaymentResponse;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Mock implementation of a payment gateway used for testing and demo purposes.
 * It does not call any external service; it simply generates a fake transaction id
 * and returns a {@link PaymentResponse} with a pending status.
 */
public class MockPaymentGateway {

    /**
     * Simulates processing a payment request.
     *
     * @param request the payment request data
     * @return a mock transaction identifier (e.g., "MOCK-xxxx")
     */
    public String processPayment(PaymentRequest request) {
        // In a real gateway you would call external APIs here.
        // For the mock we just generate a UUID‑based dummy ID.
        return "MOCK-" + UUID.randomUUID().toString();
    }

    /**
     * Creates a {@link PaymentResponse} based on the mock processing result.
     *
     * @param request the original payment request
     * @return a response object containing mock data
     */
    public PaymentResponse createMockResponse(PaymentRequest request) {
        PaymentResponse response = new PaymentResponse();
        response.setPaymentId(processPayment(request));
        response.setOrderId(request.getOrderId());
        response.setAmount(request.getAmount());
        response.setStatus("CHO_THANH_TOAN"); // pending status
        response.setCreatedAt(LocalDateTime.now());
        response.setPaidAt(null);
        return response;
    }
}
