package com.example.skygatehub.ticket.entity;

import com.example.skygatehub.order.entity.DonDat;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Generated;
import org.hibernate.generator.EventType;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "DAT_VE")
@Getter
@Setter
@NoArgsConstructor
public class DatVe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MA_DAT_VE")
    private Integer maDatVe;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "MA_DON_DAT", nullable = false)
    private DonDat donDat;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "MA_SAN_PHAM", nullable = false)
    private SanPhamVe sanPham;

    @Column(name = "NGAY_SU_DUNG", nullable = false)
    private LocalDate ngaySuDung;

    @Column(name = "SO_LUONG_NGUOI_LON")
    private Integer soLuongNguoiLon;

    @Column(name = "SO_LUONG_TRE_EM")
    private Integer soLuongTreEm;

    @Column(name = "DON_GIA_NGUOI_LON", precision = 18, scale = 2, nullable = false)
    private BigDecimal donGiaNguoiLon;

    @Column(name = "DON_GIA_TRE_EM", precision = 18, scale = 2, nullable = false)
    private BigDecimal donGiaTreEm;

    @Generated(event = {EventType.INSERT, EventType.UPDATE})
    @Column(name = "THANH_TIEN", precision = 18, scale = 2, insertable = false, updatable = false)
    @Setter(AccessLevel.NONE)
    private BigDecimal thanhTien;

    @PrePersist
    public void prePersist() {
        if (soLuongNguoiLon == null) {
            soLuongNguoiLon = 0;
        }
        if (soLuongTreEm == null) {
            soLuongTreEm = 0;
        }
    }
}
