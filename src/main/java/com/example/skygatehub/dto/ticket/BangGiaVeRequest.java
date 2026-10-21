package com.example.skygatehub.dto.ticket;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class BangGiaVeRequest {

    private String maBangGia;
    private String maLoaiVe;
    private String tenBangGia;
    private String dieuKienGia = "MAC_DINH";
    private LocalDate hieuLucTu;
    private LocalDate hieuLucDen;
    private BigDecimal donGia;
    private Integer uuTien = 0;
    private Boolean trangThai = true;
    private String ghiChu;
}
