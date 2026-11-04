package com.example.skygatehub.common.util;

import com.example.skygatehub.common.enums.LoaiNgay;
import com.example.skygatehub.repository.ticket.NgayDacBietRepository;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Tiện ích giải quyết loại ngày và quy tắc tính giá phụ thu (Quy tắc nghiệp vụ D1, D2).
 * Đọc cấu hình app.holidays và tỷ lệ đặt cọc app.deposit-rate từ application.properties.
 */
@Component
public class DayTypeResolver {

    private final Set<String> holidays = new HashSet<>();

    @Getter
    private final BigDecimal depositRate;

    private final NgayDacBietRepository ngayDacBietRepository;

    @Autowired
    public DayTypeResolver(
            @Value("${app.holidays:01-01,04-30,05-01,09-02}") String holidaysConfig,
            @Value("${app.deposit-rate:0.30}") BigDecimal depositRate,
            @Autowired(required = false) NgayDacBietRepository ngayDacBietRepository) {

        this.depositRate = (depositRate != null) ? depositRate : new BigDecimal("0.30");
        this.ngayDacBietRepository = ngayDacBietRepository;

        if (holidaysConfig != null && !holidaysConfig.isBlank()) {
            Arrays.stream(holidaysConfig.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .forEach(this.holidays::add);
        }
    }

    /**
     * Kiểm tra một ngày có phải là ngày lễ hàng năm không (MM-dd)
     */
    public boolean isHoliday(LocalDate date) {
        if (date == null) return false;
        String mmDd = date.format(DateTimeFormatter.ofPattern("MM-dd"));
        if (holidays.contains(mmDd)) {
            return true;
        }
        if (ngayDacBietRepository != null) {
            try {
                return ngayDacBietRepository.existsByNgayApDungAndTrangThai(date, true);
            } catch (Exception ignored) {}
        }
        return false;
    }

    /**
     * Quy tắc D1: Đêm thứ Bảy (Saturday) và Ngày lễ phụ thu +20% tiền phòng.
     * Thứ Sáu và Chủ Nhật áp dụng giá tiêu chuẩn ngày thường.
     */
    public boolean isRoomSurchargeNight(LocalDate date) {
        if (date == null) return false;
        return date.getDayOfWeek() == DayOfWeek.SATURDAY || isHoliday(date);
    }

    /**
     * Xác định loại ngày: THU_BAY_LE hay NGAY_THUONG
     */
    public LoaiNgay resolveLoaiNgay(LocalDate date) {
        return isRoomSurchargeNight(date) ? LoaiNgay.THU_BAY_LE : LoaiNgay.NGAY_THUONG;
    }

    /**
     * Hệ số nhân tiền phòng: 1.20 nếu là thứ Bảy/Lễ, ngược lại là 1.00
     */
    public BigDecimal getRoomNightMultiplier(LocalDate date) {
        return isRoomSurchargeNight(date) ? new BigDecimal("1.20") : BigDecimal.ONE;
    }
}

