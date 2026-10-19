package com.example.skygatehub.dining.entity;

import com.example.skygatehub.common.enums.TrangThaiDatBan;
import com.example.skygatehub.order.entity.ChiTietDon;
import com.example.skygatehub.order.entity.DonDat;
import com.example.skygatehub.order.entity.QuyenLoiDon;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Entity ánh xạ bảng DAT_BAN trong QL_Cong_Troi_Dong_Giang_MySQL.sql.
 */
@Entity
@Table(name = "DAT_BAN", uniqueConstraints = {
    @UniqueConstraint(name = "UQ_DAT_BAN_MA_DON", columnNames = {"MA_DAT_BAN", "MA_DON_DAT"}),
    @UniqueConstraint(name = "UQ_DAT_BAN_BAN_NGAY_CA", columnNames = {"MA_BAN_AN_DANG_GIU", "NGAY_AN", "MA_CA_AN"})
})
@Getter
@Setter
@NoArgsConstructor
public class DatBan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "MA_DAT_BAN", length = 20, nullable = false, unique = true)
    private String maDatBan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MA_DON_DAT", referencedColumnName = "MA_DON_DAT", nullable = false)
    private DonDat donDat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
        @JoinColumn(name = "MA_DICH_VU", referencedColumnName = "MA_DICH_VU", nullable = false),
        @JoinColumn(name = "MA_CA_AN", referencedColumnName = "MA_CA_AN", nullable = false)
    })
    private DichVuCaAn dichVuCaAn;

    @Column(name = "NGAY_AN", nullable = false)
    private LocalDate ngayAn;

    @Column(name = "SO_KHACH", nullable = false)
    private Integer soKhach;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MA_BAN_AN", referencedColumnName = "MA_BAN_AN")
    private BanAn banAn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
        @JoinColumn(name = "MA_CHI_TIET", referencedColumnName = "MA_CHI_TIET"),
        @JoinColumn(name = "MA_DON_DAT", referencedColumnName = "MA_DON_DAT", insertable = false, updatable = false)
    })
    private ChiTietDon chiTietDon;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
        @JoinColumn(name = "MA_QUYEN_LOI", referencedColumnName = "MA_QUYEN_LOI"),
        @JoinColumn(name = "MA_DON_DAT", referencedColumnName = "MA_DON_DAT", insertable = false, updatable = false)
    })
    private QuyenLoiDon quyenLoiDon;

    @Enumerated(EnumType.STRING)
    @Column(name = "TRANG_THAI", length = 25, nullable = false)
    private TrangThaiDatBan trangThai = TrangThaiDatBan.CHO_XAC_NHAN;

    /**
     * Cột tính toán GENERATED ALWAYS AS (CASE WHEN TRANG_THAI <> 'DA_HUY' THEN MA_BAN_AN ELSE NULL END) STORED
     */
    @Column(name = "MA_BAN_AN_DANG_GIU", insertable = false, updatable = false)
    private String maBanAnDangGiu;

    @Column(name = "GHI_CHU", length = 500)
    private String ghiChu;

    @PrePersist
    public void prePersist() {
        if (trangThai == null) trangThai = TrangThaiDatBan.CHO_XAC_NHAN;
    }
}
