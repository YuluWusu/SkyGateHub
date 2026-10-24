package com.example.skygatehub.dto.dining;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
public class CaAnRequest {

    private Long id;

    @NotBlank(message = "Mã ca ăn không được để trống")
    @Size(max = 20, message = "Mã ca ăn tối đa 20 ký tự")
    private String maCaAn;

    @NotBlank(message = "Tên ca ăn không được để trống")
    @Size(max = 60, message = "Tên ca ăn tối đa 60 ký tự")
    private String tenCaAn;

    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime gioBatDau;

    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime gioKetThuc;

    private Boolean trangThai = true;
}
