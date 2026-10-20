package com.example.skygatehub.enums.order;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TrangThaiDon {
    CHO_COC("Chờ cọc"),
    DA_COC("Đã cọc"),
    HOAN_TAT_THANH_TOAN("Hoàn tất thanh toán"),
    DA_CHECK_IN("Đã check-in"),
    DA_HUY("Đã hủy");

    private final String nhan;
}

