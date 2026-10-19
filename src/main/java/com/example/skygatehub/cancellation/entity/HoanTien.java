package com.example.skygatehub.cancellation.entity;

import com.example.skygatehub.common.enums.TrangThaiHoanTien;
import com.example.skygatehub.payment.entity.ThanhToan;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity ánh xạ bảng HOAN_TIEN trong QL_Cong_Troi_Dong_Giang_MySQL.sql.
 * Ghi nhận việc hoàn tiền trỏ ngược lại THANH_TOAN gốc.
 */
@Entity
@Table(name = "HOAN_TIEN")
@Getter
@Setter
@NoArgsConstructor
public class HoanTien {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "MA_HOAN_TIEN", length = 20, nullable = false, unique = true)
    private String maHoanTien;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MA_HUY", referencedColumnName = "MA_HUY")
    private HuyDon huyDon;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MA_THANH_TOAN", referencedColumnName = "MA_THANH_TOAN", nullable = false)
    private ThanhToan thanhToan;

    @Column(name = "SO_TIEN", precision = 18, scale = 2, nullable = false)
    private BigDecimal soTien;

    @Enumerated(EnumType.STRING)
    @Column(name = "TRANG_THAI", length = 20, nullable = false)
    private TrangThaiHoanTien trangThai = TrangThaiHoanTien.CHO_HOAN;

    @Column(name = "MA_GIAO_DICH_HOAN", length = 120, unique = true)
    private String maGiaoDichHoan;

    @Column(name = "THOI_GIAN_TAO", nullable = false, updatable = false)
    private LocalDateTime thoiGianTao;

    @Column(name = "THOI_GIAN_HOAN")
    private LocalDateTime thoiGianHoan;

    @Column(name = "GHI_CHU", length = 500)
    private String ghiChu;

    @PrePersist
    public void prePersist() {
        if (trangThai == null) trangThai = TrangThaiHoanTien.CHO_HOAN;
        if (thoiGianTao == null) thoiGianTao = LocalDateTime.now();
    }
}
