package com.example.skygatehub.entity.room;

import com.example.skygatehub.common.enums.TrangThaiPhong;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entity ánh xạ bảng PHONG trong QL_Cong_Troi_Dong_Giang_MySQL.sql.
 */
@Entity
@Table(name = "PHONG", uniqueConstraints = {
    @UniqueConstraint(name = "UQ_PHONG_MA_LOAI", columnNames = {"MA_PHONG", "MA_LOAI_PHONG"})
})
@Getter
@Setter
@NoArgsConstructor
public class Phong {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "MA_PHONG", length = 20, nullable = false, unique = true)
    private String maPhong;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MA_LOAI_PHONG", referencedColumnName = "MA_LOAI_PHONG", nullable = false)
    private LoaiPhong loaiPhong;

    @Column(name = "SO_PHONG", length = 30, nullable = false, unique = true)
    private String soPhong;

    @Enumerated(EnumType.STRING)
    @Column(name = "TRANG_THAI", length = 20, nullable = false)
    private TrangThaiPhong trangThai = TrangThaiPhong.SAN_SANG;

    @PrePersist
    public void prePersist() {
        if (trangThai == null) trangThai = TrangThaiPhong.SAN_SANG;
    }
}

