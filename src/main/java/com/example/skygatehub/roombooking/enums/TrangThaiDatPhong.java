package com.example.skygatehub.roombooking.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TrangThaiDatPhong {
    CHO_PHAN_PHONG("Chờ phân phòng"),
    DA_PHAN_PHONG("Đã phân phòng"),
    DA_NHAN_PHONG("Đã nhận phòng"),
    DA_TRA_PHONG("Đã trả phòng");

    private final String nhan;
}
