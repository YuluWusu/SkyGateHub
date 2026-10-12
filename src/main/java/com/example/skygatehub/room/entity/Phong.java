package com.example.skygatehub.room.entity;

import com.example.skygatehub.room.enums.TrangThaiPhong;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "PHONG")
@Getter
@Setter
@NoArgsConstructor
public class Phong {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MA_PHONG")
    private Integer maPhong;

    @Column(name = "SO_PHONG", length = 20, nullable = false, unique = true)
    private String soPhong;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "MA_LOAI_PHONG", nullable = false)
    private LoaiPhong loaiPhong;

    @Column(name = "TANG")
    private Integer tang;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "TRANG_THAI", length = 30)
    private TrangThaiPhong trangThai;

    @PrePersist
    public void prePersist() {
        if (tang == null) {
            tang = 1;
        }
        if (trangThai == null) {
            trangThai = TrangThaiPhong.SAN_SANG;
        }
    }
}
