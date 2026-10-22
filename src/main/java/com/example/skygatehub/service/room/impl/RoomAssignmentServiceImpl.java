package com.example.skygatehub.service.room.impl;

import com.example.skygatehub.dto.room.PendingAssignmentResponse;
import com.example.skygatehub.entity.room.PhanPhong;
import com.example.skygatehub.entity.room.Phong;
import com.example.skygatehub.service.room.RoomAssignmentService;
import com.example.skygatehub.service.room.RoomOperationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class RoomAssignmentServiceImpl implements RoomAssignmentService {

    private final RoomOperationService roomOperationService;

    @Override
    @Transactional(readOnly = true)
    public List<PendingAssignmentResponse> getPendingAssignments(LocalDate targetDate) {
        return roomOperationService.getPendingBookingsForAssignment(targetDate);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Phong> getAvailableRoomsForBooking(String maDatPhong) {
        return roomOperationService.getAvailablePhysicalRoomsForBooking(maDatPhong);
    }

    @Override
    public PhanPhong assignRoom(String maDatPhong, String maPhong, String maNhanVien) {
        return roomOperationService.assignRoomToBooking(maDatPhong, maPhong, maNhanVien);
    }

    @Override
    public void unassignRoom(String maPhanPhong) {
        roomOperationService.unassignRoom(maPhanPhong);
    }

    @Override
    public String checkInRoom(String maDatPhong, String maNhanVien) {
        return roomOperationService.checkInRoom(maDatPhong, maNhanVien);
    }

    @Override
    public String checkOutRoom(String maDatPhong, String maNhanVien) {
        return roomOperationService.checkOutRoom(maDatPhong, maNhanVien);
    }
}
