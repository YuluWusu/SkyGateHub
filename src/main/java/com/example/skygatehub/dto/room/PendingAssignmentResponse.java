package com.example.skygatehub.dto.room;

import com.example.skygatehub.common.enums.TrangThaiDatPhong;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PendingAssignmentResponse {

    private String maDatPhong;
    private String maDonHang;
    private String hoTenKhach;
    private String sdtKhach;
    private String emailKhach;
    private String ghiChu;

    private String maLoaiPhong;
    private String tenLoaiPhong;
    private Integer maKhu;
    private String tenKhu;

    private LocalDate ngayNhanPhong;
    private LocalDate ngayTraPhong;
    private Integer soLuongPhong;
    private Integer soKhach;
    private TrangThaiDatPhong trangThai;

    private int soLuongDaPhan;
    private int soLuongConThieu;

    private List<String> danhSachPhongDaPhan = new ArrayList<>();
    private List<String> danhSachMaPhanPhong = new ArrayList<>();

    public boolean isDaPhanDu() {
        return soLuongConThieu <= 0;
    }
}
