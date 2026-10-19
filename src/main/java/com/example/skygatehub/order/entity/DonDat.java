package com.example.skygatehub.order.entity;

import com.example.skygatehub.account.entity.NguoiDung;
import com.example.skygatehub.common.enums.TrangThaiDon;
import com.example.skygatehub.common.enums.TrangThaiThanhToanDon;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity ánh xạ bảng DON_DAT trong QL_Cong_Troi_Dong_Giang_MySQL.sql.
 * Aggregate Root cho đơn đặt dịch vụ.
 */
@Entity
@Table(name = "DON_DAT")
@Getter
@Setter
@NoArgsConstructor
public class DonDat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "MA_DON_DAT", length = 20, nullable = false, unique = true)
    private String maDonDat;

    @Column(name = "MA_DON_HANG", length = 30, nullable = false, unique = true)
    private String maDonHang;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MA_NGUOI_DUNG", referencedColumnName = "MA_NGUOI_DUNG")
    private NguoiDung nguoiDung;

    @Column(name = "HO_TEN_KHACH", length = 120, nullable = false)
    private String hoTenKhach;

    @Column(name = "SDT_KHACH", length = 20, nullable = false)
    private String sdtKhach;

    @Column(name = "EMAIL_KHACH", length = 150)
    private String emailKhach;

    @Column(name = "GHI_CHU", length = 1000)
    private String ghiChu;

    @Column(name = "THOI_GIAN_DAT", nullable = false, updatable = false)
    private LocalDateTime thoiGianDat;

    @Column(name = "TONG_TIEN", precision = 18, scale = 2, nullable = false)
    private BigDecimal tongTien = BigDecimal.ZERO;

    @Column(name = "TIEN_COC_YEU_CAU", precision = 18, scale = 2)
    private BigDecimal tienCocYeuCau;

    @Column(name = "MA_QR", length = 255, unique = true)
    private String maQr;

    @Enumerated(EnumType.STRING)
    @Column(name = "TRANG_THAI_DON", length = 30, nullable = false)
    private TrangThaiDon trangThaiDon = TrangThaiDon.CHO_XAC_NHAN;

    @Enumerated(EnumType.STRING)
    @Column(name = "TRANG_THAI_THANH_TOAN", length = 30, nullable = false)
    private TrangThaiThanhToanDon trangThaiThanhToan = TrangThaiThanhToanDon.CHUA_THANH_TOAN;

    @Column(name = "NGAY_CAP_NHAT", nullable = false)
    private LocalDateTime ngayCapNhat;

    @PrePersist
    public void prePersist() {
        if (thoiGianDat == null) thoiGianDat = LocalDateTime.now();
        if (ngayCapNhat == null) ngayCapNhat = LocalDateTime.now();
        if (tongTien == null) tongTien = BigDecimal.ZERO;
        if (trangThaiDon == null) trangThaiDon = TrangThaiDon.CHO_XAC_NHAN;
        if (trangThaiThanhToan == null) trangThaiThanhToan = TrangThaiThanhToanDon.CHUA_THANH_TOAN;
    }

    @PreUpdate
    public void preUpdate() {
        ngayCapNhat = LocalDateTime.now();
    }
}
