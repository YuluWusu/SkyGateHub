package com.example.skygatehub.enums.dining;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CaAn {
    SANG("Sáng"),
    TRUA("Trưa"),
    TOI("Tối");

    private final String nhan;
}

