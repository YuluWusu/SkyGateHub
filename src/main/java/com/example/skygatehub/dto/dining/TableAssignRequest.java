package com.example.skygatehub.dto.dining;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TableAssignRequest {

    @NotBlank(message = "Mã đơn đặt bàn không được để trống")
    private String maDatBan;

    @NotBlank(message = "Vui lòng chọn bàn ăn vật lý để phân")
    private String maBanAn;

    private String ghiChu;
}
