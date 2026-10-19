package com.example.skygatehub.order.entity;

import com.example.skygatehub.common.enums.LoaiDichVuDon;
import com.example.skygatehub.common.enums.TrangThaiChiTiet;
import com.example.skygatehub.dining.entity.DichVuAnUong;
import com.example.skygatehub.room.entity.LoaiPhong;
import com.example.skygatehub.ticket.entity.LoaiVe;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entity ánh xạ bảng CHI_TIET_DON trong QL_Cong_Troi_Dong_Giang_MySQL.sql.
 * Nguồn tiền duy nhất của hệ thống.
 */
@Entity
@Table(name = "CHI_TIET_DON", uniqueConstraints = {
    @UniqueConstraint(name = "UQ_CTD_MA_CHI_TIET_DON", columnNames = {"MA_CHI_TIET", "MA_DON_DAT"})
})
@Getter
@Setter
@NoArgsConstructor
public class ChiTietDon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "MA_CHI_TIET", length = 20, nullable = false, unique = true)
    private String maChiTiet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MA_DON_DAT", referencedColumnName = "MA_DON_DAT", nullable = false)
    private DonDat donDat;

    @Enumerated(EnumType.STRING)
    @Column(name = "LOAI_DICH_VU", length = 20, nullable = false)
    private LoaiDichVuDon loaiDichVu;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MA_LOAI_VE", referencedColumnName = "MA_LOAI_VE")
    private LoaiVe loaiVe;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MA_LOAI_PHONG", referencedColumnName = "MA_LOAI_PHONG")
    private LoaiPhong loaiPhong;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MA_DV_AN_UONG", referencedColumnName = "MA_DICH_VU")
    private DichVuAnUong dichVuAnUong;

    @Column(name = "TEN_SAN_PHAM", length = 200, nullable = false)
    private String tenSanPham;

    @Column(name = "SO_LUONG", nullable = false)
    private Integer soLuong;

    @Column(name = "DON_GIA", precision = 18, scale = 2, nullable = false)
    private BigDecimal donGia;

    /**
     * Cột tính toán GENERATED ALWAYS AS (SO_LUONG * DON_GIA) STORED
     */
    @Column(name = "THANH_TIEN", insertable = false, updatable = false)
    private BigDecimal thanhTien;

    @Enumerated(EnumType.STRING)
    @Column(name = "TRANG_THAI", length = 20, nullable = false)
    private TrangThaiChiTiet trangThai = TrangThaiChiTiet.HIEU_LUC;

    @Column(name = "NGAY_SU_DUNG")
    private LocalDate ngaySuDung;

    @Column(name = "NGAY_TAO", nullable = false, updatable = false)
    private LocalDateTime ngayTao;

    @PrePersist
    public void prePersist() {
        if (trangThai == null) trangThai = TrangThaiChiTiet.HIEU_LUC;
        if (ngayTao == null) ngayTao = LocalDateTime.now();
    }
}
