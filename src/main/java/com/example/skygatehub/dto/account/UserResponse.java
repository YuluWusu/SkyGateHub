package com.example.skygatehub.dto.account;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private Long id;
    private String maNguoiDung;
    private String tenDangNhap;
    private String hoTen;
    private String email;
    private String soDienThoai;
    private Boolean trangThai;
    private Boolean canDoiMatKhau;
    private List<String> tenVaiTroList;
    private LocalDateTime ngayTao;
    private LocalDateTime ngayCapNhat;

    public boolean isAdmin() {
        if (tenVaiTroList == null) {
            return false;
        }
        for (String role : tenVaiTroList) {
            if ("QUAN_LY".equalsIgnoreCase(role) || "ROLE_ADMIN".equalsIgnoreCase(role) || "ADMIN".equalsIgnoreCase(role) || "VT001".equalsIgnoreCase(role)) {
                return true;
            }
        }
        return false;
    }

    public boolean isStaff() {
        if (isAdmin()) {
            return true;
        }
        if (tenVaiTroList == null) {
            return false;
        }
        for (String role : tenVaiTroList) {
            if (role != null && (role.toUpperCase().startsWith("NHAN_VIEN") || role.toUpperCase().startsWith("ROLE_STAFF") || role.toUpperCase().startsWith("VT"))) {
                return true;
            }
        }
        return false;
    }

    public String getPrimaryRoleName() {
        if (tenVaiTroList != null && !tenVaiTroList.isEmpty()) {
            return tenVaiTroList.get(0);
        }
        return "Khách Hàng";
    }

    public String getTenVaiTroHienThi() {
        if (isAdmin()) {
            return "Quản trị viên";
        }
        if (tenVaiTroList == null || tenVaiTroList.isEmpty()) {
            return "Khách hàng";
        }
        String vt = tenVaiTroList.get(0);
        if ("NHAN_VIEN_VE".equalsIgnoreCase(vt) || "VT004".equalsIgnoreCase(vt)) {
            return "Nhân viên bán vé";
        }
        if ("NHAN_VIEN_NHA_HANG".equalsIgnoreCase(vt) || "VT005".equalsIgnoreCase(vt)) {
            return "Nhân viên nhà hàng";
        }
        if ("NHAN_VIEN_LUU_TRU".equalsIgnoreCase(vt) || "VT006".equalsIgnoreCase(vt)) {
            return "Nhân viên lưu trú";
        }
        if (isStaff()) {
            return "Nhân viên";
        }
        return "Khách hàng";
    }
}
