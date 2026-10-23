package com.example.skygatehub.dto.checkin;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CheckInRequest {
    private String keyword;
    private String maVe;
    private String ghiChu;
}
