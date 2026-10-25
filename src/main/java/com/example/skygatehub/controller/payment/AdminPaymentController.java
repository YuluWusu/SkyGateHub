package com.example.skygatehub.controller.payment;

import com.example.skygatehub.dto.payment.PaymentResponse;
import com.example.skygatehub.entity.payment.ThanhToan;
import com.example.skygatehub.common.enums.TrangThaiThanhToan;
import com.example.skygatehub.repository.payment.ThanhToanRepository;
import lombok.RequiredArgsConstructor;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Admin API for managing thanh toán records.
 * Provides endpoints to retrieve a payment by its mã thanh toán (paymentId)
 * and to update its status (e.g., CHO_THANH_TOAN → DA_THANH_TOAN, HUY, …).
 */
@RestController
@RequestMapping("/admin/payments")
@RequiredArgsConstructor
public class AdminPaymentController {

    private final ThanhToanRepository thanhToanRepository;

    /** Lấy chi tiết thanh toán theo mã thanh toán (paymentId) */
    @GetMapping("/{paymentId}")
    public ResponseEntity<PaymentResponse> getPayment(@PathVariable String paymentId) {
        Optional<ThanhToan> opt = thanhToanRepository.findByMaThanhToan(paymentId);
        if (opt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        ThanhToan entity = opt.get();
        return ResponseEntity.ok(mapToResponse(entity));
    }

    /**
     * Cập nhật trạng thái thanh toán.
     * Body JSON: { "status": "DA_THANH_TOAN" } // hoặc "HUY", "CHO_THANH_TOAN", …
     */
    @PutMapping("/{paymentId}/status")
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
            // Khi thanh toán thành công, ghi thời gian thực tế
            if (newStatus == TrangThaiThanhToan.DA_THANH_TOAN) {
                entity.setThoiGianThanhToan(LocalDateTime.now());
            }
            thanhToanRepository.save(entity);
        } catch (IllegalArgumentException e) {
            // Giá trị status không khớp enum
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
    private static class StatusUpdateRequest {
        /** Tên enum `TrangThaiThanhToan` (VD: "DA_THANH_TOAN") */
        private String status;
    }
}
