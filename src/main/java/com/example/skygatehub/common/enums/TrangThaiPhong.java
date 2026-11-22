package com.example.skygatehub.common.enums;

/**
 * PHONG.TRANG_THAI ∈ {'SAN_SANG','DANG_SU_DUNG','BAO_TRI'}
 */
public enum TrangThaiPhong {
    SAN_SANG("Sẵn sàng"),
    DANG_SU_DUNG("Đang có khách"),
    BAO_TRI("Đang bảo trì");

    private final String moTa;

    TrangThaiPhong(String moTa) {
        this.moTa = moTa;
    }

    public String getMoTa() {
        return moTa;
    }

    public String getNhan() {
        return moTa;
    }
}
