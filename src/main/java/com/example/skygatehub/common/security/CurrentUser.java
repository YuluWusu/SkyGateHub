package com.example.skygatehub.common.security;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Đối tượng lưu thông tin người dùng và danh sách vai trò trong HttpSession.
 * Đồng thời được GlobalModelAdvice bơm vào mọi View với tên "currentUser" để lọc menu Sidebar/Topbar theo quyền.
 */
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

    public boolean canAccessTicketModule() {
        return isAdmin() || hasRole("NHAN_VIEN_VE") || hasRole("VT004");
    }

    public boolean canAccessRoomModule() {
        return isAdmin() || hasRole("NHAN_VIEN_LUU_TRU") || hasRole("VT006");
    }

    public boolean canAccessDiningModule() {
        return isAdmin() || hasRole("NHAN_VIEN_NHA_HANG") || hasRole("VT005");
    }

    public boolean canAccessOrders() {
        return isAdmin() || canAccessTicketModule() || canAccessRoomModule();
    }

    public String getDefaultAdminUrl() {
        if (isAdmin()) {
            return "/admin/dashboard";
        }
        if (canAccessRoomModule()) {
            return "/admin/room-map";
        }
        if (canAccessDiningModule()) {
            return "/admin/dining/table-assignment";
        }
        if (canAccessTicketModule()) {
            return "/admin/tickets";
        }
        return "/admin/dashboard";
    }

    public String getTenVaiTroHienThi() {
        if (isAdmin()) {
            return "Quản Trị Viên";
        }
        if (canAccessRoomModule()) {
            return "Lễ Tân Lưu Trú";
        }
        if (canAccessDiningModule()) {
            return "Nhân Viên Nhà Hàng";
        }
        if (canAccessTicketModule()) {
            return "Nhân Viên Vé";
        }
        return "Khách Hàng";
    }
}
