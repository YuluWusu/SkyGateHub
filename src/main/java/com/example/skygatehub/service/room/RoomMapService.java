package com.example.skygatehub.service.room;

import com.example.skygatehub.dto.room.RoomMapCell;
import com.example.skygatehub.dto.room.RoomMapSummaryDto;

import java.time.LocalDate;
import java.util.List;

public interface RoomMapService {

    List<RoomMapCell> getRoomMap(LocalDate targetDate, Integer maKhu, String maLoaiPhong, String trangThaiFilter);

    RoomMapSummaryDto getRoomMapSummary(LocalDate targetDate, Integer maKhu, String maLoaiPhong);
}
