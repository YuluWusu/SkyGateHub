package com.example.skygatehub.entity.account;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Entity ánh xạ bảng NGUOI_DUNG_VAI_TRO (join table M:N).
 * PK composite (MA_NGUOI_DUNG, MA_VAI_TRO).
 */
@Entity
@Table(name = "NGUOI_DUNG_VAI_TRO")
@IdClass(NguoiDungVaiTroId.class)
@Getter @Setter @NoArgsConstructor
public class NguoiDungVaiTro {

    @Id
    @Column(name = "MA_NGUOI_DUNG", length = 20)
    private String maNguoiDung;

    @Id
    @Column(name = "MA_VAI_TRO", length = 20)
    private String maVaiTro;

    @Column(name = "NGAY_GAN", nullable = false)
    private LocalDateTime ngayGan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MA_NGUOI_DUNG", referencedColumnName = "MA_NGUOI_DUNG",
                insertable = false, updatable = false)
    private NguoiDung nguoiDung;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MA_VAI_TRO", referencedColumnName = "MA_VAI_TRO",
                insertable = false, updatable = false)
    private VaiTro vaiTro;

    @PrePersist
    public void prePersist() {
        if (ngayGan == null) ngayGan = LocalDateTime.now();
    }
}

