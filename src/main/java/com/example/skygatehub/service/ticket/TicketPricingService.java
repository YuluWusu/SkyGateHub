package com.example.skygatehub.service.ticket;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface TicketPricingService {

    BigDecimal tinhGiaVe(String maLoaiVe, LocalDate ngayThamQuan);

    default BigDecimal calculateTicketPrice(String maLoaiVe, LocalDate ngayThamQuan) {
        return tinhGiaVe(maLoaiVe, ngayThamQuan);
    }

    String xacDinhDieuKienNgay(LocalDate ngay);

    default String resolveDayCondition(LocalDate date) {
        return xacDinhDieuKienNgay(date);
    }
}
