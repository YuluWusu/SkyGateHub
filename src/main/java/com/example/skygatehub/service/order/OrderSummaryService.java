package com.example.skygatehub.service.order;

import com.example.skygatehub.dto.order.OrderSummaryResponse;
import com.example.skygatehub.dto.order.OrderSummaryResponse.OrderItem;
import com.example.skygatehub.entity.order.DonDat;
import com.example.skygatehub.entity.payment.ThanhToan;
import com.example.skygatehub.repository.order.DonDatRepository;
import com.example.skygatehub.repository.payment.ThanhToanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderSummaryService {

    private final DonDatRepository donDatRepository;
    private final ThanhToanRepository thanhToanRepository;

    public OrderSummaryResponse getOrderSummary(String orderId) {
        // Lấy thông tin đơn đặt
        DonDat donDat = donDatRepository.findByMaDonDat(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));

        // Lấy các thanh toán liên quan
        List<ThanhToan> payments = thanhToanRepository.findByDonDat_MaDonDatOrderByThoiGianTaoAsc(orderId);

        // Tổng hợp trạng thái thanh toán (có thể có nhiều trạng thái)
        String paymentStatus = payments.stream()
                .map(p -> p.getTrangThai().name())
                .distinct()
                .collect(Collectors.joining(", "));

        // Tổng số tiền đã thanh toán (nếu cần)
        BigDecimal paidAmount = payments.stream()
                .map(ThanhToan::getSoTien)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Tạo DTO trả về
        OrderSummaryResponse response = new OrderSummaryResponse();
        response.setOrderId(donDat.getMaDonDat());
        response.setTotalAmount(donDat.getTongTien()); // hoặc trường tổng tiền thực tế
        response.setCreatedAt(donDat.getThoiGianDat());
        response.setPaymentStatus(paymentStatus);
        // TODO: map chi tiết đơn sang OrderItem nếu cần
        response.setItems(Collections.emptyList());
        return response;
    }
}
