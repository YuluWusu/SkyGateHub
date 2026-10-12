package com.example.skygatehub.ticket.entity;

import com.example.skygatehub.ticket.enums.TrangThaiSanPham;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "SAN_PHAM_VE")
@Getter
@Setter
@NoArgsConstructor
public class SanPhamVe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MA_SAN_PHAM")
    private Integer maSanPham;

    @Column(name = "MA_CODE", length = 30, nullable = false, unique = true)
    private String maCode;

    @Column(name = "TEN_SAN_PHAM", length = 150, nullable = false)
    private String tenSanPham;

    @Column(name = "LA_COMBO")
    private Boolean laCombo;

    @Column(name = "MO_TA", length = 500)
    private String moTa;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "TRANG_THAI", length = 20)
    private TrangThaiSanPham trangThai;

    @PrePersist
    public void prePersist() {
        if (laCombo == null) {
            laCombo = false;
        }
        if (trangThai == null) {
            trangThai = TrangThaiSanPham.DANG_BAN;
        }
    }
}
