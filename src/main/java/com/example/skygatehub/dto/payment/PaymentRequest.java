package com.example.skygatehub.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;

/**
 * DTO nhận dữ liệu từ client khi tạo yêu cầu thanh toán.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequest {
    /** Mã đơn đặt (order ID) */
    private String orderId;

    /** Số tiền cần thanh toán */
    private BigDecimal amount;

    /** Phương thức thanh toán (ví dụ: VNPAY, Momo, Stripe) */
    private String paymentMethod;

    /** Mô tả hoặc thông tin bổ sung (tùy chọn) */
    private String description;
}
