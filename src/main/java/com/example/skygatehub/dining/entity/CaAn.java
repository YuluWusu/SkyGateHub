package com.example.skygatehub.dining.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;

/**
 * Entity ánh xạ bảng CA_AN trong QL_Cong_Troi_Dong_Giang_MySQL.sql.
 */
@Entity
@Table(name = "CA_AN")
@Getter
@Setter
@NoArgsConstructor
public class CaAn {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "MA_CA_AN", length = 20, nullable = false, unique = true)
    private String maCaAn;

    @Column(name = "TEN_CA_AN", length = 60, nullable = false)
    private String tenCaAn;

    @Column(name = "GIO_BAT_DAU")
    private LocalTime gioBatDau;

    @Column(name = "GIO_KET_THUC")
    private LocalTime gioKetThuc;

    @Column(name = "TRANG_THAI", nullable = false)
    private Boolean trangThai = true;

    @PrePersist
    public void prePersist() {
        if (trangThai == null) trangThai = true;
    }
}
