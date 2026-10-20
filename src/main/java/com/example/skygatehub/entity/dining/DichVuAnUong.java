package com.example.skygatehub.entity.dining;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity ánh xạ bảng DICH_VU_AN_UONG trong QL_Cong_Troi_Dong_Giang_MySQL.sql.
 */
@Entity
@Table(name = "DICH_VU_AN_UONG")
@Getter
@Setter
@NoArgsConstructor
public class DichVuAnUong {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "MA_DICH_VU", length = 20, nullable = false, unique = true)
    private String maDichVu;

    @Column(name = "TEN_DICH_VU", length = 150, nullable = false)
    private String tenDichVu;

    @Column(name = "LOAI_DICH_VU", length = 30, nullable = false)
    private String loaiDichVu;

    @Column(name = "MO_TA", length = 500)
    private String moTa;

    @Column(name = "GIA_DICH_VU", precision = 18, scale = 2)
    private BigDecimal giaDichVu;

    @Column(name = "TRANG_THAI", nullable = false)
    private Boolean trangThai = false;

    @Column(name = "NGAY_TAO", nullable = false, updatable = false)
    private LocalDateTime ngayTao;

    @PrePersist
    public void prePersist() {
        if (trangThai == null) trangThai = false;
        if (ngayTao == null) ngayTao = LocalDateTime.now();
    }
}

