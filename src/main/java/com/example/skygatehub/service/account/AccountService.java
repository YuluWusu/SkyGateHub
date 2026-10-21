package com.example.skygatehub.service.account;

import com.example.skygatehub.dto.account.UserCreateRequest;
import com.example.skygatehub.dto.account.UserResponse;
import com.example.skygatehub.entity.account.VaiTro;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AccountService {

    Page<UserResponse> layTatCaNguoiDung(Pageable phanTrang);

    Page<UserResponse> timKiemNguoiDung(String tuKhoa, Pageable phanTrang);

    UserResponse layNguoiDungTheoId(Long id);

    UserResponse layNguoiDungTheoMa(String maNguoiDung);

    UserResponse taoNguoiDung(UserCreateRequest yeuCau);

    UserResponse capNhatNguoiDung(Long id, UserCreateRequest yeuCau);

    void chuyenTrangThaiNguoiDung(Long id);

    void xoaNguoiDung(Long id);

    List<VaiTro> layTatCaVaiTro();

    void capNhatVaiTroNguoiDung(String maNguoiDung, List<String> danhSachMaVaiTro);

    default Page<UserResponse> getAllUsers(Pageable pageable) {
        return layTatCaNguoiDung(pageable);
    }

    default Page<UserResponse> searchUsers(String keyword, Pageable pageable) {
        return timKiemNguoiDung(keyword, pageable);
    }

    default UserResponse getUserById(Long id) {
        return layNguoiDungTheoId(id);
    }

    default UserResponse getUserByMa(String maNguoiDung) {
        return layNguoiDungTheoMa(maNguoiDung);
    }

    default UserResponse createUser(UserCreateRequest request) {
        return taoNguoiDung(request);
    }

    default UserResponse updateUser(Long id, UserCreateRequest request) {
        return capNhatNguoiDung(id, request);
    }

    default void toggleUserStatus(Long id) {
        chuyenTrangThaiNguoiDung(id);
    }

    default void deleteUser(Long id) {
        xoaNguoiDung(id);
    }

    default List<VaiTro> getAllRoles() {
        return layTatCaVaiTro();
    }

    default void updateUserRoles(String maNguoiDung, List<String> roleCodes) {
        capNhatVaiTroNguoiDung(maNguoiDung, roleCodes);
    }
}
