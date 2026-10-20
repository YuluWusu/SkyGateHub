package com.example.skygatehub.entity.room;

import com.example.skygatehub.entity.account.NguoiDung;
import com.example.skygatehub.common.enums.TrangThaiPhanPhong;
import com.example.skygatehub.entity.roombooking.DatPhong;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Entity ánh xạ bảng PHAN_PHONG trong QL_Cong_Troi_Dong_Giang_MySQL.sql.
 */
@Entity
@Table(name = "PHAN_PHONG", uniqueConstraints = {
    @UniqueConstraint(name = "UQ_PHAN_PHONG_DAT_PHONG", columnNames = {"MA_DAT_PHONG", "MA_PHONG"})
})
@Getter
@Setter
@NoArgsConstructor
public class PhanPhong {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "MA_PHAN_PHONG", length = 20, nullable = false, unique = true)
    private String maPhanPhong;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
        @JoinColumn(name = "MA_DAT_PHONG", referencedColumnName = "MA_DAT_PHONG", nullable = false),
        @JoinColumn(name = "MA_LOAI_PHONG", referencedColumnName = "MA_LOAI_PHONG", nullable = false)
    })
    private DatPhong datPhong;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MA_PHONG", referencedColumnName = "MA_PHONG", nullable = false)
    private Phong phong;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MA_NHAN_VIEN", referencedColumnName = "MA_NGUOI_DUNG", nullable = false)
    private NguoiDung nhanVien;

    @Column(name = "THOI_GIAN_PHAN", nullable = false, updatable = false)
    private LocalDateTime thoiGianPhan;

    @Enumerated(EnumType.STRING)
    @Column(name = "TRANG_THAI", length = 20, nullable = false)
    private TrangThaiPhanPhong trangThai = TrangThaiPhanPhong.DANG_PHAN;

    @PrePersist
    public void prePersist() {
        if (thoiGianPhan == null) thoiGianPhan = LocalDateTime.now();
        if (trangThai == null) trangThai = TrangThaiPhanPhong.DANG_PHAN;
    }
}

