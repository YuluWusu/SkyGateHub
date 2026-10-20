package com.example.skygatehub.enums.dining;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TrangThaiBan {
    SAN_SANG("Sẵn sàng"),
    DANG_DUNG("Đang dùng"),
    BAO_TRI("Bảo trì");

    private final String nhan;
}

