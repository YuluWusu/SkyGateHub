package com.example.skygatehub.ticket.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ComboChiTietId implements Serializable {

    @Column(name = "MA_COMBO")
    private Integer maCombo;

    @Column(name = "MA_SAN_PHAM_CON")
    private Integer maSanPhamCon;
}
