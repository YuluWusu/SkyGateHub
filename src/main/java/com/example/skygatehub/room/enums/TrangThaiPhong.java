package com.example.skygatehub.room.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TrangThaiPhong {
    SAN_SANG("Sẵn sàng"),
    DANG_O("Đang ở"),
    BAO_TRI("Bảo trì"),
    DANG_DON("Đang dọn");

    private final String nhan;
}
