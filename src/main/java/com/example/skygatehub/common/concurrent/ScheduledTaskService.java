package com.example.skygatehub.common.concurrent;

import com.example.skygatehub.common.enums.TrangThaiDon;
import com.example.skygatehub.order.entity.DonDat;
import com.example.skygatehub.order.repository.DonDatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service thực thi tác vụ định kỳ @Scheduled (Tiêu chí 5.1).
 * In log rõ tên luồng: [THREAD: ...]
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ScheduledTaskService {

    private final DonDatRepository donDatRepository;

    /**
     * Tác vụ định kỳ 1: Rà soát đơn chờ xác nhận quá hạn (mặc định quá 30 phút chưa cọc/thanh toán).
     * Chạy mỗi 60 giây.
     */
    @Scheduled(fixedDelay = 60000)
    @Transactional
    public void scanExpiredPendingOrders() {
        String threadName = Thread.currentThread().getName();
        LocalDateTime threshold = LocalDateTime.now().minusMinutes(30);

        log.info("[THREAD: {}] [SCHEDULED-CRON] Bắt đầu rà soát các đơn đặt quá hạn trước {}", threadName, threshold);

        List<DonDat> expiredOrders = donDatRepository.findByTrangThaiDonAndThoiGianDatBefore(
                TrangThaiDon.CHO_XAC_NHAN, threshold);

        if (expiredOrders.isEmpty()) {
            log.info("[THREAD: {}] [SCHEDULED-CRON] Không có đơn đặt nào hết hạn cần hủy.", threadName);
            return;
        }

        for (DonDat don : expiredOrders) {
            don.setTrangThaiDon(TrangThaiDon.DA_HUY);
            don.setGhiChu((don.getGhiChu() == null ? "" : don.getGhiChu() + " | ") + "Tự động hủy do hết hạn giữ chỗ (30 phút).");
            donDatRepository.save(don);
            log.warn("[THREAD: {}] [SCHEDULED-CRON] Đã tự động hủy đơn hết hạn giữ chỗ: {} (Mã đơn hàng: {})",
                    threadName, don.getMaDonDat(), don.getMaDonHang());
        }

        log.info("[THREAD: {}] [SCHEDULED-CRON] Hoàn tất rà soát. Đã hủy {} đơn hết hạn.", threadName, expiredOrders.size());
    }

    /**
     * Tác vụ định kỳ 2: Ghi log trạng thái heartbeat hệ thống mỗi 5 phút.
     */
    @Scheduled(fixedRate = 300000)
    public void systemHeartbeatLog() {
        String threadName = Thread.currentThread().getName();
        log.info("[THREAD: {}] [SCHEDULED-HEARTBEAT] Hệ thống SkyGateHub đang hoạt động ổn định tại thời điểm {}",
                threadName, LocalDateTime.now());
    }
}
