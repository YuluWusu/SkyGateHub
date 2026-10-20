package com.example.skygatehub.entity.account;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entity ánh xạ bảng VAI_TRO.
 */
@Entity
@Table(name = "VAI_TRO")
@Getter @Setter @NoArgsConstructor
public class VaiTro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "MA_VAI_TRO", length = 20, nullable = false, unique = true)
    private String maVaiTro;

    @Column(name = "TEN_VAI_TRO", length = 60, nullable = false, unique = true)
    private String tenVaiTro;

    @Column(name = "MO_TA", length = 255)
    private String moTa;
}

