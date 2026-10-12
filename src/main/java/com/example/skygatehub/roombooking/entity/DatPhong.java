package com.example.skygatehub.roombooking.entity;

import com.example.skygatehub.order.entity.DonDat;
import com.example.skygatehub.room.entity.LoaiPhong;
import com.example.skygatehub.roombooking.enums.TrangThaiDatPhong;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "DAT_PHONG")
@Getter
@Setter
@NoArgsConstructor
public class DatPhong {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MA_DAT_PHONG")
    private Integer maDatPhong;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "MA_DON_DAT", nullable = false)
    private DonDat donDat;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "MA_LOAI_PHONG", nullable = false)
    private LoaiPhong loaiPhong;

    @Column(name = "NGAY_NHAN_PHONG", nullable = false)
    private LocalDate ngayNhanPhong;

    @Column(name = "NGAY_TRA_PHONG", nullable = false)
    private LocalDate ngayTraPhong;

    @Column(name = "SO_LUONG_PHONG")
    private Integer soLuongPhong;

    @Column(name = "SO_NGUOI_LON")
    private Integer soNguoiLon;

    @Column(name = "SO_TRE_EM")
    private Integer soTreEm;

    @Column(name = "GIA_AP_DUNG_MOI_DEM", precision = 18, scale = 2, nullable = false)
    private BigDecimal giaApDungMoiDem;

    @Column(name = "TIEN_PHU_THU", precision = 18, scale = 2)
    private BigDecimal tienPhuThu;

    @Column(name = "THANH_TIEN", precision = 18, scale = 2, nullable = false)
    private BigDecimal thanhTien;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "TRANG_THAI", length = 30)
    private TrangThaiDatPhong trangThai;

    @PrePersist
    public void prePersist() {
        if (soLuongPhong == null) {
            soLuongPhong = 1;
        }
        if (soNguoiLon == null) {
            soNguoiLon = 2;
        }
        if (soTreEm == null) {
            soTreEm = 0;
        }
        if (tienPhuThu == null) {
            tienPhuThu = BigDecimal.ZERO;
        }
        if (trangThai == null) {
            trangThai = TrangThaiDatPhong.CHO_PHAN_PHONG;
        }
    }
}
