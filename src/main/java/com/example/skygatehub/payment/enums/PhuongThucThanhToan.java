package com.example.skygatehub.payment.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PhuongThucThanhToan {
    TIEN_MAT("Tiền mặt"),
    CHUYEN_KHOAN("Chuyển khoản"),
    VNPAY_MO_PHONG("VNPAY mô phỏng");

    private final String nhan;
}
