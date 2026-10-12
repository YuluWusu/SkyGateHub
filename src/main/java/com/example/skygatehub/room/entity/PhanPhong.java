package com.example.skygatehub.room.entity;

import com.example.skygatehub.account.entity.NguoiDung;
import com.example.skygatehub.roombooking.entity.DatPhong;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "PHAN_PHONG", uniqueConstraints = {
        @UniqueConstraint(name = "UQ_PHANPHONG", columnNames = {"MA_DAT_PHONG", "MA_PHONG"})
})
@Getter
@Setter
@NoArgsConstructor
public class PhanPhong {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MA_PHAN_PHONG")
    private Integer maPhanPhong;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "MA_DAT_PHONG", nullable = false)
    private DatPhong datPhong;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "MA_PHONG", nullable = false)
    private Phong phong;

    @Column(name = "NGAY_PHAN")
    private LocalDateTime ngayPhan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "NGUOI_PHAN")
    private NguoiDung nguoiPhan;

    @PrePersist
    public void prePersist() {
        if (ngayPhan == null) {
            ngayPhan = LocalDateTime.now();
        }
    }
}
