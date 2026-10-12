package com.example.skygatehub.checkin.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LoaiDichVuCheckIn {
    VE_CONG("Vé cổng"),
    CAP_TREO("Cáp treo"),
    NHAN_PHONG("Nhận phòng"),
    AN_UONG("Ăn uống");

    private final String nhan;
}
