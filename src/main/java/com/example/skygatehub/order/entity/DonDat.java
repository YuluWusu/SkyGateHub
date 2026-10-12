package com.example.skygatehub.order.entity;

import com.example.skygatehub.account.entity.NguoiDung;
import com.example.skygatehub.order.enums.TrangThaiDon;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "DON_DAT")
@Getter
@Setter
@NoArgsConstructor
public class DonDat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MA_DON_DAT")
    private Integer maDonDat;

    @Column(name = "MA_DON_CODE", length = 30, nullable = false, unique = true)
    private String maDonCode;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "MA_KHACH_HANG", nullable = false)
    private NguoiDung khachHang;

    @Column(name = "NGAY_DAT")
    private LocalDateTime ngayDat;

    @Column(name = "TONG_TIEN", precision = 18, scale = 2)
    private BigDecimal tongTien;

    @Column(name = "TIEN_DA_COC", precision = 18, scale = 2)
    private BigDecimal tienDaCoc;

    @Column(name = "TIEN_CON_LAI", precision = 18, scale = 2)
    private BigDecimal tienConLai;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "TRANG_THAI_DON", length = 30)
    private TrangThaiDon trangThaiDon;

    @Column(name = "GHI_CHU", length = 500)
    private String ghiChu;

    @PrePersist
    public void prePersist() {
        if (ngayDat == null) {
            ngayDat = LocalDateTime.now();
        }
        if (tongTien == null) {
            tongTien = BigDecimal.ZERO;
        }
        if (tienDaCoc == null) {
            tienDaCoc = BigDecimal.ZERO;
        }
        if (tienConLai == null) {
            tienConLai = BigDecimal.ZERO;
        }
        if (trangThaiDon == null) {
            trangThaiDon = TrangThaiDon.CHO_COC;
        }
    }
}
