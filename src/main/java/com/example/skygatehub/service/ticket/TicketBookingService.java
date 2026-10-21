package com.example.skygatehub.service.ticket;

import com.example.skygatehub.dto.ticket.TicketQuoteRequest;
import com.example.skygatehub.dto.ticket.TicketQuoteResponse;
import com.example.skygatehub.entity.ticket.KhoVe;

import java.time.LocalDate;
import java.util.List;

public interface TicketBookingService {

    TicketQuoteResponse layBaoGiaVe(TicketQuoteRequest yeuCau);

    default TicketQuoteResponse getTicketQuote(TicketQuoteRequest request) {
        return layBaoGiaVe(request);
    }

    boolean kiemTraVaGiuCho(String maLoaiVe, LocalDate ngayThamQuan, int soLuong);

    default boolean checkAndReserveTicket(String maLoaiVe, LocalDate ngayThamQuan, int soLuong) {
        return kiemTraVaGiuCho(maLoaiVe, ngayThamQuan, soLuong);
    }

    List<KhoVe> layTonKhoTheoNgay(LocalDate ngayThamQuan);

    default List<KhoVe> getInventoryByDate(LocalDate ngayThamQuan) {
        return layTonKhoTheoNgay(ngayThamQuan);
    }

    KhoVe capNhatHanMucKho(String maLoaiVe, LocalDate ngayThamQuan, Integer hanMucToiDa);

    default KhoVe updateInventoryLimit(String maLoaiVe, LocalDate ngayThamQuan, Integer maxLimit) {
        return capNhatHanMucKho(maLoaiVe, ngayThamQuan, maxLimit);
    }

    List<KhoVe> layTonKhoTheoVe(String maLoaiVe);

    default List<KhoVe> getInventoryByTicket(String maLoaiVe) {
        return layTonKhoTheoVe(maLoaiVe);
    }
}
