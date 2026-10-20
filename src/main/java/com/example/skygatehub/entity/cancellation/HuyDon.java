package com.example.skygatehub.entity.cancellation;

import com.example.skygatehub.entity.account.NguoiDung;
import com.example.skygatehub.common.enums.TrangThaiHuyDon;
import com.example.skygatehub.entity.order.ChiTietDon;
import com.example.skygatehub.entity.payment.ChinhSachHuy;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity ánh xạ bảng HUY_DON trong QL_Cong_Troi_Dong_Giang_MySQL.sql.
 * Quản lý yêu cầu hủy từng chi tiết đơn (không hủy cả đơn 1 cục).
 */
@Entity
@Table(name = "HUY_DON")
@Getter
@Setter
@NoArgsConstructor
public class HuyDon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "MA_HUY", length = 20, nullable = false, unique = true)
    private String maHuy;

    @Column(name = "MA_NHOM_HUY", length = 30, nullable = false)
    private String maNhomHuy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
        @JoinColumn(name = "MA_CHI_TIET", referencedColumnName = "MA_CHI_TIET", nullable = false),
        @JoinColumn(name = "MA_DON_DAT", referencedColumnName = "MA_DON_DAT", nullable = false)
    })
    private ChiTietDon chiTietDon;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MA_CHINH_SACH_HUY", referencedColumnName = "MA_CHINH_SACH_HUY")
    private ChinhSachHuy chinhSachHuy;

    @Column(name = "SO_NGAY_TRUOC", nullable = false)
    private Integer soNgayTruoc;

    @Column(name = "THOI_GIAN_YEU_CAU", nullable = false, updatable = false)
    private LocalDateTime thoiGianYeuCau;

    @Column(name = "LY_DO", length = 1000)
    private String lyDo;

    @Column(name = "TIEN_HOAN", precision = 18, scale = 2)
    private BigDecimal tienHoan;

    @Column(name = "TIEN_GIU_LAI", precision = 18, scale = 2)
    private BigDecimal tienGiuLai;

    @Enumerated(EnumType.STRING)
    @Column(name = "TRANG_THAI_XU_LY", length = 25, nullable = false)
    private TrangThaiHuyDon trangThaiXuLy = TrangThaiHuyDon.CAN_XAC_NHAN;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MA_NGUOI_XU_LY", referencedColumnName = "MA_NGUOI_DUNG")
    private NguoiDung nguoiXuLy;

    @Column(name = "THOI_GIAN_XU_LY")
    private LocalDateTime thoiGianXuLy;

    @Column(name = "GHI_CHU_XU_LY", length = 1000)
    private String ghiChuXuLy;

    @PrePersist
    public void prePersist() {
        if (thoiGianYeuCau == null) thoiGianYeuCau = LocalDateTime.now();
        if (trangThaiXuLy == null) trangThaiXuLy = TrangThaiHuyDon.CAN_XAC_NHAN;
    }
}

