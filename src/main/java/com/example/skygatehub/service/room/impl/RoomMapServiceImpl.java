package com.example.skygatehub.service.room.impl;

import com.example.skygatehub.dto.room.RoomMapCell;
import com.example.skygatehub.dto.room.RoomMapSummaryDto;
import com.example.skygatehub.service.room.RoomMapService;
import com.example.skygatehub.service.room.RoomOperationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoomMapServiceImpl implements RoomMapService {

    private final RoomOperationService roomOperationService;

    @Override
    public List<RoomMapCell> getRoomMap(LocalDate targetDate, Integer maKhu, String maLoaiPhong, String trangThaiFilter) {
        return roomOperationService.buildRoomMap(targetDate, maKhu, maLoaiPhong, trangThaiFilter);
    }

    @Override
    public RoomMapSummaryDto getRoomMapSummary(LocalDate targetDate, Integer maKhu, String maLoaiPhong) {
        return roomOperationService.calculateRoomMapSummary(targetDate, maKhu, maLoaiPhong);
    }
}
