package com.example.skygatehub.dto.room;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoomAssignRequest {

    @NotBlank(message = "Mã đặt phòng không được để trống")
    private String maDatPhong;

    @NotBlank(message = "Vui lòng chọn phòng cần phân")
    private String maPhong;

    private String maNhanVien;

    // Cho trường hợp gán nhiều phòng cùng lúc
    private List<String> danhSachMaPhong = new ArrayList<>();
}
