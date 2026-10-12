package com.example.skygatehub.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LoaiNgay {
    NGAY_THUONG("Ngày thường"),
    THU_BAY_LE("Thứ Bảy & Lễ");

    private final String nhan;
}
