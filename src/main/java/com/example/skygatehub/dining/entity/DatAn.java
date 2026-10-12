package com.example.skygatehub.dining.entity;

import com.example.skygatehub.dining.enums.CaAn;
import com.example.skygatehub.order.entity.DonDat;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.generator.EventType;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "DAT_AN")
@Getter
@Setter
@NoArgsConstructor
public class DatAn {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MA_DAT_AN")
    private Integer maDatAn;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "MA_DON_DAT", nullable = false)
    private DonDat donDat;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "MA_DICH_VU_AN", nullable = false)
    private DichVuAn dichVuAn;

    @Column(name = "NGAY_DUNG_BUA", nullable = false)
    private LocalDate ngayDungBua;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "CA_AN", length = 20, nullable = false)
    private CaAn caAn;

    @Column(name = "SO_SUAT", nullable = false)
    private Integer soSuat;

    @Column(name = "DON_GIA", precision = 18, scale = 2, nullable = false)
    private BigDecimal donGia;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MA_BAN")
    private BanAn banAn;

    @Generated(event = {EventType.INSERT, EventType.UPDATE})
    @Column(name = "THANH_TIEN", precision = 18, scale = 2, insertable = false, updatable = false)
    @Setter(AccessLevel.NONE)
    private BigDecimal thanhTien;

    @Column(name = "GHI_CHU", length = 200)
    private String ghiChu;
}
