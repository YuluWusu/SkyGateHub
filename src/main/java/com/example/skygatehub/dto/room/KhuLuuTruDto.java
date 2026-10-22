package com.example.skygatehub.dto.room;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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
public class KhuLuuTruDto {

    private Integer maKhu;

    @NotBlank(message = "Tên khu lưu trú không được để trống")
    @Size(max = 100, message = "Tên khu không được vượt quá 100 ký tự")
    private String tenKhu;

    @Min(value = 1, message = "Hạng sao tối thiểu là 1")
    @Max(value = 5, message = "Hạng sao tối đa là 5")
    private Integer hangSao = 5;

    @Size(max = 255, message = "Địa chỉ không vượt quá 255 ký tự")
    private String diaChi;

    @Size(max = 100, message = "Email không vượt quá 100 ký tự")
    private String email;

    @Size(max = 20, message = "Hotline không vượt quá 20 ký tự")
    private String hotline;

    @Size(max = 255, message = "Đường dẫn hình ảnh không quá 255 ký tự")
    private String hinhAnh;

    private Long soLuongLoaiPhong = 0L;
    private Long soLuongPhong = 0L;
}
