package com.example.skygatehub.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO trả về cho client sau khi tạo hoặc truy vấn thanh toán.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {
    /** Mã thanh toán (unique). */
    private String paymentId;

    /** Mã đơn đặt liên quan. */
    private String orderId;

    /** Số tiền thanh toán. */
    private BigDecimal amount;

    /** Trạng thái hiện tại của thanh toán (CHU_THANH_TOAN, DA_THANH_TOAN, HUY, …). */
    private String status;

    /** Thời gian tạo yêu cầu. */
    private LocalDateTime createdAt;

    /** Thời gian thanh toán (nếu đã hoàn tất). */
    private LocalDateTime paidAt;
}
