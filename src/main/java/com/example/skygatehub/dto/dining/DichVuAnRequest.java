package com.example.skygatehub.dto.dining;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class DichVuAnRequest {

    private Long id;

    @NotBlank(message = "Mã dịch vụ không được để trống")
    @Size(max = 20, message = "Mã dịch vụ tối đa 20 ký tự")
    private String maDichVu;

    @NotBlank(message = "Tên dịch vụ không được để trống")
    @Size(max = 150, message = "Tên dịch vụ tối đa 150 ký tự")
    private String tenDichVu;

    @NotBlank(message = "Vui lòng chọn loại dịch vụ")
    private String loaiDichVu;

    @Size(max = 500, message = "Mô tả tối đa 500 ký tự")
    private String moTa;

    @DecimalMin(value = "0", message = "Giá dịch vụ không được âm")
    private BigDecimal giaDichVu;

    private Boolean trangThai = true;

    private List<String> maCaAnList = new ArrayList<>();
}
