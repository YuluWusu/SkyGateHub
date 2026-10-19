package com.example.skygatehub.roombooking.entity;

import com.example.skygatehub.common.enums.TrangThaiDatPhong;
import com.example.skygatehub.order.entity.ChiTietDon;
import com.example.skygatehub.room.entity.LoaiPhong;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Entity ánh xạ bảng DAT_PHONG trong QL_Cong_Troi_Dong_Giang_MySQL.sql.
 */
@Entity
@Table(name = "DAT_PHONG", uniqueConstraints = {
    @UniqueConstraint(name = "UQ_DAT_PHONG_MA_DON", columnNames = {"MA_DAT_PHONG", "MA_DON_DAT"}),
    @UniqueConstraint(name = "UQ_DAT_PHONG_MA_LOAI", columnNames = {"MA_DAT_PHONG", "MA_LOAI_PHONG"})
})
@Getter
@Setter
@NoArgsConstructor
public class DatPhong {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "MA_DAT_PHONG", length = 20, nullable = false, unique = true)
    private String maDatPhong;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
        @JoinColumn(name = "MA_CHI_TIET", referencedColumnName = "MA_CHI_TIET", nullable = false),
        @JoinColumn(name = "MA_DON_DAT", referencedColumnName = "MA_DON_DAT", nullable = false)
    })
    private ChiTietDon chiTietDon;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MA_LOAI_PHONG", referencedColumnName = "MA_LOAI_PHONG", nullable = false)
    private LoaiPhong loaiPhong;

    @Column(name = "NGAY_NHAN_PHONG", nullable = false)
    private LocalDate ngayNhanPhong;

    @Column(name = "NGAY_TRA_PHONG", nullable = false)
    private LocalDate ngayTraPhong;

    @Column(name = "SO_LUONG_PHONG", nullable = false)
    private Integer soLuongPhong;

    @Column(name = "SO_KHACH", nullable = false)
    private Integer soKhach;

    @Enumerated(EnumType.STRING)
    @Column(name = "TRANG_THAI", length = 25, nullable = false)
    private TrangThaiDatPhong trangThai = TrangThaiDatPhong.CHO_XAC_NHAN;

    @PrePersist
    public void prePersist() {
        if (trangThai == null) trangThai = TrangThaiDatPhong.CHO_XAC_NHAN;
    }
}
