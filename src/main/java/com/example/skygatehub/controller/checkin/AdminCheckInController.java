package com.example.skygatehub.controller.checkin;

import com.example.skygatehub.common.constant.SessionKeys;
import com.example.skygatehub.common.security.CurrentUser;
import com.example.skygatehub.common.security.RequireRole;
import com.example.skygatehub.dto.checkin.CheckInLookupResponse;
import com.example.skygatehub.entity.checkin.CheckIn;
import com.example.skygatehub.service.checkin.CheckInService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/checkin")
@RequireRole({"QUAN_LY", "ROLE_ADMIN", "VT001", "ADMIN", "NHAN_VIEN_VE", "VT004"})
@RequiredArgsConstructor
@Slf4j
public class AdminCheckInController {

    private final CheckInService checkInService;

    /**
     * Màn hình tra cứu vé vào cổng (lookup.html)
     */
    @GetMapping({"", "/", "/lookup"})
    public String showLookupPage(
            @RequestParam(value = "keyword", required = false) String keyword,
            Model model) {

        // Lấy danh sách 10 lượt soát vé gần nhất để hiển thị bảng lịch sử
        List<CheckIn> recentCheckIns = checkInService.getRecentTicketCheckIns(10);
        model.addAttribute("recentCheckIns", recentCheckIns);
        model.addAttribute("keyword", keyword);

        // Nếu có keyword truyền vào (ví dụ sau khi check-in chuyển về hoặc gõ link trực tiếp)
        if (keyword != null && !keyword.trim().isEmpty()) {
            try {
                CheckInLookupResponse lookupResult = checkInService.lookupTicket(keyword.trim());
                model.addAttribute("result", lookupResult);
                return "checkin/admin/detail";
            } catch (Exception ex) {
                model.addAttribute(SessionKeys.FLASH_ERROR, ex.getMessage());
            }
        }

        return "checkin/admin/lookup";
    }

    /**
     * Xử lý gửi form tra cứu (chuyển hướng sang detail.html)
     */
    @PostMapping("/lookup")
    public String processLookup(
            @RequestParam("keyword") String keyword,
            RedirectAttributes redirectAttributes) {

        if (keyword == null || keyword.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute(SessionKeys.FLASH_ERROR,
                    "Vui lòng nhập mã vé, mã đơn hàng, mã QR hoặc số điện thoại!");
            return "redirect:/admin/checkin";
        }

        try {
            // Kiểm tra trước xem có tìm thấy dữ liệu không
            checkInService.lookupTicket(keyword.trim());
            return "redirect:/admin/checkin/lookup?keyword=" + keyword.trim();
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute(SessionKeys.FLASH_ERROR, ex.getMessage());
            return "redirect:/admin/checkin";
        }
    }

    /**
     * Xác nhận Check-in cho 1 vé đơn lẻ
     */
    @PostMapping("/ticket")
    public String checkInSingleTicket(
            @RequestParam("maVe") String maVe,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "ghiChu", required = false) String ghiChu,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        CurrentUser currentUser = (session != null) ? (CurrentUser) session.getAttribute(SessionKeys.CURRENT_USER) : null;
        String staffCode = (currentUser != null) ? currentUser.getMaNguoiDung() : null;

        try {
            CheckIn ci = checkInService.checkInSingleTicket(maVe, staffCode, ghiChu);
            redirectAttributes.addFlashAttribute(SessionKeys.FLASH_SUCCESS,
                    "Soát vé thành công cho vé [" + maVe + "]! Mã Check-in: " + ci.getMaCheckIn());
        } catch (Exception ex) {
            log.warn("Lỗi soát vé {}: {}", maVe, ex.getMessage());
            redirectAttributes.addFlashAttribute(SessionKeys.FLASH_ERROR, ex.getMessage());
        }

        String returnKey = (keyword != null && !keyword.trim().isEmpty()) ? keyword.trim() : maVe;
        return "redirect:/admin/checkin/lookup?keyword=" + returnKey;
    }

    /**
     * Xác nhận Check-in toàn bộ vé trong đơn hàng
     */
    @PostMapping("/all")
    public String checkInAllTickets(
            @RequestParam("maDonDat") String maDonDat,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "ghiChu", required = false) String ghiChu,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        CurrentUser currentUser = (session != null) ? (CurrentUser) session.getAttribute(SessionKeys.CURRENT_USER) : null;
        String staffCode = (currentUser != null) ? currentUser.getMaNguoiDung() : null;

        try {
            List<CheckIn> list = checkInService.checkInAllTicketsInOrder(maDonDat, staffCode, ghiChu);
            redirectAttributes.addFlashAttribute(SessionKeys.FLASH_SUCCESS,
                    "Đã Check-in thành công toàn bộ (" + list.size() + ") vé trong đơn hàng [" + maDonDat + "]!");
        } catch (Exception ex) {
            log.warn("Lỗi check-in cả đơn {}: {}", maDonDat, ex.getMessage());
            redirectAttributes.addFlashAttribute(SessionKeys.FLASH_ERROR, ex.getMessage());
        }

        String returnKey = (keyword != null && !keyword.trim().isEmpty()) ? keyword.trim() : maDonDat;
        return "redirect:/admin/checkin/lookup?keyword=" + returnKey;
    }
}
