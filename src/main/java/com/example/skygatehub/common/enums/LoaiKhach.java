package com.example.skygatehub.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LoaiKhach {
    NGUOI_LON("Người lớn"),
    TRE_EM("Trẻ em");

    private final String nhan;
}
