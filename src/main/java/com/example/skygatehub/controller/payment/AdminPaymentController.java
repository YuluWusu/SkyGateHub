package com.example.skygatehub.controller.payment;

import com.example.skygatehub.common.constant.SessionKeys;
import com.example.skygatehub.common.enums.TrangThaiThanhToan;
import com.example.skygatehub.common.security.RequireRole;
import com.example.skygatehub.dto.payment.PaymentResponse;
import com.example.skygatehub.entity.payment.ThanhToan;
import com.example.skygatehub.repository.payment.ThanhToanRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Admin Controller for managing thanh toán records.
 * Supports both Web UI (HTML) and REST API (JSON).
 */
@Controller
@RequestMapping("/admin/payments")
@RequiredArgsConstructor
@RequireRole({"QUAN_LY", "ROLE_ADMIN", "VT001", "ADMIN", "NHAN_VIEN_VE", "VT004"})
public class AdminPaymentController {

    private final ThanhToanRepository thanhToanRepository;

    /**
     * Web UI: Hiển thị danh sách lịch sử thanh toán & đặt cọc
     */
    @GetMapping
    public String listPayments(
            @RequestParam(value = "status", required = false, defaultValue = "ALL") String status,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            Model model) {

        Pageable pageable = PageRequest.of(Math.max(0, page), size, Sort.by(Sort.Direction.DESC, "thoiGianTao"));
        Page<ThanhToan> paymentsPage;

        if (keyword != null && !keyword.trim().isEmpty()) {
            String kw = keyword.trim();
            paymentsPage = thanhToanRepository.findByMaThanhToanContainingIgnoreCaseOrMaGiaoDichContainingIgnoreCaseOrDonDat_MaDonDatContainingIgnoreCaseOrderByThoiGianTaoDesc(
                    kw, kw, kw, pageable);
        } else if (!"ALL".equalsIgnoreCase(status)) {
            try {
                TrangThaiThanhToan enumStatus = TrangThaiThanhToan.valueOf(status.toUpperCase());
                paymentsPage = thanhToanRepository.findByTrangThaiOrderByThoiGianTaoDesc(enumStatus, pageable);
            } catch (IllegalArgumentException e) {
                paymentsPage = thanhToanRepository.findAllByOrderByThoiGianTaoDesc(pageable);
            }
        } else {
            paymentsPage = thanhToanRepository.findAllByOrderByThoiGianTaoDesc(pageable);
        }

        BigDecimal totalPaid = thanhToanRepository.sumAllPaid();
        if (totalPaid == null) totalPaid = BigDecimal.ZERO;

        BigDecimal totalPending = thanhToanRepository.sumAllPending();
        if (totalPending == null) totalPending = BigDecimal.ZERO;

        model.addAttribute("paymentsPage", paymentsPage);
        model.addAttribute("currentStatus", status);
        model.addAttribute("keyword", keyword != null ? keyword.trim() : "");
        model.addAttribute("totalPaid", totalPaid);
        model.addAttribute("totalPending", totalPending);

        return "payment/admin/list";
    }

    /**
     * Web UI: Cập nhật trạng thái thanh toán từ giao diện quản trị
     */
    @PostMapping("/{paymentId}/quick-status")
    public String updateStatusWeb(@PathVariable String paymentId,
                                  @RequestParam("status") String status,
                                  RedirectAttributes redirectAttributes) {
        Optional<ThanhToan> opt = thanhToanRepository.findByMaThanhToan(paymentId);
        if (opt.isEmpty()) {
            redirectAttributes.addFlashAttribute(SessionKeys.FLASH_ERROR, "Không tìm thấy giao dịch: " + paymentId);
            return "redirect:/admin/payments";
        }
        ThanhToan entity = opt.get();
        try {
            TrangThaiThanhToan newStatus = TrangThaiThanhToan.valueOf(status.toUpperCase());
            entity.setTrangThai(newStatus);
            if (newStatus == TrangThaiThanhToan.DA_THANH_TOAN) {
                entity.setThoiGianThanhToan(LocalDateTime.now());
            }
            thanhToanRepository.save(entity);
            redirectAttributes.addFlashAttribute(SessionKeys.FLASH_SUCCESS, "Đã cập nhật trạng thái thanh toán: " + newStatus.name());
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute(SessionKeys.FLASH_ERROR, "Trạng thái không hợp lệ: " + status);
        }
        return "redirect:/admin/payments";
    }

    /**
     * REST API: Lấy chi tiết thanh toán theo mã thanh toán (paymentId)
     */
    @GetMapping(value = "/{paymentId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<PaymentResponse> getPayment(@PathVariable String paymentId) {
        Optional<ThanhToan> opt = thanhToanRepository.findByMaThanhToan(paymentId);
        if (opt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        ThanhToan entity = opt.get();
        return ResponseEntity.ok(mapToResponse(entity));
    }

    /**
     * REST API: Cập nhật trạng thái thanh toán qua JSON.
     * Body JSON: { "status": "DA_THANH_TOAN" }
     */
    @PutMapping(value = "/{paymentId}/status", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<PaymentResponse> updateStatus(@PathVariable String paymentId,
                                                        @RequestBody StatusUpdateRequest request) {
        Optional<ThanhToan> opt = thanhToanRepository.findByMaThanhToan(paymentId);
        if (opt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        ThanhToan entity = opt.get();

        try {
            TrangThaiThanhToan newStatus = TrangThaiThanhToan.valueOf(request.getStatus());
            entity.setTrangThai(newStatus);
            if (newStatus == TrangThaiThanhToan.DA_THANH_TOAN) {
                entity.setThoiGianThanhToan(LocalDateTime.now());
            }
            thanhToanRepository.save(entity);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(mapToResponse(entity));
    }

    // ------------------------------------------------------------------------
    // Helper & DTO
    // ------------------------------------------------------------------------
    private PaymentResponse mapToResponse(ThanhToan entity) {
        PaymentResponse resp = new PaymentResponse();
        resp.setPaymentId(entity.getMaThanhToan());
        resp.setOrderId(entity.getDonDat() != null ? entity.getDonDat().getMaDonDat() : null);
        resp.setAmount(entity.getSoTien());
        resp.setStatus(entity.getTrangThai().name());
        resp.setCreatedAt(entity.getThoiGianTao());
        resp.setPaidAt(entity.getThoiGianThanhToan());
        return resp;
    }

    @Data
    public static class StatusUpdateRequest {
        private String status;
    }
}
