package com.example.skygatehub.payment.entity;

import com.example.skygatehub.common.enums.LoaiGiaoDich;
import com.example.skygatehub.common.enums.PhuongThucThanhToan;
import com.example.skygatehub.common.enums.TrangThaiThanhToan;
import com.example.skygatehub.order.entity.DonDat;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity ánh xạ bảng THANH_TOAN trong QL_Cong_Troi_Dong_Giang_MySQL.sql.
 * Hỗ trợ thanh toán nhiều đợt cho 1 đơn hàng.
 */
@Entity
@Table(name = "THANH_TOAN")
@Getter
@Setter
@NoArgsConstructor
public class ThanhToan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "MA_THANH_TOAN", length = 20, nullable = false, unique = true)
    private String maThanhToan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MA_DON_DAT", referencedColumnName = "MA_DON_DAT", nullable = false)
    private DonDat donDat;

    @Enumerated(EnumType.STRING)
    @Column(name = "LOAI_GIAO_DICH", length = 30, nullable = false)
    private LoaiGiaoDich loaiGiaoDich = LoaiGiaoDich.THANH_TOAN;

    @Enumerated(EnumType.STRING)
    @Column(name = "PHUONG_THUC_THANH_TOAN", length = 30, nullable = false)
    private PhuongThucThanhToan phuongThucThanhToan;

    @Column(name = "SO_TIEN", precision = 18, scale = 2, nullable = false)
    private BigDecimal soTien;

    @Column(name = "MA_GIAO_DICH", length = 120, unique = true)
    private String maGiaoDich;

    @Enumerated(EnumType.STRING)
    @Column(name = "TRANG_THAI", length = 30, nullable = false)
    private TrangThaiThanhToan trangThai = TrangThaiThanhToan.CHO_THANH_TOAN;

    @Column(name = "THOI_GIAN_TAO", nullable = false, updatable = false)
    private LocalDateTime thoiGianTao;

    @Column(name = "THOI_GIAN_THANH_TOAN")
    private LocalDateTime thoiGianThanhToan;

    @PrePersist
    public void prePersist() {
        if (loaiGiaoDich == null) loaiGiaoDich = LoaiGiaoDich.THANH_TOAN;
        if (trangThai == null) trangThai = TrangThaiThanhToan.CHO_THANH_TOAN;
        if (thoiGianTao == null) thoiGianTao = LocalDateTime.now();
    }
}
