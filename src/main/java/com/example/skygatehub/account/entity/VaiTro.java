package com.example.skygatehub.account.entity;

import com.example.skygatehub.common.enums.RoleName;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "VAI_TRO")
@Getter
@Setter
@NoArgsConstructor
public class VaiTro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MA_VAI_TRO")
    private Integer maVaiTro;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "TEN_VAI_TRO", length = 50, nullable = false, unique = true)
    private RoleName tenVaiTro;

    @Column(name = "MO_TA", length = 200)
    private String moTa;
}
