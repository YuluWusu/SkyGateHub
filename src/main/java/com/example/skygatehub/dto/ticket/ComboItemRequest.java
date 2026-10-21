package com.example.skygatehub.dto.ticket;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ComboItemRequest {

    private String maThanhPhan;
    private Integer soLuongMoiCombo = 1;
    private Boolean theoDoiSuDung = true;
    private String ghiChu;
}
