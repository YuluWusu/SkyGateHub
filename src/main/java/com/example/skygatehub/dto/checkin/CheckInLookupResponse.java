package com.example.skygatehub.dto.checkin;

import com.example.skygatehub.common.enums.TrangThaiDon;
import com.example.skygatehub.common.enums.TrangThaiThanhToanDon;
import com.example.skygatehub.common.enums.TrangThaiVe;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CheckInLookupResponse {

    // Thông tin đơn hàng
    private String maDonDat;
    private String maDonHang;
    private String hoTenKhach;
    private String sdtKhach;
    private String emailKhach;
    private String ghiChu;
    private LocalDateTime thoiGianDat;
    private BigDecimal tongTien;
    private TrangThaiDon trangThaiDon;
    private TrangThaiThanhToanDon trangThaiThanhToan;
    private String maQr;

    // Danh sách vé trong đơn
    @Builder.Default
    private List<TicketItemDto> tickets = new ArrayList<>();

    // Thống kê nhanh
    private int tongSoVe;
    private int soVeChuaDung;
    private int soVeDaDung;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TicketItemDto {
        private String maVe;
        private String maVeChuoi;
        private String tenLoaiVe;
        private String loaiVeType;
        private LocalDate ngayThamQuan;
        private TrangThaiVe trangThai;
        private LocalDateTime thoiGianSuDung;
        private String maChiTiet;
        private BigDecimal donGia;
        private boolean canCheckIn;
        private String thongBaoTrangThai;
    }
}
