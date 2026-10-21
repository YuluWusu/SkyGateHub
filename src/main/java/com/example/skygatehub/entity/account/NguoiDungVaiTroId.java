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

    public String getMaNguoiDung() {
        return maNguoiDung;
    }

    public void setMaNguoiDung(String maNguoiDung) {
        this.maNguoiDung = maNguoiDung;
    }

    public String getMaVaiTro() {
        return maVaiTro;
    }

    public void setMaVaiTro(String maVaiTro) {
        this.maVaiTro = maVaiTro;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        NguoiDungVaiTroId that = (NguoiDungVaiTroId) object;
        return Objects.equals(maNguoiDung, that.maNguoiDung)
                && Objects.equals(maVaiTro, that.maVaiTro);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maNguoiDung, maVaiTro);
    }
}
