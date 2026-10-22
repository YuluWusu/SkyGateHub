package com.example.skygatehub.service.room;

import com.example.skygatehub.dto.room.PendingAssignmentResponse;
import com.example.skygatehub.entity.room.PhanPhong;
import com.example.skygatehub.entity.room.Phong;

import java.time.LocalDate;
import java.util.List;

public interface RoomAssignmentService {

    List<PendingAssignmentResponse> getPendingAssignments(LocalDate targetDate);

    List<Phong> getAvailableRoomsForBooking(String maDatPhong);

    PhanPhong assignRoom(String maDatPhong, String maPhong, String maNhanVien);

    void unassignRoom(String maPhanPhong);

    String checkInRoom(String maDatPhong, String maNhanVien);

    String checkOutRoom(String maDatPhong, String maNhanVien);
}
