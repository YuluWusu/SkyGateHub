package com.example.skygatehub.dto.room;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoomMapSummaryDto {

    private long tongSoPhong = 0;
    private long soPhongTrong = 0;
    private long soPhongDaPhan = 0;
    private long soPhongDangO = 0;
    private long soPhongBaoTri = 0;

    public double getTyLeLapDay() {
        if (tongSoPhong == 0) return 0.0;
        return ((double) (soPhongDaPhan + soPhongDangO) / tongSoPhong) * 100.0;
    }

    public String getTyLeLapDayFormatted() {
        return String.format("%.1f%%", getTyLeLapDay());
    }
}
