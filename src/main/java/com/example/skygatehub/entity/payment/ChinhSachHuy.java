package com.example.skygatehub.entity.payment;

import com.example.skygatehub.common.enums.HanhDongHuy;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Entity ánh xạ bảng CHINH_SACH_HUY trong QL_Cong_Troi_Dong_Giang_MySQL.sql.
 */
@Entity
@Table(name = "CHINH_SACH_HUY")
@Getter
@Setter
@NoArgsConstructor
public class ChinhSachHuy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "MA_CHINH_SACH_HUY", length = 20, nullable = false, unique = true)
    private String maChinhSachHuy;

    @Column(name = "TEN_CHINH_SACH", length = 150, nullable = false)
    private String tenChinhSach;

    @Column(name = "SO_NGAY_TU", nullable = false)
    private Integer soNgayTu;

    @Column(name = "SO_NGAY_DEN")
    private Integer soNgayDen;

    @Enumerated(EnumType.STRING)
    @Column(name = "HANH_DONG", length = 25, nullable = false)
    private HanhDongHuy hanhDong;

    @Column(name = "TY_LE_HOAN", precision = 5, scale = 2)
    private BigDecimal tyLeHoan;

    @Column(name = "TRANG_THAI", nullable = false)
    private Boolean trangThai = true;

    @Column(name = "GHI_CHU", length = 500)
    private String ghiChu;

    @PrePersist
    public void prePersist() {
        if (trangThai == null) trangThai = true;
    }
}

