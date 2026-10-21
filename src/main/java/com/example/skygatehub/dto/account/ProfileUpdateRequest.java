package com.example.skygatehub.dto.account;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProfileUpdateRequest {

    private String hoTen;
    private String email;
    private String soDienThoai;
    private String matKhauHienTai;
    private String matKhauMoi;
    private String xacNhanMatKhauMoi;
}
