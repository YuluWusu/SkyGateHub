package com.example.skygatehub.common.util;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Tiện ích xử lý khoảng thời gian (Check-in, Check-out).
 */
public final class DateRangeUtils {

    private DateRangeUtils() {}

    /**
     * Lấy danh sách các đêm lưu trú [checkIn, checkOut).
     */
    public static List<LocalDate> getStayNights(LocalDate checkIn, LocalDate checkOut) {
        if (checkIn == null || checkOut == null || !checkOut.isAfter(checkIn)) {
            return Collections.emptyList();
        }
        long nights = ChronoUnit.DAYS.between(checkIn, checkOut);
        List<LocalDate> dates = new ArrayList<>((int) nights);
        for (int i = 0; i < nights; i++) {
            dates.add(checkIn.plusDays(i));
        }
        return dates;
    }

    /**
     * Tính số đêm lưu trú.
     */
    public static long calculateNights(LocalDate checkIn, LocalDate checkOut) {
        if (checkIn == null || checkOut == null || !checkOut.isAfter(checkIn)) {
            return 1;
        }
        long nights = ChronoUnit.DAYS.between(checkIn, checkOut);
        return nights <= 0 ? 1 : nights;
    }
}

