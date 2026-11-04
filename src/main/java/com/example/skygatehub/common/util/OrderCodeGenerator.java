package com.example.skygatehub.common.util;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Tiện ích sinh mã định danh duy nhất cho toàn hệ thống SkyGateHub.
 * Sử dụng AtomicInteger kết hợp Timestamp để đảm bảo không trùng lặp ngay cả khi chạy đa luồng.
 * Tất cả các mã (trừ MA_DON_HANG <= 30 ký tự) đều có độ dài <= 20 ký tự theo quy định schema.
 */
@Component
public class OrderCodeGenerator {

    private static final DateTimeFormatter TS_FORMAT = DateTimeFormatter.ofPattern("yyMMddHHmmss");
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyMMdd");

    private static final AtomicInteger SEQ_DON_DAT = new AtomicInteger(100);
    private static final AtomicInteger SEQ_DON_HANG = new AtomicInteger(1000);
    private static final AtomicInteger SEQ_CHI_TIET = new AtomicInteger(100);
    private static final AtomicInteger SEQ_VE = new AtomicInteger(1000);
    private static final AtomicInteger SEQ_DAT_PHONG = new AtomicInteger(100);
    private static final AtomicInteger SEQ_DAT_BAN = new AtomicInteger(100);
    private static final AtomicInteger SEQ_THANH_TOAN = new AtomicInteger(100);

    private static int nextSeq(AtomicInteger counter, int max) {
        return counter.updateAndGet(val -> (val >= max) ? 100 : val + 1);
    }

    /**
     * Sinh mã đơn đặt MA_DON_DAT (VD: DD2611040215100 - 15 ký tự <= 20)
     */
    public static String generateMaDonDat() {
        return "DD" + LocalDateTime.now().format(TS_FORMAT) + (nextSeq(SEQ_DON_DAT, 999) % 1000);
    }

    /**
     * Sinh mã đơn hàng hiển thị MA_DON_HANG (VD: CTDG-261104-1001 <= 30)
     */
    public static String generateMaDonHang(String prefix) {
        String p = (prefix != null && !prefix.isBlank()) ? prefix : "CTDG";
        int seq = SEQ_DON_HANG.updateAndGet(val -> (val >= 9999) ? 1000 : val + 1);
        return p + "-" + LocalDateTime.now().format(DATE_FORMAT) + "-" + seq;
    }

    /**
     * Sinh mã chi tiết đơn MA_CHI_TIET (VD: CTD2611040215100 - 16 ký tự <= 20)
     */
    public static String generateMaChiTiet() {
        return "CTD" + LocalDateTime.now().format(TS_FORMAT) + (nextSeq(SEQ_CHI_TIET, 999) % 1000);
    }

    /**
     * Sinh mã vé cá nhân MA_VE (VD: V26110402151001 - 16 ký tự <= 20)
     */
    public static String generateMaVe() {
        int seq = SEQ_VE.updateAndGet(val -> (val >= 9999) ? 1000 : val + 1);
        return "V" + LocalDateTime.now().format(TS_FORMAT) + seq;
    }

    /**
     * Sinh mã đặt phòng MA_DAT_PHONG (VD: DP2611040215100 - 15 ký tự <= 20)
     */
    public static String generateMaDatPhong() {
        return "DP" + LocalDateTime.now().format(TS_FORMAT) + (nextSeq(SEQ_DAT_PHONG, 999) % 1000);
    }

    /**
     * Sinh mã đặt bàn MA_DAT_BAN (VD: DB2611040215100 - 15 ký tự <= 20)
     */
    public static String generateMaDatBan() {
        return "DB" + LocalDateTime.now().format(TS_FORMAT) + (nextSeq(SEQ_DAT_BAN, 999) % 1000);
    }

    /**
     * Sinh mã thanh toán MA_THANH_TOAN (VD: TT2611040215100 - 15 ký tự <= 20)
     */
    public static String generateMaThanhToan() {
        return "TT" + LocalDateTime.now().format(TS_FORMAT) + (nextSeq(SEQ_THANH_TOAN, 999) % 1000);
    }
}

