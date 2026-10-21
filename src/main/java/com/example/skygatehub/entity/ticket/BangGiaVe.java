package com.example.skygatehub.entity.ticket;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Entity ánh xạ bảng BANG_GIA_VE trong QL_Cong_Troi_Dong_Giang_MySQL.sql.
 */
@Entity
@Table(name = "BANG_GIA_VE")
@Getter
@Setter
@NoArgsConstructor
public class BangGiaVe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "MA_BANG_GIA", length = 20, nullable = false, unique = true)
    private String maBangGia;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MA_LOAI_VE", referencedColumnName = "MA_LOAI_VE", nullable = false)
    private LoaiVe loaiVe;

    @Column(name = "TEN_BANG_GIA", length = 120, nullable = false)
    private String tenBangGia;

    @Column(name = "DIEU_KIEN_GIA", length = 30, nullable = false)
    private String dieuKienGia = "MAC_DINH";

    @Column(name = "HIEU_LUC_TU", nullable = false)
    private LocalDate hieuLucTu;

    @Column(name = "HIEU_LUC_DEN")
    private LocalDate hieuLucDen;

    @Column(name = "DON_GIA", precision = 18, scale = 2, nullable = false)
    private BigDecimal donGia;

    @Column(name = "UU_TIEN", nullable = false)
    private Integer uuTien = 0;

    @Column(name = "TRANG_THAI", nullable = false)
    private Boolean trangThai = false;

    @Column(name = "GHI_CHU", length = 500)
    private String ghiChu;

    @PrePersist
    public void prePersist() {
        if (dieuKienGia == null) {
            dieuKienGia = "MAC_DINH";
        }
        if (uuTien == null) {
            uuTien = 0;
        }
        if (trangThai == null) {
            trangThai = false;
        }
    }
}
