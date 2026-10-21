package com.example.skygatehub.dto.auth;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RegisterRequest {

    private String tenDangNhap;
    private String matKhau;
    private String xacNhanMatKhau;
    private String hoTen;
    private String email;
    private String soDienThoai;
    private String maVaiTro;
}
