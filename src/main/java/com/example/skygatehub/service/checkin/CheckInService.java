package com.example.skygatehub.service.checkin;

import com.example.skygatehub.dto.checkin.CheckInLookupResponse;
import com.example.skygatehub.entity.checkin.CheckIn;

import java.util.List;

public interface CheckInService {

    /**
     * Tra cứu thông tin vé/đơn hàng theo từ khóa (Mã vé, mã chuỗi, mã đơn hàng, mã QR, SĐT).
     */
    CheckInLookupResponse lookupTicket(String keyword);

    /**
     * Thực hiện Check-in 1 vé cụ thể.
     */
    CheckIn checkInSingleTicket(String maVe, String maNhanVien, String ghiChu);

    /**
     * Thực hiện Check-in toàn bộ các vé chưa sử dụng trong 1 đơn hàng.
     */
    List<CheckIn> checkInAllTicketsInOrder(String maDonDat, String maNhanVien, String ghiChu);

    /**
     * Lấy danh sách lượt check-in gần nhất (để hiển thị nhật ký soát vé).
     */
    List<CheckIn> getRecentTicketCheckIns(int limit);
}
