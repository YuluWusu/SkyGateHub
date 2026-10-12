package com.example.skygatehub.ticket.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TrangThaiSanPham {
    DANG_BAN("Đang bán"),
    NGUNG_BAN("Ngừng bán");

    private final String nhan;
}
