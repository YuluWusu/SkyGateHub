package com.example.skygatehub.dto.order;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderSummaryResponse {
    /** Mã đơn đặt (order ID). */
    private String orderId;

    /** Tổng số tiền của đơn (có thể bao gồm thuế, phí, giảm giá...). */
    private BigDecimal totalAmount;

    /** Danh sách các mục trong đơn. */
    private List<OrderItem> items;

    /** Trạng thái thanh toán tổng hợp của đơn. */
    private String paymentStatus;

    /** Thời gian tạo đơn. */
    private LocalDateTime createdAt;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItem {
        private String productCode;
        private String productName;
        private Integer quantity;
        private BigDecimal unitPrice;
    }
}
