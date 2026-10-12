package com.example.skygatehub.dining.entity;

import com.example.skygatehub.dining.enums.TrangThaiBan;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "BAN_AN")
@Getter
@Setter
@NoArgsConstructor
public class BanAn {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MA_BAN")
    private Integer maBan;

    @Column(name = "SO_BAN", length = 20, nullable = false, unique = true)
    private String soBan;

    @Column(name = "SUC_CHUA")
    private Integer sucChua;

    @Column(name = "KHU_VUC", length = 50)
    private String khuVuc;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "TRANG_THAI", length = 30)
    private TrangThaiBan trangThai;

    @PrePersist
    public void prePersist() {
        if (sucChua == null) {
            sucChua = 4;
        }
        if (khuVuc == null) {
            khuVuc = "NHA_HANG_CHINH";
        }
        if (trangThai == null) {
            trangThai = TrangThaiBan.SAN_SANG;
        }
    }
}
