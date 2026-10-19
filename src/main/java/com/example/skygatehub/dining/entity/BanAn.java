package com.example.skygatehub.dining.entity;

import com.example.skygatehub.common.enums.TrangThaiBanAn;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entity ánh xạ bảng BAN_AN trong QL_Cong_Troi_Dong_Giang_MySQL.sql.
 */
@Entity
@Table(name = "BAN_AN")
@Getter
@Setter
@NoArgsConstructor
public class BanAn {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "MA_BAN_AN", length = 20, nullable = false, unique = true)
    private String maBanAn;

    @Column(name = "SO_BAN", length = 30, nullable = false, unique = true)
    private String soBan;

    @Column(name = "KHU_VUC", length = 120, nullable = false)
    private String khuVuc;

    @Column(name = "SUC_CHUA", nullable = false)
    private Integer sucChua;

    @Enumerated(EnumType.STRING)
    @Column(name = "TRANG_THAI", length = 20, nullable = false)
    private TrangThaiBanAn trangThai = TrangThaiBanAn.SAN_SANG;

    @PrePersist
    public void prePersist() {
        if (trangThai == null) trangThai = TrangThaiBanAn.SAN_SANG;
    }
}
