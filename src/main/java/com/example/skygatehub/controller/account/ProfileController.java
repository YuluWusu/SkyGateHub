package com.example.skygatehub.controller.account;

import com.example.skygatehub.common.constant.SessionKeys;
import com.example.skygatehub.common.security.CurrentUser;
import com.example.skygatehub.dto.account.ProfileUpdateRequest;
import com.example.skygatehub.dto.account.UserResponse;
import com.example.skygatehub.service.account.AccountService;
import com.example.skygatehub.service.auth.AuthService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    private final AccountService accountService;
    private final AuthService authService;

    public ProfileController(AccountService accountService, AuthService authService) {
        this.accountService = accountService;
        this.authService = authService;
    }

    @GetMapping("")
    public String xemHoSo(HttpSession phienLamViec, Model moHinh) {
        CurrentUser nguoiDungHienTai = (CurrentUser) phienLamViec.getAttribute(SessionKeys.CURRENT_USER);
        if (nguoiDungHienTai == null) {
            return "redirect:/login";
        }

        UserResponse nguoiDung = accountService.layNguoiDungTheoMa(nguoiDungHienTai.getMaNguoiDung());
        moHinh.addAttribute("user", nguoiDung);
        moHinh.addAttribute("profileRequest", new ProfileUpdateRequest());
        return "account/customer/profile";
    }

    @PostMapping("/update")
    public String capNhatHoSo(@ModelAttribute("profileRequest") ProfileUpdateRequest yeuCauCapNhat,
                              HttpSession phienLamViec,
                              RedirectAttributes dieuHuong) {
        CurrentUser nguoiDungHienTai = (CurrentUser) phienLamViec.getAttribute(SessionKeys.CURRENT_USER);
        if (nguoiDungHienTai == null) {
            return "redirect:/login";
        }

        if (yeuCauCapNhat.getMatKhauMoi() != null && !yeuCauCapNhat.getMatKhauMoi().trim().isEmpty()) {
            if (!yeuCauCapNhat.getMatKhauMoi().equals(yeuCauCapNhat.getXacNhanMatKhauMoi())) {
                dieuHuong.addFlashAttribute(SessionKeys.FLASH_ERROR, "Mật khẩu mới xác nhận không khớp.");
                return "redirect:/profile";
            }
            boolean doiThanhCong = authService.doiMatKhau(
                    nguoiDungHienTai.getMaNguoiDung(),
                    yeuCauCapNhat.getMatKhauHienTai(),
                    yeuCauCapNhat.getMatKhauMoi()
            );
            if (!doiThanhCong) {
                dieuHuong.addFlashAttribute(SessionKeys.FLASH_ERROR, "Mật khẩu hiện tại không chính xác.");
                return "redirect:/profile";
            }
            nguoiDungHienTai.setCanDoiMatKhau(false);
            phienLamViec.setAttribute(SessionKeys.CURRENT_USER, nguoiDungHienTai);
        }

        dieuHuong.addFlashAttribute(SessionKeys.FLASH_SUCCESS, "Cập nhật hồ sơ tài khoản thành công.");
        return "redirect:/profile";
    }
}
