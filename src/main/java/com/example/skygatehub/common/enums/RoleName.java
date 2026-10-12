package com.example.skygatehub.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RoleName {
    QUAN_LY("Quản lý"),
    NHAN_VIEN_CHECK_IN("Nhân viên check-in"),
    NHAN_VIEN_PHAN_PHONG("Nhân viên phân phòng"),
    NHAN_VIEN_PHAN_BAN("Nhân viên phân bàn"),
    KHACH_HANG("Khách hàng");

    private final String nhan;
}
