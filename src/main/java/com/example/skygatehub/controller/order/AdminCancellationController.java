package com.example.skygatehub.controller.order;

import com.example.skygatehub.common.constant.SessionKeys;
import com.example.skygatehub.common.security.CurrentUser;
import com.example.skygatehub.common.security.RequireRole;
import com.example.skygatehub.entity.cancellation.HuyDon;
import com.example.skygatehub.service.cancellation.TicketCancellationService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/cancellations")
@RequiredArgsConstructor
@RequireRole({"QUAN_LY", "ROLE_ADMIN", "VT001", "ADMIN", "NHAN_VIEN_VE", "VT004"})
public class AdminCancellationController {

    private final TicketCancellationService ticketCancellationService;

    @GetMapping
    public String listCancellations(
            @RequestParam(value = "status", required = false, defaultValue = "ALL") String status,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            Model model) {
        Pageable pageable = PageRequest.of(Math.max(0, page), size);
        Page<HuyDon> requestsPage = ticketCancellationService.listRequests(status, pageable);

        model.addAttribute("requestsPage", requestsPage);
        model.addAttribute("currentStatus", status);
        model.addAttribute("pendingCount", ticketCancellationService.countPendingRequests());
        return "order/admin/cancellations";
    }

    @PostMapping("/{maHuy}/approve")
    public String approve(@PathVariable String maHuy,
                          @RequestParam(value = "ghiChu", required = false) String ghiChu,
                          HttpSession session,
                          RedirectAttributes redirectAttributes) {
        try {
            CurrentUser user = session != null ? (CurrentUser) session.getAttribute(SessionKeys.CURRENT_USER) : null;
            String staffCode = user != null ? user.getMaNguoiDung() : null;
            ticketCancellationService.approveCancellation(maHuy, staffCode, ghiChu);
            redirectAttributes.addFlashAttribute(SessionKeys.FLASH_SUCCESS,
                    "Đã duyệt hủy vé [" + maHuy + "]. Vé và tồn kho đã được cập nhật.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute(SessionKeys.FLASH_ERROR, ex.getMessage());
        }
        return "redirect:/admin/cancellations";
    }

    @PostMapping("/{maHuy}/reject")
    public String reject(@PathVariable String maHuy,
                         @RequestParam(value = "ghiChu", required = false) String ghiChu,
                         HttpSession session,
                         RedirectAttributes redirectAttributes) {
        try {
            CurrentUser user = session != null ? (CurrentUser) session.getAttribute(SessionKeys.CURRENT_USER) : null;
            String staffCode = user != null ? user.getMaNguoiDung() : null;
            ticketCancellationService.rejectCancellation(maHuy, staffCode, ghiChu);
            redirectAttributes.addFlashAttribute(SessionKeys.FLASH_SUCCESS,
                    "Đã từ chối yêu cầu hủy [" + maHuy + "]. Vé vẫn giữ nguyên hiệu lực.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute(SessionKeys.FLASH_ERROR, ex.getMessage());
        }
        return "redirect:/admin/cancellations";
    }
}
