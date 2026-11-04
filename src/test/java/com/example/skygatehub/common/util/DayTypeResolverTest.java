package com.example.skygatehub.common.util;

import com.example.skygatehub.common.enums.LoaiNgay;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Kiểm thử giải quyết loại ngày và quy tắc phụ thu D1, D2")
class DayTypeResolverTest {

    private final DayTypeResolver resolver = new DayTypeResolver("01-01,04-30,05-01,09-02", new BigDecimal("0.30"), null);

    @Test
    @DisplayName("Thứ Bảy phải tính là đêm phụ thu phòng (+20%)")
    void testSaturdayIsRoomSurchargeNight() {
        // Ngày 07/11/2026 là Thứ Bảy
        LocalDate saturday = LocalDate.of(2026, 11, 7);
        assertTrue(resolver.isRoomSurchargeNight(saturday));
        assertEquals(new BigDecimal("1.20"), resolver.getRoomNightMultiplier(saturday));
        assertEquals(LoaiNgay.THU_BAY_LE, resolver.resolveLoaiNgay(saturday));
    }

    @Test
    @DisplayName("Ngày thường (Thứ Hai -> Thứ Sáu, Chủ Nhật) áp dụng giá gốc 1.00")
    void testWeekdayIsNormalRate() {
        // Ngày 06/11/2026 là Thứ Sáu (áp dụng giá ngày thường theo quy định D1)
        LocalDate friday = LocalDate.of(2026, 11, 6);
        assertFalse(resolver.isRoomSurchargeNight(friday));
        assertEquals(BigDecimal.ONE, resolver.getRoomNightMultiplier(friday));
        assertEquals(LoaiNgay.NGAY_THUONG, resolver.resolveLoaiNgay(friday));

        // Ngày 08/11/2026 là Chủ Nhật
        LocalDate sunday = LocalDate.of(2026, 11, 8);
        assertFalse(resolver.isRoomSurchargeNight(sunday));
        assertEquals(BigDecimal.ONE, resolver.getRoomNightMultiplier(sunday));
    }

    @Test
    @DisplayName("Ngày lễ Quốc Khánh (02/09) phải là ngày lễ và phụ thu +20%")
    void testHolidaySurcharge() {
        LocalDate nationalDay = LocalDate.of(2026, 9, 2);
        assertTrue(resolver.isHoliday(nationalDay));
        assertTrue(resolver.isRoomSurchargeNight(nationalDay));
        assertEquals(new BigDecimal("1.20"), resolver.getRoomNightMultiplier(nationalDay));
    }

    @Test
    @DisplayName("Tỷ lệ đặt cọc mặc định đọc từ cấu hình là 30%")
    void testDepositRate() {
        assertEquals(new BigDecimal("0.30"), resolver.getDepositRate());
    }
}

