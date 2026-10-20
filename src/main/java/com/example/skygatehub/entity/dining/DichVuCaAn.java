package com.example.skygatehub.entity.dining;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entity ánh xạ bảng DICH_VU_CA_AN trong QL_Cong_Troi_Dong_Giang_MySQL.sql.
 */
@Entity
@Table(name = "DICH_VU_CA_AN", uniqueConstraints = {
    @UniqueConstraint(name = "UQ_DICH_VU_CA_AN", columnNames = {"MA_DICH_VU", "MA_CA_AN"})
})
@Getter
@Setter
@NoArgsConstructor
public class DichVuCaAn {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MA_DICH_VU", referencedColumnName = "MA_DICH_VU", nullable = false)
    private DichVuAnUong dichVuAnUong;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MA_CA_AN", referencedColumnName = "MA_CA_AN", nullable = false)
    private CaAn caAn;

    @Column(name = "TRANG_THAI", nullable = false)
    private Boolean trangThai = true;

    @PrePersist
    public void prePersist() {
        if (trangThai == null) trangThai = true;
    }
}

