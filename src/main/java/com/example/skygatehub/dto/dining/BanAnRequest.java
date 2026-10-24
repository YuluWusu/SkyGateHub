package com.example.skygatehub.dto.dining;

import com.example.skygatehub.common.enums.TrangThaiBanAn;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BanAnRequest {

    private Long id;

    @NotBlank(message = "Mã bàn ăn không được để trống")
    @Size(max = 20, message = "Mã bàn ăn tối đa 20 ký tự")
    private String maBanAn;

    @NotBlank(message = "Số bàn không được để trống")
    @Size(max = 30, message = "Số bàn tối đa 30 ký tự")
    private String soBan;

    @NotBlank(message = "Khu vực không được để trống")
    @Size(max = 120, message = "Khu vực tối đa 120 ký tự")
    private String khuVuc;

    @NotNull(message = "Sức chứa không được để trống")
    @Min(value = 1, message = "Sức chứa tối thiểu là 1")
    private Integer sucChua;

    @NotNull(message = "Vui lòng chọn trạng thái bàn")
    private TrangThaiBanAn trangThai = TrangThaiBanAn.SAN_SANG;
}
