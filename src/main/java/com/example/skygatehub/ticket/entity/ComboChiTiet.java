package com.example.skygatehub.ticket.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "COMBO_CHI_TIET")
@Getter
@Setter
@NoArgsConstructor
public class ComboChiTiet {

    @EmbeddedId
    private ComboChiTietId id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("maCombo")
    @JoinColumn(name = "MA_COMBO", nullable = false)
    private SanPhamVe combo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("maSanPhamCon")
    @JoinColumn(name = "MA_SAN_PHAM_CON", nullable = false)
    private SanPhamVe sanPhamCon;

    @Column(name = "SO_LUONG")
    private Integer soLuong;

    @PrePersist
    public void prePersist() {
        if (soLuong == null) {
            soLuong = 1;
        }
    }
}
