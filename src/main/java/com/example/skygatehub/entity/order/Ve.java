package com.example.skygatehub.entity.order;

import com.example.skygatehub.common.enums.TrangThaiVe;
import com.example.skygatehub.entity.ticket.LoaiVe;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entity ánh xạ bảng VE trong QL_Cong_Troi_Dong_Giang_MySQL.sql.
 */
@Entity
@Table(name = "VE", uniqueConstraints = {
    @UniqueConstraint(name = "UQ_VE_MA_DON", columnNames = {"MA_VE", "MA_DON_DAT"})
})
@Getter
@Setter
@NoArgsConstructor
public class Ve {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "MA_VE", length = 20, nullable = false, unique = true)
    private String maVe;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
        @JoinColumn(name = "MA_CHI_TIET", referencedColumnName = "MA_CHI_TIET", nullable = false),
        @JoinColumn(name = "MA_DON_DAT", referencedColumnName = "MA_DON_DAT", nullable = false)
    })
    private ChiTietDon chiTietDon;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MA_LOAI_VE", referencedColumnName = "MA_LOAI_VE", nullable = false)
    private LoaiVe loaiVe;

    @Column(name = "MA_VE_CHUOI", length = 50, nullable = false, unique = true)
    private String maVeChuoi;

    @Column(name = "NGAY_THAM_QUAN", nullable = false)
    private LocalDate ngayThamQuan;

    @Enumerated(EnumType.STRING)
    @Column(name = "TRANG_THAI", length = 20, nullable = false)
    private TrangThaiVe trangThai = TrangThaiVe.CHUA_SU_DUNG;

    @Column(name = "THOI_GIAN_SU_DUNG")
    private LocalDateTime thoiGianSuDung;

    @PrePersist
    public void prePersist() {
        if (trangThai == null) trangThai = TrangThaiVe.CHUA_SU_DUNG;
    }
}

