package com.example.skygatehub.enums.dining;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LoaiDichVuAn {
    BUFFET("Buffet"),
    SET_MENU("Set menu"),
    A_LA_CARTE("Gọi món"),
    DO_UONG("Đồ uống");

    private final String nhan;
}
