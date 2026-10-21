package com.example.skygatehub.dto.ticket;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class TicketQuoteRequest {

    private String maLoaiVe;
    private LocalDate ngayThamQuan;
    private Integer soLuong = 1;
}
