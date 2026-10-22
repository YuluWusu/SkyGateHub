package com.example.skygatehub.service.room;

import com.example.skygatehub.dto.room.PendingAssignmentResponse;
import com.example.skygatehub.dto.room.RoomMapCell;
import com.example.skygatehub.dto.room.RoomMapSummaryDto;
import com.example.skygatehub.entity.room.PhanPhong;
import com.example.skygatehub.entity.room.Phong;

import java.time.LocalDate;
import java.util.List;

public interface RoomOperationService {

    List<RoomMapCell> buildRoomMap(LocalDate targetDate, Integer maKhu, String maLoaiPhong, String trangThaiFilter);

    RoomMapSummaryDto calculateRoomMapSummary(LocalDate targetDate, Integer maKhu, String maLoaiPhong);

    List<PendingAssignmentResponse> getPendingBookingsForAssignment(LocalDate targetDate);

    List<Phong> getAvailablePhysicalRoomsForBooking(String maDatPhong);

    PhanPhong assignRoomToBooking(String maDatPhong, String maPhong, String maNhanVien);

    void unassignRoom(String maPhanPhong);

    String checkInRoom(String maDatPhong, String maNhanVien);

    String checkOutRoom(String maDatPhong, String maNhanVien);
}
