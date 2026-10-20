package com.example.skygatehub.common.concurrent;

import com.example.skygatehub.entity.order.DonDat;
import com.example.skygatehub.repository.order.DonDatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Service xử lý tác vụ bất đồng bộ @Async (Tiêu chí 5.1).
 * In log rõ tên luồng: [THREAD: ...]
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AsyncTaskService {

    private final DonDatRepository donDatRepository;

    /**
     * Tác vụ nền 1: Sinh mã QR token và cập nhật vào đơn hàng.
     */
    @Async("taskExecutor")
    @Transactional
    public CompletableFuture<String> generateQrCodeAsync(String maDonDat) {
        String threadName = Thread.currentThread().getName();
        log.info("[THREAD: {}] Bắt đầu sinh mã QR token cho đơn hàng: {}", threadName, maDonDat);

        try {
            // Giả lập thời gian xử lý mã hóa tạo token QR
            Thread.sleep(300);
            String qrToken = "QR-CTDG-" + UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase();

            donDatRepository.findByMaDonDat(maDonDat).ifPresent(donDat -> {
                donDat.setMaQr(qrToken);
                donDatRepository.save(donDat);
                log.info("[THREAD: {}] Đã cập nhật thành công mã QR: {} vào đơn hàng: {}", threadName, qrToken, maDonDat);
            });

            return CompletableFuture.completedFuture(qrToken);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("[THREAD: {}] Lỗi ngắt luồng khi sinh mã QR: {}", threadName, e.getMessage());
            return CompletableFuture.completedFuture(null);
        }
    }

    /**
     * Tác vụ nền 2: Gửi thông báo / email xác nhận đặt chỗ.
     */
    @Async("taskExecutor")
    public CompletableFuture<Boolean> sendNotificationAsync(String recipient, String tieuDe, String noiDung) {
        String threadName = Thread.currentThread().getName();
        log.info("[THREAD: {}] Đang gửi thông báo tới [{}], Tiêu đề: {}", threadName, recipient, tieuDe);

        try {
            Thread.sleep(500); // Giả lập độ trễ mạng khi gửi email/SMS
            log.info("[THREAD: {}] Đã gửi thông báo thành công tới [{}]", threadName, recipient);
            return CompletableFuture.completedFuture(true);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("[THREAD: {}] Lỗi ngắt luồng khi gửi thông báo: {}", threadName, e.getMessage());
            return CompletableFuture.completedFuture(false);
        }
    }

    /**
     * Tác vụ nền 3: Gửi email xác nhận thanh toán thành công.
     */
    @Async("taskExecutor")
    public void sendPaymentConfirmationEmailAsync(String email, String maDonHang, BigDecimal soTien) {
        String threadName = Thread.currentThread().getName();
        log.info("[THREAD: {}] Bắt đầu gửi email xác nhận thanh toán {} VNĐ cho đơn [{}] tới email: {}",
                threadName, soTien, maDonHang, email);

        try {
            Thread.sleep(400);
            log.info("[THREAD: {}] Gửi email xác nhận thanh toán đơn [{}] THÀNH CÔNG!", threadName, maDonHang);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("[THREAD: {}] Lỗi khi gửi email xác nhận thanh toán: {}", threadName, e.getMessage());
        }
    }
}

