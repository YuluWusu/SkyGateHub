package com.example.skygatehub.entity.room;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity ánh xạ bảng LOAI_PHONG trong QL_Cong_Troi_Dong_Giang_MySQL.sql.
 */
@Entity
@Table(name = "LOAI_PHONG")
@Getter
@Setter
@NoArgsConstructor
public class LoaiPhong {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "MA_LOAI_PHONG", length = 20, nullable = false, unique = true)
    private String maLoaiPhong;

    @Column(name = "TEN_LOAI_PHONG", length = 120, nullable = false)
    private String tenLoaiPhong;

    @Column(name = "MO_TA", length = 500)
    private String moTa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MA_KHU")
    private KhuLuuTru khuLuuTru;

    @Column(name = "DIEN_TICH")
    private Integer dienTich;

    @Column(name = "LOAI_GIUONG", length = 50)
    private String loaiGiuong = "DBL";

    @Column(name = "SUC_CHUA_NGUOI_LON", nullable = false)
    private Integer sucChuaNguoiLon;

    @Column(name = "SUC_CHUA_TRE_EM")
    private Integer sucChuaTreEm = 1;

    @Column(name = "GIA_CO_BAN", precision = 18, scale = 2)
    private BigDecimal giaCoBan;

    @Column(name = "HINH_ANH", length = 255)
    private String hinhAnh;

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

