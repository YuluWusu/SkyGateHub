package com.example.skygatehub.dto.dining;

import com.example.skygatehub.entity.dining.DatBan;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MealScheduleRow {

    private String maCaAn;
    private String tenCaAn;
    private String khungGio;
    private int tongDonDat;
    private int tongSoKhach;
    private int soBanDaXep;
    private int soBanChuaXep;
    private int soDonDaCheckIn;
    private int soDonDaHoanThanh;

    @Builder.Default
    private List<DatBan> danhSachDatBan = new ArrayList<>();
}
