package com.example.skygatehub.service.ticket;

import com.example.skygatehub.entity.ticket.BangGiaVe;
import com.example.skygatehub.entity.ticket.LoaiVe;
import com.example.skygatehub.repository.ticket.BangGiaVeRepository;
import com.example.skygatehub.repository.ticket.LoaiVeRepository;
import com.example.skygatehub.repository.ticket.NgayDacBietRepository;
import com.example.skygatehub.service.ticket.impl.TicketPricingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TicketPricingServiceTest {

    @Mock
    private BangGiaVeRepository bangGiaVeRepository;

    @Mock
    private NgayDacBietRepository ngayDacBietRepository;

    @Mock
    private LoaiVeRepository loaiVeRepository;

    private TicketPricingService ticketPricingService;

    @BeforeEach
    void setUp() {
        ticketPricingService = new TicketPricingServiceImpl(
                bangGiaVeRepository,
                ngayDacBietRepository,
                loaiVeRepository
        );
    }

    @Test
    void testCalculateTicketPriceWithHoliday() {
        LocalDate holidayDate = LocalDate.of(2026, 4, 30);
        String ticketCode = "LV001";

        when(ngayDacBietRepository.existsByNgayApDungAndTrangThai(holidayDate, true)).thenReturn(true);

        BangGiaVe holidayPrice = new BangGiaVe();
        holidayPrice.setDieuKienGia("NGAY_LE");
        holidayPrice.setDonGia(new BigDecimal("200000"));
        holidayPrice.setUuTien(10);

        when(bangGiaVeRepository.findValidPrices(eq(ticketCode), eq(holidayDate)))
                .thenReturn(List.of(holidayPrice));

        BigDecimal calculatedPrice = ticketPricingService.calculateTicketPrice(ticketCode, holidayDate);

        assertEquals(new BigDecimal("200000"), calculatedPrice);
    }

    @Test
    void testCalculateTicketPriceFallbackToTicketBasePrice() {
        LocalDate regularDate = LocalDate.of(2026, 5, 6);
        String ticketCode = "LV001";

        when(ngayDacBietRepository.existsByNgayApDungAndTrangThai(regularDate, true)).thenReturn(false);
        when(bangGiaVeRepository.findValidPrices(eq(ticketCode), eq(regularDate))).thenReturn(List.of());

        LoaiVe ticket = new LoaiVe();
        ticket.setMaLoaiVe(ticketCode);
        ticket.setGiaVe(new BigDecimal("150000"));

        when(loaiVeRepository.findByMaLoaiVe(ticketCode)).thenReturn(Optional.of(ticket));

        BigDecimal calculatedPrice = ticketPricingService.calculateTicketPrice(ticketCode, regularDate);

        assertEquals(new BigDecimal("150000"), calculatedPrice);
    }
}
