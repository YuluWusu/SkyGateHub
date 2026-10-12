package com.example.skygatehub.dining.entity;

import com.example.skygatehub.dining.enums.LoaiDichVuAn;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;

@Entity
@Table(name = "DICH_VU_AN")
@Getter
@Setter
@NoArgsConstructor
public class DichVuAn {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MA_DICH_VU_AN")
    private Integer maDichVuAn;

    @Column(name = "TEN_DICH_VU", length = 150, nullable = false)
    private String tenDichVu;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "LOAI_DICH_VU", length = 30, nullable = false)
    private LoaiDichVuAn loaiDichVu;

    @Column(name = "GIA_TIEN", precision = 18, scale = 2, nullable = false)
    private BigDecimal giaTien;

    @Column(name = "MO_TA", length = 500)
    private String moTa;
}
