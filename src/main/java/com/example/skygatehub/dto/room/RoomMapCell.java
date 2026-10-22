package com.example.skygatehub.dto.room;

import com.example.skygatehub.common.enums.TrangThaiPhong;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoomMapCell {

    private String maPhong;
    private String soPhong;
    private String maLoaiPhong;
    private String tenLoaiPhong;
    private Integer maKhu;
    private String tenKhu;
    private BigDecimal giaCoBan;
    private String loaiGiuong;
    private Integer dienTich;
    private Integer sucChuaNguoiLon;
    private Integer sucChuaTreEm;

    // Trạng thái vật lý của bảng PHONG
    private TrangThaiPhong trangThaiVatLy;

    // Trạng thái hiển thị theo ngày được chọn: TRONG | DA_PHAN_CHO_NHAN | DANG_O | BAO_TRI
    private String trangThaiHienThi;
    private String cssBadge;
    private String badgeText;

    // Thông tin phân phòng & khách hàng (nếu có)
    private String maPhanPhong;
    private String maDatPhong;
    private String maDonHang;
    private String hoTenKhach;
    private String sdtKhach;
    private String emailKhach;
    private LocalDate ngayNhan;
    private LocalDate ngayTra;
    private Integer soLuongPhongDat;
    private Integer soKhach;
    private LocalDateTime thoiGianPhan;

    public boolean isTrong() {
        return "TRONG".equals(trangThaiHienThi);
    }

    public boolean isDaPhan() {
        return "DA_PHAN_CHO_NHAN".equals(trangThaiHienThi);
    }

    public boolean isDangO() {
        return "DANG_O".equals(trangThaiHienThi);
    }

    public boolean isBaoTri() {
        return "BAO_TRI".equals(trangThaiHienThi);
    }
}
