package com.example.skygatehub.account.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TrangThaiNguoiDung {
    HOAT_DONG("Hoạt động"),
    KHOA("Khóa");

    private final String nhan;
}
