package com.example.skygatehub.dining.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LoaiDichVuAn {
    BUFFET_SANG("Buffet sáng"),
    SET_TRUA("Set trưa"),
    SET_TOI_THUONG("Set tối thường"),
    SET_TOI_DAC_SAN("Set tối đặc sản");

    private final String nhan;
}
