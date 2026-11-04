package com.example.skygatehub.service.order;

import com.example.skygatehub.dto.order.BookingPipelineForm;
import com.example.skygatehub.entity.order.DonDat;

public interface CheckoutService {
    /**
     * Xử lý trọn gói quy trình đặt dịch vụ: Vé tham quan, Phòng lưu trú, Suất ăn ẩm thực
     * và tạo giao dịch thanh toán đặt cọc 30%.
     */
    DonDat processPipelineBooking(BookingPipelineForm form);
}

