package com.example.skygatehub.account.entity;

import com.example.skygatehub.account.enums.TrangThaiNguoiDung;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "NGUOI_DUNG")
@Getter
@Setter
@NoArgsConstructor
public class NguoiDung {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MA_NGUOI_DUNG")
    private Integer maNguoiDung;

    @Column(name = "TEN_DANG_NHAP", length = 50, nullable = false, unique = true)
    private String tenDangNhap;

    @Column(name = "MAT_KHAU", length = 255, nullable = false)
    private String matKhau;

    @Column(name = "HO_TEN", length = 100, nullable = false)
    private String hoTen;

    @Column(name = "SO_DIEN_THOAI", length = 15, nullable = false)
    private String soDienThoai;

    @Column(name = "EMAIL", length = 100)
    private String email;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "TRANG_THAI", length = 20)
    private TrangThaiNguoiDung trangThai;

    @Column(name = "NGAY_TAO")
    private LocalDateTime ngayTao;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "NGUOI_DUNG_VAI_TRO",
            joinColumns = @JoinColumn(name = "MA_NGUOI_DUNG"),
            inverseJoinColumns = @JoinColumn(name = "MA_VAI_TRO")
    )
    private Set<VaiTro> vaiTros = new HashSet<>();

    @PrePersist
    public void prePersist() {
        if (trangThai == null) {
            trangThai = TrangThaiNguoiDung.HOAT_DONG;
        }
        if (ngayTao == null) {
            ngayTao = LocalDateTime.now();
        }
    }
}
