package com.example.skygatehub.enums.payment;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LoaiThanhToan {
    DAT_COC("Đặt cọc"),
    THANH_TOAN_NOT("Thanh toán nốt"),
    TOAN_BO("Toàn bộ"),
    HOAN_TIEN("Hoàn tiền");

    private final String nhan;
}

