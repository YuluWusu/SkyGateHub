package com.example.skygatehub.ticket.entity;

import com.example.skygatehub.common.enums.LoaiVeType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity ánh xạ bảng LOAI_VE trong QL_Cong_Troi_Dong_Giang_MySQL.sql.
 */
@Entity
@Table(name = "LOAI_VE")
@Getter
@Setter
@NoArgsConstructor
public class LoaiVe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "MA_LOAI_VE", length = 20, nullable = false, unique = true)
    private String maLoaiVe;

    @Column(name = "TEN_LOAI_VE", length = 150, nullable = false)
    private String tenLoaiVe;

    @Enumerated(EnumType.STRING)
    @Column(name = "LOAI_VE", length = 20, nullable = false)
    private LoaiVeType loaiVe;

    @Column(name = "MO_TA", length = 500)
    private String moTa;

    @Column(name = "GIA_VE", precision = 18, scale = 2)
    private BigDecimal giaVe;

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
