package com.example.skygatehub.entity.ticket;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Entity ánh xạ bảng KHO_VE trong QL_Cong_Troi_Dong_Giang_MySQL.sql.
 */
@Entity
@Table(name = "KHO_VE", uniqueConstraints = {
    @UniqueConstraint(name = "UQ_KHO_VE_LOAI_NGAY", columnNames = {"MA_LOAI_VE", "NGAY_THAM_QUAN"})
})
@Getter
@Setter
@NoArgsConstructor
public class KhoVe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "MA_KHO_VE", length = 20, nullable = false, unique = true)
    private String maKhoVe;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MA_LOAI_VE", referencedColumnName = "MA_LOAI_VE", nullable = false)
    private LoaiVe loaiVe;

    @Column(name = "NGAY_THAM_QUAN", nullable = false)
    private LocalDate ngayThamQuan;

    @Column(name = "SO_LUONG_TOI_DA")
    private Integer soLuongToiDa;

    @Column(name = "SO_LUONG_DA_BAN", nullable = false)
    private Integer soLuongDaBan = 0;

    @PrePersist
    public void prePersist() {
        if (soLuongDaBan == null) soLuongDaBan = 0;
    }
}

