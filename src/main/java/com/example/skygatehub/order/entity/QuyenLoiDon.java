package com.example.skygatehub.order.entity;

import com.example.skygatehub.common.enums.TrangThaiQuyenLoi;
import com.example.skygatehub.ticket.entity.ThanhPhanDichVu;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Entity ánh xạ bảng QUYEN_LOI_DON trong QL_Cong_Troi_Dong_Giang_MySQL.sql.
 * Chụp thành phần combo để quản lý lượt sử dụng chống tính trùng buffet.
 */
@Entity
@Table(name = "QUYEN_LOI_DON", uniqueConstraints = {
    @UniqueConstraint(name = "UQ_QUYEN_LOI_MA_DON", columnNames = {"MA_QUYEN_LOI", "MA_DON_DAT"})
})
@Getter
@Setter
@NoArgsConstructor
public class QuyenLoiDon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "MA_QUYEN_LOI", length = 20, nullable = false, unique = true)
    private String maQuyenLoi;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
        @JoinColumn(name = "MA_CHI_TIET", referencedColumnName = "MA_CHI_TIET", nullable = false),
        @JoinColumn(name = "MA_DON_DAT", referencedColumnName = "MA_DON_DAT", nullable = false)
    })
    private ChiTietDon chiTietDon;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MA_THANH_PHAN", referencedColumnName = "MA_THANH_PHAN", nullable = false)
    private ThanhPhanDichVu thanhPhanDichVu;

    @Column(name = "TEN_THANH_PHAN_CHUP", length = 150, nullable = false)
    private String tenThanhPhanChup;

    @Column(name = "LOAI_THANH_PHAN_CHUP", length = 30, nullable = false)
    private String loaiThanhPhanChup;

    @Column(name = "SO_LUONG_TOI_DA", nullable = false)
    private Integer soLuongToiDa;

    @Column(name = "SO_LUONG_DA_DUNG", nullable = false)
    private Integer soLuongDaDung = 0;

    @Column(name = "THEO_DOI_SU_DUNG", nullable = false)
    private Boolean theoDoiSuDung = true;

    @Enumerated(EnumType.STRING)
    @Column(name = "TRANG_THAI", length = 20, nullable = false)
    private TrangThaiQuyenLoi trangThai = TrangThaiQuyenLoi.HIEU_LUC;

    @Column(name = "NGAY_TAO", nullable = false, updatable = false)
    private LocalDateTime ngayTao;

    @PrePersist
    public void prePersist() {
        if (soLuongDaDung == null) soLuongDaDung = 0;
        if (theoDoiSuDung == null) theoDoiSuDung = true;
        if (trangThai == null) trangThai = TrangThaiQuyenLoi.HIEU_LUC;
        if (ngayTao == null) ngayTao = LocalDateTime.now();
    }
}
