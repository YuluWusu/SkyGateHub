package com.example.skygatehub.dto.room;

import com.example.skygatehub.common.enums.TrangThaiPhong;
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
public class PhongRequest {

    private Long id;

    @NotBlank(message = "Mã phòng không được để trống")
    @Size(max = 20, message = "Mã phòng tối đa 20 ký tự")
    private String maPhong;

    @NotBlank(message = "Vui lòng chọn loại phòng")
    private String maLoaiPhong;

    @NotBlank(message = "Số phòng không được để trống")
    @Size(max = 30, message = "Số phòng tối đa 30 ký tự")
    private String soPhong;

    @NotNull(message = "Vui lòng chọn trạng thái phòng")
    private TrangThaiPhong trangThai = TrangThaiPhong.SAN_SANG;

    // Helper display fields
    private String tenLoaiPhong;
    private String tenKhu;
    private Integer maKhu;
    private BigDecimal giaCoBan;
    private String loaiGiuong;
    private Integer sucChuaNguoiLon;
    private Integer sucChuaTreEm;
}
