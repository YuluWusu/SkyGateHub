package com.example.skygatehub.dto.dining;

import com.example.skygatehub.common.enums.TrangThaiBanAn;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TableMapCell {

    private String maBanAn;
    private String soBan;
    private String khuVuc;
    private Integer sucChua;
    private TrangThaiBanAn trangThaiVatLy;

    // Trạng thái hiển thị trong sơ đồ: TRONG, DA_XEP_CHO_KHACH, DANG_AN, BAO_TRI
    private String trangThaiHienThi;
    private String cssBadge;
    private String badgeText;

    // Thông tin đặt bàn nếu có gán
    private String maDatBan;
    private String maDonHang;
    private String hoTenKhach;
    private String sdtKhach;
    private Integer soKhach;
    private String tenDichVu;
    private String maCaAn;
    private String tenCaAn;
    private String ghiChu;

    public boolean isTrong() {
        return "TRONG".equalsIgnoreCase(trangThaiHienThi);
    }

    public boolean isDaXep() {
        return "DA_XEP_CHO_KHACH".equalsIgnoreCase(trangThaiHienThi);
    }

    public boolean isDangAn() {
        return "DANG_AN".equalsIgnoreCase(trangThaiHienThi);
    }

    public boolean isBaoTri() {
        return "BAO_TRI".equalsIgnoreCase(trangThaiHienThi);
    }
}
