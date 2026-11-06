package com.example.skygatehub.dto.dining;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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

    @Size(max = 500, message = "Ghi chú tối đa 500 ký tự")
    private String ghiChu;
}
