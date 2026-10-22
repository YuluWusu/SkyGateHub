package com.example.skygatehub.dto.room;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoaiPhongRequest {

    private Long id;

    @NotBlank(message = "Mã loại phòng không được để trống")
    @Size(max = 20, message = "Mã loại phòng tối đa 20 ký tự")
    private String maLoaiPhong;

    @NotNull(message = "Vui lòng chọn khu lưu trú")
    private Integer maKhu;

    @NotBlank(message = "Tên loại phòng không được để trống")
    @Size(max = 120, message = "Tên loại phòng tối đa 120 ký tự")
    private String tenLoaiPhong;

    @Min(value = 1, message = "Diện tích phải lớn hơn 0 m²")
    private Integer dienTich;

    @NotBlank(message = "Vui lòng chọn hoặc nhập loại giường")
    @Size(max = 50, message = "Loại giường tối đa 50 ký tự")
    private String loaiGiuong = "DBL";

    @NotNull(message = "Vui lòng nhập sức chứa người lớn")
    @Min(value = 1, message = "Sức chứa người lớn tối thiểu là 1 người")
    private Integer sucChuaNguoiLon = 2;

    @NotNull(message = "Vui lòng nhập sức chứa trẻ em")
    @Min(value = 0, message = "Sức chứa trẻ em tối thiểu là 0 người")
    private Integer sucChuaTreEm = 1;

    @NotNull(message = "Vui lòng nhập giá cơ bản")
    @DecimalMin(value = "0.0", message = "Giá cơ bản không được âm")
    private BigDecimal giaCoBan;

    @Size(max = 255, message = "Đường dẫn hình ảnh tối đa 255 ký tự")
    private String hinhAnh;

    @Size(max = 500, message = "Mô tả không quá 500 ký tự")
    private String moTa;

    private Boolean trangThai = true;

    // Helper display fields
    private String tenKhu;
    private Long soPhong = 0L;
}
