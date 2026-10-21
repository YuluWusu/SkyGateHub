package com.example.skygatehub.controller.account;

import com.example.skygatehub.common.constant.SessionKeys;
import com.example.skygatehub.common.security.RequireRole;
import com.example.skygatehub.dto.account.UserCreateRequest;
import com.example.skygatehub.dto.account.UserResponse;
import com.example.skygatehub.entity.account.VaiTro;
import com.example.skygatehub.service.account.AccountService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/users")
@RequireRole({"QUAN_LY", "ROLE_ADMIN", "VT001", "ADMIN"})
public class AdminAccountController {

    private final AccountService accountService;

    public AdminAccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("")
    public String danhSachNguoiDung(Model moHinh,
                                    @RequestParam(value = "keyword", required = false) String tuKhoa,
                                    @RequestParam(value = "page", defaultValue = "0") int trang,
                                    @RequestParam(value = "size", defaultValue = "10") int kichThuoc) {
        Pageable phanTrang = PageRequest.of(trang, kichThuoc, Sort.by(Sort.Direction.DESC, "id"));
        Page<UserResponse> trangNguoiDung;

        if (tuKhoa != null && !tuKhoa.trim().isEmpty()) {
            trangNguoiDung = accountService.timKiemNguoiDung(tuKhoa.trim(), phanTrang);
            moHinh.addAttribute("keyword", tuKhoa.trim());
        } else {
            trangNguoiDung = accountService.layTatCaNguoiDung(phanTrang);
        }

        moHinh.addAttribute("usersPage", trangNguoiDung);
        moHinh.addAttribute("currentPage", trang);
        moHinh.addAttribute("totalPages", trangNguoiDung.getTotalPages());
        return "account/admin/list";
    }

    @GetMapping("/create")
    public String hienThiFormTao(Model moHinh) {
        List<VaiTro> danhSachVaiTro = accountService.layTatCaVaiTro();
        moHinh.addAttribute("userRequest", new UserCreateRequest());
        moHinh.addAttribute("roles", danhSachVaiTro);
        moHinh.addAttribute("isEdit", false);
        return "account/admin/form";
    }

    @PostMapping("/create")
    public String taoNguoiDung(@ModelAttribute("userRequest") UserCreateRequest yeuCau,
                               RedirectAttributes dieuHuong) {
        try {
            accountService.taoNguoiDung(yeuCau);
            dieuHuong.addFlashAttribute(SessionKeys.FLASH_SUCCESS, "Tạo tài khoản người dùng thành công.");
            return "redirect:/admin/users";
        } catch (Exception ngoaiLe) {
            dieuHuong.addFlashAttribute(SessionKeys.FLASH_ERROR, ngoaiLe.getMessage());
            return "redirect:/admin/users/create";
        }
    }

    @GetMapping("/edit/{id}")
    public String hienThiFormSua(@PathVariable("id") Long id, Model moHinh, RedirectAttributes dieuHuong) {
        UserResponse nguoiDung = accountService.layNguoiDungTheoId(id);
        if (nguoiDung == null) {
            dieuHuong.addFlashAttribute(SessionKeys.FLASH_ERROR, "Không tìm thấy người dùng.");
            return "redirect:/admin/users";
        }

        UserCreateRequest yeuCau = new UserCreateRequest();
        yeuCau.setMaNguoiDung(nguoiDung.getMaNguoiDung());
        yeuCau.setTenDangNhap(nguoiDung.getTenDangNhap());
        yeuCau.setHoTen(nguoiDung.getHoTen());
        yeuCau.setEmail(nguoiDung.getEmail());
        yeuCau.setSoDienThoai(nguoiDung.getSoDienThoai());
        yeuCau.setTrangThai(nguoiDung.getTrangThai());
        yeuCau.setCanDoiMatKhau(nguoiDung.getCanDoiMatKhau());

        List<VaiTro> danhSachVaiTro = accountService.layTatCaVaiTro();
        moHinh.addAttribute("userRequest", yeuCau);
        moHinh.addAttribute("roles", danhSachVaiTro);
        moHinh.addAttribute("userId", id);
        moHinh.addAttribute("isEdit", true);
        return "account/admin/form";
    }

    @PostMapping("/edit/{id}")
    public String capNhatNguoiDung(@PathVariable("id") Long id,
                                   @ModelAttribute("userRequest") UserCreateRequest yeuCau,
                                   RedirectAttributes dieuHuong) {
        try {
            accountService.capNhatNguoiDung(id, yeuCau);
            dieuHuong.addFlashAttribute(SessionKeys.FLASH_SUCCESS, "Cập nhật tài khoản thành công.");
            return "redirect:/admin/users";
        } catch (Exception ngoaiLe) {
            dieuHuong.addFlashAttribute(SessionKeys.FLASH_ERROR, ngoaiLe.getMessage());
            return "redirect:/admin/users/edit/" + id;
        }
    }

    @GetMapping("/detail/{id}")
    public String chiTietNguoiDung(@PathVariable("id") Long id, Model moHinh, RedirectAttributes dieuHuong) {
        UserResponse nguoiDung = accountService.layNguoiDungTheoId(id);
        if (nguoiDung == null) {
            dieuHuong.addFlashAttribute(SessionKeys.FLASH_ERROR, "Không tìm thấy người dùng.");
            return "redirect:/admin/users";
        }
        moHinh.addAttribute("user", nguoiDung);
        return "account/admin/detail";
    }

    @GetMapping("/toggle/{id}")
    public String chuyenTrangThaiNguoiDung(@PathVariable("id") Long id, RedirectAttributes dieuHuong) {
        accountService.chuyenTrangThaiNguoiDung(id);
        dieuHuong.addFlashAttribute(SessionKeys.FLASH_SUCCESS, "Thay đổi trạng thái tài khoản thành công.");
        return "redirect:/admin/users";
    }

    @GetMapping("/delete/{id}")
    public String xoaNguoiDung(@PathVariable("id") Long id, RedirectAttributes dieuHuong) {
        try {
            accountService.xoaNguoiDung(id);
            dieuHuong.addFlashAttribute(SessionKeys.FLASH_SUCCESS, "Xóa tài khoản người dùng thành công.");
        } catch (Exception ngoaiLe) {
            dieuHuong.addFlashAttribute(SessionKeys.FLASH_ERROR, "Không thể xóa người dùng do có liên kết dữ liệu.");
        }
        return "redirect:/admin/users";
    }
}
