package com.example.skygatehub.service.payment.impl;

import com.example.skygatehub.dto.payment.PaymentRequest;
import com.example.skygatehub.dto.payment.PaymentResponse;
import com.example.skygatehub.entity.payment.ThanhToan;
import com.example.skygatehub.repository.payment.ThanhToanRepository;
import com.example.skygatehub.repository.order.DonDatRepository;
import com.example.skygatehub.service.payment.PaymentService;
import com.example.skygatehub.common.enums.LoaiGiaoDich;
import com.example.skygatehub.common.enums.PhuongThucThanhToan;
import com.example.skygatehub.common.enums.TrangThaiThanhToan;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final DonDatRepository donDatRepository;
    private final ThanhToanRepository thanhToanRepository;

    @Override
    public PaymentResponse createPayment(PaymentRequest request) {
        // Tìm đơn đặt hàng
        var donDat = donDatRepository.findByMaDonDat(request.getOrderId())
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + request.getOrderId()));

        // Tạo mã thanh toán duy nhất
        String paymentId = "PAY-" + UUID.randomUUID().toString();

        // Chuyển đổi phương thức thanh toán (nếu enum phù hợp)
        PhuongThucThanhToan phuongThuc;
        try {
            phuongThuc = PhuongThucThanhToan.valueOf(request.getPaymentMethod().toUpperCase());
        } catch (Exception e) {
            // Nếu không khớp, để null (có thể mở rộng sau này)
            phuongThuc = null;
        }

        // Tạo và lưu thực thể ThanhToan
        ThanhToan thanhToan = new ThanhToan();
        thanhToan.setMaThanhToan(paymentId);
        thanhToan.setDonDat(donDat);
        thanhToan.setLoaiGiaoDich(LoaiGiaoDich.THANH_TOAN);
        thanhToan.setPhuongThucThanhToan(phuongThuc);
        thanhToan.setSoTien(request.getAmount());
        thanhToan.setTrangThai(TrangThaiThanhToan.CHO_THANH_TOAN);
        thanhToan.setThoiGianTao(LocalDateTime.now());
        thanhToanRepository.save(thanhToan);

        // Trả về DTO
        PaymentResponse response = new PaymentResponse();
        response.setPaymentId(paymentId);
        response.setOrderId(request.getOrderId());
        response.setAmount(request.getAmount());
        response.setStatus(TrangThaiThanhToan.CHO_THANH_TOAN.name());
        response.setCreatedAt(thanhToan.getThoiGianTao());
        response.setPaidAt(null);
        return response;
    }

    @Override
    public PaymentResponse getLatestPaymentByOrderId(String orderId) {
        List<ThanhToan> list = thanhToanRepository.findByDonDat_MaDonDatOrderByThoiGianTaoAsc(orderId);
        if (list.isEmpty()) {
            throw new IllegalArgumentException("No payment found for order: " + orderId);
        }
        ThanhToan last = list.get(list.size() - 1);
        PaymentResponse response = new PaymentResponse();
        response.setPaymentId(last.getMaThanhToan());
        response.setOrderId(orderId);
        response.setAmount(last.getSoTien());
        response.setStatus(last.getTrangThai().name());
        response.setCreatedAt(last.getThoiGianTao());
        response.setPaidAt(last.getThoiGianThanhToan());
        return response;
    }
}
