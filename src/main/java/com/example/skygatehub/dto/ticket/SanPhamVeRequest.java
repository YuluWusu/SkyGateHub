package com.example.skygatehub.dto.ticket;

import com.example.skygatehub.common.enums.LoaiVeType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class SanPhamVeRequest {

    private String maLoaiVe;
    private String tenLoaiVe;
    private LoaiVeType loaiVe;
    private String moTa;
    private BigDecimal giaVe;
    private Boolean trangThai = true;
}
