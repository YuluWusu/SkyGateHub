package com.example.skygatehub.checkin.entity;

import com.example.skygatehub.account.entity.NguoiDung;
import com.example.skygatehub.checkin.enums.LoaiDichVuCheckIn;
import com.example.skygatehub.order.entity.DonDat;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

@Entity
@Table(name = "LICHSU_CHECK_IN")
@Getter
@Setter
@NoArgsConstructor
public class LichSuCheckIn {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MA_CHECK_IN")
    private Integer maCheckIn;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "MA_DON_DAT", nullable = false)
    private DonDat donDat;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "LOAI_DICH_VU_CHECK_IN", length = 30, nullable = false)
    private LoaiDichVuCheckIn loaiDichVuCheckIn;

    @Column(name = "THOI_GIAN_CHECK_IN")
    private LocalDateTime thoiGianCheckIn;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "NGUOI_XAC_NHAN", nullable = false)
    private NguoiDung nguoiXacNhan;

    @Column(name = "GHI_CHU", length = 200)
    private String ghiChu;

    @PrePersist
    public void prePersist() {
        if (thoiGianCheckIn == null) {
            thoiGianCheckIn = LocalDateTime.now();
        }
    }
}
