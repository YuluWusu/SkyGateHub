package com.example.skygatehub.common.security;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CurrentUser implements Serializable {

    private Long id;
    private String maNguoiDung;
    private String tenDangNhap;
    private String hoTen;
    private String email;
    private String soDienThoai;
    private Boolean canDoiMatKhau;
    private List<String> roles = new ArrayList<>();

    public boolean hasRole(String role) {
        if (roles == null) {
            return false;
        }
        for (String r : roles) {
            if (r.equalsIgnoreCase(role)) {
                return true;
            }
        }
        return false;
    }

    public boolean isAdmin() {
        return hasRole("QUAN_LY") || hasRole("ROLE_ADMIN") || hasRole("VT001") || hasRole("ADMIN");
    }

    public boolean isStaff() {
        return isAdmin() || hasRole("NHAN_VIEN_VE") || hasRole("NHAN_VIEN_NHA_HANG") || hasRole("NHAN_VIEN_LUU_TRU")
                || hasRole("VT004") || hasRole("VT005") || hasRole("VT006");
    }
}
