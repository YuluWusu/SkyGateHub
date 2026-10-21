package com.example.skygatehub.controller.auth;

import com.example.skygatehub.common.constant.SessionKeys;
import com.example.skygatehub.common.security.CurrentUser;
import com.example.skygatehub.dto.auth.LoginRequest;
import com.example.skygatehub.dto.auth.RegisterRequest;
import com.example.skygatehub.service.auth.AuthService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/login")
    public String hienThiTrangDangNhap(Model moHinh,
                                       @RequestParam(value = "error", required = false) String loi,
                                       @RequestParam(value = "logout", required = false) String dangXuat,
                                       @RequestParam(value = "redirect", required = false) String chuyenHuong,
                                       HttpSession phienLamViec) {
        if (phienLamViec != null && phienLamViec.getAttribute(SessionKeys.CURRENT_USER) != null) {
            return "redirect:/";
        }
        if (loi != null) {
            moHinh.addAttribute(SessionKeys.FLASH_ERROR, "Tên đăng nhập hoặc mật khẩu không chính xác.");
        }
        if (dangXuat != null) {
            moHinh.addAttribute(SessionKeys.FLASH_SUCCESS, "Bạn đã đăng xuất thành công.");
        }
        moHinh.addAttribute("loginRequest", new LoginRequest());
        moHinh.addAttribute("redirect", chuyenHuong);
        return "auth/login";
    }

    @PostMapping("/login")
    public String xuLyDangNhap(@ModelAttribute("loginRequest") LoginRequest yeuCauDangNhap,
                               @RequestParam(value = "redirect", required = false) String chuyenHuong,
                               HttpSession phienLamViec,
                               RedirectAttributes dieuHuong) {
        CurrentUser nguoiDungHienTai = authService.dangNhap(yeuCauDangNhap);
        if (nguoiDungHienTai == null) {
            dieuHuong.addFlashAttribute(SessionKeys.FLASH_ERROR, "Sai thông tin đăng nhập hoặc tài khoản bị khóa.");
            return "redirect:/login";
        }

        phienLamViec.setAttribute(SessionKeys.CURRENT_USER, nguoiDungHienTai);
        phienLamViec.setAttribute(SessionKeys.CURRENT_USER_ROLES, nguoiDungHienTai.getRoles());

        if (Boolean.TRUE.equals(nguoiDungHienTai.getCanDoiMatKhau())) {
            dieuHuong.addFlashAttribute(SessionKeys.FLASH_SUCCESS, "Vui lòng đổi mật khẩu mới trong lần đầu đăng nhập.");
            return "redirect:/profile";
        }

        if (chuyenHuong != null && !chuyenHuong.trim().isEmpty() && !chuyenHuong.contains("login")) {
            return "redirect:" + chuyenHuong;
        }

        if (nguoiDungHienTai.isAdmin()) {
            return "redirect:/admin/users";
        }

        return "redirect:/";
    }

    @GetMapping("/logout")
    public String xuLyDangXuat(HttpSession phienLamViec, RedirectAttributes dieuHuong) {
        if (phienLamViec != null) {
            phienLamViec.invalidate();
        }
        dieuHuong.addFlashAttribute(SessionKeys.FLASH_SUCCESS, "Đăng xuất thành công.");
        return "redirect:/login?logout=true";
    }

    @GetMapping("/register")
    public String hienThiTrangDangKy(Model moHinh, HttpSession phienLamViec) {
        if (phienLamViec != null && phienLamViec.getAttribute(SessionKeys.CURRENT_USER) != null) {
            return "redirect:/";
        }
        moHinh.addAttribute("registerRequest", new RegisterRequest());
        return "auth/register";
    }

    @PostMapping("/register")
    public String xuLyDangKy(@ModelAttribute("registerRequest") RegisterRequest yeuCauDangKy,
                             RedirectAttributes dieuHuong) {
        if (!yeuCauDangKy.getMatKhau().equals(yeuCauDangKy.getXacNhanMatKhau())) {
            dieuHuong.addFlashAttribute(SessionKeys.FLASH_ERROR, "Mật khẩu xác nhận không trùng khớp.");
            return "redirect:/register";
        }

        try {
            authService.dangKy(yeuCauDangKy);
            dieuHuong.addFlashAttribute(SessionKeys.FLASH_SUCCESS, "Đăng ký tài khoản thành công! Vui lòng đăng nhập.");
            return "redirect:/login";
        } catch (Exception ngoaiLe) {
            dieuHuong.addFlashAttribute(SessionKeys.FLASH_ERROR, ngoaiLe.getMessage());
            return "redirect:/register";
        }
    }
}
