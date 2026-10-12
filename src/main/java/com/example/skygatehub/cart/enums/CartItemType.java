package com.example.skygatehub.cart.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CartItemType {
    VE("Vé tham quan"),
    PHONG("Phòng lưu trú"),
    AN("Dịch vụ ăn uống");

    private final String nhan;
}
