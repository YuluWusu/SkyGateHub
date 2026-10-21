package com.example.skygatehub.dto.ticket;

import com.example.skygatehub.common.enums.LoaiVeType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SanPhamVeResponse {

    private Long id;
    private String maLoaiVe;
    private String tenLoaiVe;
    private LoaiVeType loaiVe;
    private String moTa;
    private BigDecimal giaVe;
    private Boolean trangThai;
    private LocalDateTime ngayTao;
}
