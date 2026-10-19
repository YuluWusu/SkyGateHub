package com.example.skygatehub.ticket.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Entity ánh xạ bảng NGAY_DAC_BIET trong QL_Cong_Troi_Dong_Giang_MySQL.sql.
 */
@Entity
@Table(name = "NGAY_DAC_BIET")
@Getter
@Setter
@NoArgsConstructor
public class NgayDacBiet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "MA_NGAY_DAC_BIET", length = 20, nullable = false, unique = true)
    private String maNgayDacBiet;

    @Column(name = "NGAY_AP_DUNG", nullable = false)
    private LocalDate ngayApDung;

    @Column(name = "TEN_NGAY", length = 150, nullable = false)
    private String tenNgay;

    @Column(name = "LOAI_NGAY", length = 30, nullable = false)
    private String loaiNgay = "LE_TET";

    @Column(name = "GHI_CHU", length = 500)
    private String ghiChu;

    @Column(name = "TRANG_THAI", nullable = false)
    private Boolean trangThai = true;

    @PrePersist
    public void prePersist() {
        if (loaiNgay == null) loaiNgay = "LE_TET";
        if (trangThai == null) trangThai = true;
    }
}
