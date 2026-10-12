package com.example.skygatehub.room.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "LOAI_PHONG")
@Getter
@Setter
@NoArgsConstructor
public class LoaiPhong {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MA_LOAI_PHONG")
    private Integer maLoaiPhong;

    @Column(name = "TEN_LOAI_PHONG", length = 100, nullable = false, unique = true)
    private String tenLoaiPhong;

    @Column(name = "SUC_CHUA_NGUOI_LON")
    private Integer sucChuaNguoiLon;

    @Column(name = "GIA_CO_BAN", precision = 18, scale = 2, nullable = false)
    private BigDecimal giaCoBan;

    @Column(name = "TY_LE_PHU_THU_CUOI_TUAN", precision = 5, scale = 2)
    private BigDecimal tyLePhuThuCuoiTuan;

    @Column(name = "MO_TA", length = 500)
    private String moTa;

    @PrePersist
    public void prePersist() {
        if (sucChuaNguoiLon == null) {
            sucChuaNguoiLon = 2;
        }
        if (tyLePhuThuCuoiTuan == null) {
            tyLePhuThuCuoiTuan = new BigDecimal("0.20");
        }
    }
}
