package com.example.skygatehub.service.auth;

import com.example.skygatehub.common.security.CurrentUser;
import com.example.skygatehub.dto.auth.LoginRequest;
import com.example.skygatehub.entity.account.NguoiDung;

public interface AuthService {

    CurrentUser dangNhap(LoginRequest yeuCau);


    boolean doiMatKhau(String maNguoiDung, String matKhauCu, String matKhauMoi);

    boolean capNhatThongTinVaDoiMatKhau(String maNguoiDung, String hoTen, String email, String soDienThoai, String matKhauCu, String matKhauMoi);

    default CurrentUser login(LoginRequest request) {
        return dangNhap(request);
    }


    default boolean changePassword(String maNguoiDung, String oldPassword, String newPassword) {
        return doiMatKhau(maNguoiDung, oldPassword, newPassword);
    }
}
