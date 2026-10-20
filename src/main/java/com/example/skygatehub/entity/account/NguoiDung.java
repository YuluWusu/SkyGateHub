package com.example.skygatehub.entity.account;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Entity ánh xạ bảng NGUOI_DUNG.
 */
@Entity
@Table(name = "NGUOI_DUNG")
@Getter @Setter @NoArgsConstructor
public class NguoiDung {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "MA_NGUOI_DUNG", length = 20, nullable = false, unique = true)
    private String maNguoiDung;

    @Column(name = "TEN_DANG_NHAP", length = 80, nullable = false, unique = true)
    private String tenDangNhap;

    @Column(name = "MAT_KHAU_HASH", length = 255, nullable = false)
    private String matKhauHash;

    @Column(name = "HO_TEN", length = 120, nullable = false)
    private String hoTen;

    @Column(name = "EMAIL", length = 150)
    private String email;

    @Column(name = "SO_DIEN_THOAI", length = 20)
    private String soDienThoai;

    @Column(name = "TRANG_THAI", nullable = false)
    private Boolean trangThai;

    @Column(name = "CAN_DOI_MAT_KHAU", nullable = false)
    private Boolean canDoiMatKhau;

    @Column(name = "NGAY_TAO", nullable = false, updatable = false)
    private LocalDateTime ngayTao;

    @Column(name = "NGAY_CAP_NHAT", nullable = false)
    private LocalDateTime ngayCapNhat;

    @PrePersist
    public void prePersist() {
        if (trangThai == null) trangThai = true;
        if (canDoiMatKhau == null) canDoiMatKhau = true;
        if (ngayTao == null) ngayTao = LocalDateTime.now();
        if (ngayCapNhat == null) ngayCapNhat = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        ngayCapNhat = LocalDateTime.now();
    }
}

