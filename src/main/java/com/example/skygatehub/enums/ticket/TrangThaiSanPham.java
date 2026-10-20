package com.example.skygatehub.enums.ticket;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TrangThaiSanPham {
    DANG_BAN("Đang bán"),
    NGUNG_BAN("Ngừng bán");

    private final String nhan;
}

