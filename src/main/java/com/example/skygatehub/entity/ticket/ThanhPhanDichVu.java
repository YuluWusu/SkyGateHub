package com.example.skygatehub.entity.ticket;

import com.example.skygatehub.common.enums.LoaiThanhPhan;
import com.example.skygatehub.entity.dining.DichVuAnUong;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

/**
 * Entity ánh xạ bảng THANH_PHAN_DICH_VU trong QL_Cong_Troi_Dong_Giang_MySQL.sql.
 */
@Entity
@Table(name = "THANH_PHAN_DICH_VU")
@Getter
@Setter
@NoArgsConstructor
public class ThanhPhanDichVu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "MA_THANH_PHAN", length = 20, nullable = false, unique = true)
    private String maThanhPhan;

    @Column(name = "TEN_THANH_PHAN", length = 150, nullable = false)
    private String tenThanhPhan;

    @Enumerated(EnumType.STRING)
    @Column(name = "LOAI_THANH_PHAN", length = 30, nullable = false)
    private LoaiThanhPhan loaiThanhPhan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MA_LOAI_VE", referencedColumnName = "MA_LOAI_VE")
    private LoaiVe loaiVe;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MA_DV_AN_UONG", referencedColumnName = "MA_DICH_VU")
    private DichVuAnUong dichVuAnUong;

    @Column(name = "MO_TA", length = 500)
    private String moTa;

    @Column(name = "TRANG_THAI", nullable = false)
    private Boolean trangThai = true;

    @PrePersist
    public void prePersist() {
        if (trangThai == null) {
            trangThai = true;
        }
    }
}
