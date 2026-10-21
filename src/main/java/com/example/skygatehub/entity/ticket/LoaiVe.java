package com.example.skygatehub.entity.ticket;

import com.example.skygatehub.common.enums.LoaiVeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
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
        if (trangThai == null) {
            trangThai = false;
        }
        if (ngayTao == null) {
            ngayTao = LocalDateTime.now();
        }
    }

    public boolean isCombo() {
        return this.loaiVe == LoaiVeType.TRON_GOI;
    }

    public String getTenPhanLoai() {
        if (this.loaiVe == null) {
            return "Khác";
        }
        return switch (this.loaiVe) {
            case NGUOI_LON -> "Vé người lớn";
            case TRE_EM -> "Vé trẻ em";
            case TRON_GOI -> "Combo trọn gói";
            case KHAC -> "Vé dịch vụ";
        };
    }
}
