package com.example.skygatehub.service.checkin;

import com.example.skygatehub.dto.checkin.CheckInLookupResponse;
import com.example.skygatehub.entity.checkin.CheckIn;

import java.util.List;

public interface CheckInService {

    CheckInLookupResponse lookupTicket(String keyword);

    CheckIn checkInSingleTicket(String maVe, String maNhanVien, String ghiChu);

    List<CheckIn> checkInAllTicketsInOrder(String maDonDat, String maNhanVien, String ghiChu);

    List<CheckIn> getRecentTicketCheckIns(int limit);
}
