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
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entity ánh xạ bảng COMBO_THANH_PHAN trong QL_Cong_Troi_Dong_Giang_MySQL.sql.
 */
@Entity
@Table(name = "COMBO_THANH_PHAN", uniqueConstraints = {
    @UniqueConstraint(name = "UQ_COMBO_THANH_PHAN", columnNames = {"MA_LOAI_VE", "MA_THANH_PHAN"})
})
@Getter
@Setter
@NoArgsConstructor
public class ComboThanhPhan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MA_LOAI_VE", referencedColumnName = "MA_LOAI_VE", nullable = false)
    private LoaiVe loaiVe;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MA_THANH_PHAN", referencedColumnName = "MA_THANH_PHAN", nullable = false)
    private ThanhPhanDichVu thanhPhanDichVu;

    @Column(name = "SO_LUONG_MOI_COMBO", nullable = false)
    private Integer soLuongMoiCombo = 1;

    @Column(name = "THEO_DOI_SU_DUNG", nullable = false)
    private Boolean theoDoiSuDung = true;

    @Column(name = "GHI_CHU", length = 500)
    private String ghiChu;

    @PrePersist
    public void prePersist() {
        if (soLuongMoiCombo == null) {
            soLuongMoiCombo = 1;
        }
        if (theoDoiSuDung == null) {
            theoDoiSuDung = true;
        }
    }
}
