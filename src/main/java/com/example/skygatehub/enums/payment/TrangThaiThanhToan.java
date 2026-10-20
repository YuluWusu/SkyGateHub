package com.example.skygatehub.enums.payment;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TrangThaiThanhToan {
    THANH_CONG("Thành công"),
    THAT_BAI("Thất bại"),
    DANG_XU_LY("Đang xử lý");

    private final String nhan;
}

