package com.example.skygatehub.common.enums;

public enum LoaiThanhPhan {
    THAM_QUAN("Tham quan"),
    AN_UONG("Ẩm thực"),
    CAP_TREO("Cáp treo"),
    XE_DIEN("Xe điện"),
    KHAC("Tiện ích khác");

    private final String displayName;

    LoaiThanhPhan(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
