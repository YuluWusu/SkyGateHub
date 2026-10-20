package com.example.skygatehub.entity.account;

import java.io.Serializable;
import java.util.Objects;

/**
 * Composite key cho bảng NGUOI_DUNG_VAI_TRO.
 */
public class NguoiDungVaiTroId implements Serializable {

    private String maNguoiDung;
    private String maVaiTro;

    public NguoiDungVaiTroId() {}

    public NguoiDungVaiTroId(String maNguoiDung, String maVaiTro) {
        this.maNguoiDung = maNguoiDung;
        this.maVaiTro = maVaiTro;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NguoiDungVaiTroId that)) return false;
        return Objects.equals(maNguoiDung, that.maNguoiDung)
            && Objects.equals(maVaiTro, that.maVaiTro);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maNguoiDung, maVaiTro);
    }
}

