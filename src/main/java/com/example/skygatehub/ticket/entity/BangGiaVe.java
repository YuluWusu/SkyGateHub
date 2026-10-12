package com.example.skygatehub.ticket.entity;

import com.example.skygatehub.common.enums.LoaiKhach;
import com.example.skygatehub.common.enums.LoaiNgay;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;

@Entity
@Table(name = "BANG_GIA_VE", uniqueConstraints = {
        @UniqueConstraint(name = "UQ_GIA_VE", columnNames = {"MA_SAN_PHAM", "LOAI_KHACH", "LOAI_NGAY"})
})
@Getter
@Setter
@NoArgsConstructor
public class BangGiaVe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MA_GIA")
    private Integer maGia;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "MA_SAN_PHAM", nullable = false)
    private SanPhamVe sanPham;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "LOAI_KHACH", length = 20, nullable = false)
    private LoaiKhach loaiKhach;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "LOAI_NGAY", length = 20, nullable = false)
    private LoaiNgay loaiNgay;

    @Column(name = "GIA_TIEN", precision = 18, scale = 2, nullable = false)
    private BigDecimal giaTien;
}
