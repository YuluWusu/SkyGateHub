package com.example.skygatehub.entity.room;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * [Module 3 - Quản trị Lưu trú] Entity ánh xạ bảng LOAI_PHONG (danh mục 15 hạng phòng thực tế).
 * Giữ giá niêm yết cơ bản/đêm (GIA_CO_BAN), sức chứa người lớn/trẻ em và đường dẫn ảnh đại diện.
 */
@Entity
@Table(name = "LOAI_PHONG")
@Getter
@Setter
@NoArgsConstructor
public class LoaiPhong {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "MA_LOAI_PHONG", length = 20, nullable = false, unique = true)
    private String maLoaiPhong;

    @Column(name = "TEN_LOAI_PHONG", length = 120, nullable = false)
    private String tenLoaiPhong;

    @Column(name = "MO_TA", length = 500)
    private String moTa;

    // Nạp LAZY để tránh kéo dư dữ liệu khi chỉ liệt kê phòng; dùng JOIN FETCH ở Repository khi cần hiển thị Khu
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MA_KHU")
    private KhuLuuTru khuLuuTru;

    @Column(name = "DIEN_TICH")
    private Integer dienTich;

    @Column(name = "LOAI_GIUONG", length = 50)
    private String loaiGiuong = "DBL";

    @Column(name = "SUC_CHUA_NGUOI_LON", nullable = false)
    private Integer sucChuaNguoiLon;

    @Column(name = "SUC_CHUA_TRE_EM")
    private Integer sucChuaTreEm = 1;

    @Column(name = "GIA_CO_BAN", precision = 18, scale = 2)
    private BigDecimal giaCoBan;

    @Column(name = "HINH_ANH", length = 255)
    private String hinhAnh;

    @Column(name = "TRANG_THAI", nullable = false)
    private Boolean trangThai = false;

    @Column(name = "NGAY_TAO", nullable = false, updatable = false)
    private LocalDateTime ngayTao;

    @OneToMany(mappedBy = "loaiPhong", fetch = FetchType.LAZY)
    private java.util.List<Phong> phongs = new java.util.ArrayList<>();

    @PrePersist
    public void prePersist() {
        if (trangThai == null) trangThai = false;
        if (ngayTao == null) ngayTao = LocalDateTime.now();
    }
}

