package com.example.skygatehub.controller.auth;

import com.example.skygatehub.common.constant.SessionKeys;
import com.example.skygatehub.common.security.CurrentUser;
import com.example.skygatehub.dto.auth.LoginRequest;
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
            dieuHuong.addFlashAttribute(SessionKeys.FLASH_SUCCESS, "Chào mừng bạn! Vui lòng cập nhật thông tin cá nhân và đặt mật khẩu mới cho lần đầu đăng nhập.");
            return "redirect:/doi-mat-khau-lan-dau";
        }

        if (chuyenHuong != null && !chuyenHuong.trim().isEmpty() && !chuyenHuong.contains("login")) {
            return "redirect:" + chuyenHuong;
        }

        if (nguoiDungHienTai.isAdmin()) {
            return "redirect:/admin/users";
        }
        if (nguoiDungHienTai.hasRole("NHAN_VIEN_LUU_TRU") || nguoiDungHienTai.hasRole("VT006")) {
            return "redirect:/admin/room-map";
        }
        if (nguoiDungHienTai.hasRole("NHAN_VIEN_VE") || nguoiDungHienTai.hasRole("VT004")) {
            return "redirect:/admin/tickets";
        }

        return "redirect:/admin/rooms";
    }

    @GetMapping("/doi-mat-khau-lan-dau")
    public String hienThiTrangDoiMatKhauLanDau(HttpSession phienLamViec, Model moHinh, RedirectAttributes dieuHuong) {
        CurrentUser nguoiDungHienTai = (CurrentUser) phienLamViec.getAttribute(SessionKeys.CURRENT_USER);
        if (nguoiDungHienTai == null) {
            dieuHuong.addFlashAttribute(SessionKeys.FLASH_ERROR, "Vui lòng đăng nhập trước.");
            return "redirect:/login";
        }

        moHinh.addAttribute("currentUser", nguoiDungHienTai);
        return "auth/first-login";
    }

    @PostMapping("/doi-mat-khau-lan-dau")
    public String xuLyDoiMatKhauLanDau(@RequestParam("hoTen") String hoTen,
                                       @RequestParam("email") String email,
                                       @RequestParam("soDienThoai") String soDienThoai,
                                       @RequestParam("matKhauHienTai") String matKhauHienTai,
                                       @RequestParam("matKhauMoi") String matKhauMoi,
                                       @RequestParam("xacNhanMatKhauMoi") String xacNhanMatKhauMoi,
                                       HttpSession phienLamViec,
                                       RedirectAttributes dieuHuong) {
        CurrentUser nguoiDungHienTai = (CurrentUser) phienLamViec.getAttribute(SessionKeys.CURRENT_USER);
        if (nguoiDungHienTai == null) {
            return "redirect:/login";
        }

        if (matKhauMoi == null || matKhauMoi.trim().length() < 6) {
            dieuHuong.addFlashAttribute(SessionKeys.FLASH_ERROR, "Mật khẩu mới phải có tối thiểu 6 ký tự.");
            return "redirect:/doi-mat-khau-lan-dau";
        }

        if (!matKhauMoi.equals(xacNhanMatKhauMoi)) {
            dieuHuong.addFlashAttribute(SessionKeys.FLASH_ERROR, "Mật khẩu mới xác nhận không trùng khớp.");
            return "redirect:/doi-mat-khau-lan-dau";
        }

        boolean thanhCong = authService.capNhatThongTinVaDoiMatKhau(
                nguoiDungHienTai.getMaNguoiDung(),
                hoTen, email, soDienThoai,
                matKhauHienTai, matKhauMoi
        );

        if (!thanhCong) {
            dieuHuong.addFlashAttribute(SessionKeys.FLASH_ERROR, "Mật khẩu khởi tạo hiện tại không chính xác.");
            return "redirect:/doi-mat-khau-lan-dau";
        }

        nguoiDungHienTai.setHoTen(hoTen.trim());
        nguoiDungHienTai.setEmail(email.trim());
        nguoiDungHienTai.setSoDienThoai(soDienThoai.trim());
        nguoiDungHienTai.setCanDoiMatKhau(false);
        phienLamViec.setAttribute(SessionKeys.CURRENT_USER, nguoiDungHienTai);

        dieuHuong.addFlashAttribute(SessionKeys.FLASH_SUCCESS,
                "Cập nhật hồ sơ và đổi mật khẩu lần đầu thành công! Chào mừng bạn đến với hệ thống SkyGateHub.");

        if (nguoiDungHienTai.isAdmin()) {
            return "redirect:/admin/users";
        }
        if (nguoiDungHienTai.hasRole("NHAN_VIEN_LUU_TRU") || nguoiDungHienTai.hasRole("VT006")) {
            return "redirect:/admin/room-map";
        }
        if (nguoiDungHienTai.hasRole("NHAN_VIEN_VE") || nguoiDungHienTai.hasRole("VT004")) {
            return "redirect:/admin/tickets";
        }

        return "redirect:/admin/rooms";
    }

    @GetMapping("/logout")
    public String xuLyDangXuat(HttpSession phienLamViec, RedirectAttributes dieuHuong) {
        if (phienLamViec != null) {
            phienLamViec.invalidate();
        }
        dieuHuong.addFlashAttribute(SessionKeys.FLASH_SUCCESS, "Bạn đã đăng xuất khỏi hệ thống.");
        return "redirect:/login?logout=true";
    }

    @GetMapping("/admin/switch-to-customer")
    public String chuyenVeWebsiteKhachHang(HttpSession phienLamViec, RedirectAttributes dieuHuong) {
        if (phienLamViec != null) {
            phienLamViec.invalidate();
        }
        dieuHuong.addFlashAttribute(SessionKeys.FLASH_SUCCESS, "Đã đăng xuất và chuyển về giao diện Khách hàng.");
        return "redirect:/";
    }
}
