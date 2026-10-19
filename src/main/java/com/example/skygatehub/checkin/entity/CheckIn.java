package com.example.skygatehub.checkin.entity;

import com.example.skygatehub.account.entity.NguoiDung;
import com.example.skygatehub.common.enums.LoaiCheckIn;
import com.example.skygatehub.common.enums.TrangThaiCheckIn;
import com.example.skygatehub.dining.entity.DatBan;
import com.example.skygatehub.order.entity.DonDat;
import com.example.skygatehub.order.entity.QuyenLoiDon;
import com.example.skygatehub.order.entity.Ve;
import com.example.skygatehub.roombooking.entity.DatPhong;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Entity ánh xạ bảng CHECK_IN trong QL_Cong_Troi_Dong_Giang_MySQL.sql.
 * Check-in đa khu vực (Vé, Phòng, Ăn uống).
 * Unique constraint trên MA_VE, MA_DAT_PHONG, MA_DAT_BAN chống quét trùng.
 */
@Entity
@Table(name = "CHECK_IN")
@Getter
@Setter
@NoArgsConstructor
public class CheckIn {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "MA_CHECK_IN", length = 20, nullable = false, unique = true)
    private String maCheckIn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MA_DON_DAT", referencedColumnName = "MA_DON_DAT", nullable = false)
    private DonDat donDat;

    @Enumerated(EnumType.STRING)
    @Column(name = "LOAI_CHECK_IN", length = 20, nullable = false)
    private LoaiCheckIn loaiCheckIn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
        @JoinColumn(name = "MA_VE", referencedColumnName = "MA_VE", unique = true),
        @JoinColumn(name = "MA_DON_DAT", referencedColumnName = "MA_DON_DAT", insertable = false, updatable = false)
    })
    private Ve ve;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
        @JoinColumn(name = "MA_DAT_PHONG", referencedColumnName = "MA_DAT_PHONG", unique = true),
        @JoinColumn(name = "MA_DON_DAT", referencedColumnName = "MA_DON_DAT", insertable = false, updatable = false)
    })
    private DatPhong datPhong;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
        @JoinColumn(name = "MA_DAT_BAN", referencedColumnName = "MA_DAT_BAN", unique = true),
        @JoinColumn(name = "MA_DON_DAT", referencedColumnName = "MA_DON_DAT", insertable = false, updatable = false)
    })
    private DatBan datBan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
        @JoinColumn(name = "MA_QUYEN_LOI", referencedColumnName = "MA_QUYEN_LOI"),
        @JoinColumn(name = "MA_DON_DAT", referencedColumnName = "MA_DON_DAT", insertable = false, updatable = false)
    })
    private QuyenLoiDon quyenLoiDon;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MA_NHAN_VIEN", referencedColumnName = "MA_NGUOI_DUNG", nullable = false)
    private NguoiDung nhanVien;

    @Column(name = "SO_LUONG", nullable = false)
    private Integer soLuong = 1;

    @Column(name = "THOI_GIAN_CHECK_IN", nullable = false, updatable = false)
    private LocalDateTime thoiGianCheckIn;

    @Enumerated(EnumType.STRING)
    @Column(name = "TRANG_THAI", length = 20, nullable = false)
    private TrangThaiCheckIn trangThai = TrangThaiCheckIn.DA_CHECK_IN;

    @Column(name = "GHI_CHU", length = 500)
    private String ghiChu;

    @PrePersist
    public void prePersist() {
        if (soLuong == null) soLuong = 1;
        if (thoiGianCheckIn == null) thoiGianCheckIn = LocalDateTime.now();
        if (trangThai == null) trangThai = TrangThaiCheckIn.DA_CHECK_IN;
    }
}
