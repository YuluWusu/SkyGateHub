package com.example.skygatehub.payment.entity;

import com.example.skygatehub.account.entity.NguoiDung;
import com.example.skygatehub.order.entity.DonDat;
import com.example.skygatehub.payment.enums.LoaiThanhToan;
import com.example.skygatehub.payment.enums.PhuongThucThanhToan;
import com.example.skygatehub.payment.enums.TrangThaiThanhToan;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "THANH_TOAN")
@Getter
@Setter
@NoArgsConstructor
public class ThanhToan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MA_THANH_TOAN")
    private Integer maThanhToan;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "MA_DON_DAT", nullable = false)
    private DonDat donDat;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "LOAI_THANH_TOAN", length = 30, nullable = false)
    private LoaiThanhToan loaiThanhToan;

    @Column(name = "SO_TIEN", precision = 18, scale = 2, nullable = false)
    private BigDecimal soTien;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "PHUONG_THUC", length = 30, nullable = false)
    private PhuongThucThanhToan phuongThuc;

    @Column(name = "MA_GIAO_DICH", length = 50)
    private String maGiaoDich;

    @Column(name = "THOI_GIAN_GIAO_DICH")
    private LocalDateTime thoiGianGiaoDich;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "TRANG_THAI", length = 20)
    private TrangThaiThanhToan trangThai;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "NGUOI_THU")
    private NguoiDung nguoiThu;

    @PrePersist
    public void prePersist() {
        if (thoiGianGiaoDich == null) {
            thoiGianGiaoDich = LocalDateTime.now();
        }
        if (trangThai == null) {
            trangThai = TrangThaiThanhToan.THANH_CONG;
        }
    }
}
