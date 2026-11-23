package com.example.skygatehub.entity.room;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * [Module 3 - Quản trị Lưu trú] Entity ánh xạ bảng KHU_LUU_TRU (4 phân khu nghỉ dưỡng).
 * Quan hệ: 1 KhuLuuTru -> N LoaiPhong (quản lý chuỗi kết nối qua MA_KHU).
 */
@Entity
@Table(name = "KHU_LUU_TRU")
@Getter
@Setter
@NoArgsConstructor
public class KhuLuuTru {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MA_KHU")
    private Integer maKhu;

    @Column(name = "TEN_KHU", length = 100, nullable = false, unique = true)
    private String tenKhu;

    @Column(name = "HANG_SAO")
    private Integer hangSao = 5;

    @Column(name = "DIA_CHI", length = 255)
    private String diaChi;

    @Column(name = "EMAIL", length = 100)
    private String email;

    @Column(name = "HOTLINE", length = 20)
    private String hotline;

    @Column(name = "HINH_ANH", length = 255)
    private String hinhAnh;
}

