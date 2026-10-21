package com.example.skygatehub.controller.ticket;

import com.example.skygatehub.common.enums.LoaiVeType;
import com.example.skygatehub.dto.ticket.TicketQuoteRequest;
import com.example.skygatehub.dto.ticket.TicketQuoteResponse;
import com.example.skygatehub.entity.ticket.ComboThanhPhan;
import com.example.skygatehub.entity.ticket.LoaiVe;
import com.example.skygatehub.service.ticket.SanPhamVeService;
import com.example.skygatehub.service.ticket.TicketBookingService;
import com.example.skygatehub.service.ticket.TicketPricingService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/tickets")
public class TicketController {

    private final SanPhamVeService sanPhamVeService;
    private final TicketPricingService ticketPricingService;
    private final TicketBookingService ticketBookingService;

    public TicketController(SanPhamVeService sanPhamVeService,
                            TicketPricingService ticketPricingService,
                            TicketBookingService ticketBookingService) {
        this.sanPhamVeService = sanPhamVeService;
        this.ticketPricingService = ticketPricingService;
        this.ticketBookingService = ticketBookingService;
    }

    @GetMapping("")
    public String danhSachVe(Model moHinh,
                             @RequestParam(value = "type", required = false) LoaiVeType loai,
                             @RequestParam(value = "keyword", required = false) String tuKhoa) {
        List<LoaiVe> danhSachVe;
        if (loai != null) {
            danhSachVe = sanPhamVeService.layVeTheoPhanLoai(loai);
            moHinh.addAttribute("selectedType", loai);
        } else if (tuKhoa != null && !tuKhoa.trim().isEmpty()) {
            danhSachVe = sanPhamVeService.timKiemVeDangBan(tuKhoa.trim());
            moHinh.addAttribute("keyword", tuKhoa.trim());
        } else {
            danhSachVe = sanPhamVeService.layDanhSachVeDangBan();
        }

        moHinh.addAttribute("tickets", danhSachVe);
        moHinh.addAttribute("loaiVeTypes", LoaiVeType.values());
        return "ticket/customer/list";
    }

    @GetMapping("/{id}")
    public String chiTietVe(@PathVariable("id") Long id, Model moHinh) {
        LoaiVe loaiVe = sanPhamVeService.layVeTheoId(id);
        if (loaiVe == null) {
            return "redirect:/tickets";
        }

        List<ComboThanhPhan> danhSachThanhPhan = sanPhamVeService.layThanhPhanCombo(loaiVe.getMaLoaiVe());
        moHinh.addAttribute("ticket", loaiVe);
        moHinh.addAttribute("comboItems", danhSachThanhPhan);
        moHinh.addAttribute("todayPrice", ticketPricingService.tinhGiaVe(loaiVe.getMaLoaiVe(), LocalDate.now()));
        return "ticket/customer/detail";
    }

    @GetMapping("/quote")
    @ResponseBody
    public TicketQuoteResponse layBaoGiaVe(@RequestParam("maLoaiVe") String maLoaiVe,
                                          @RequestParam(value = "date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate ngayThamQuan,
                                          @RequestParam(value = "quantity", defaultValue = "1") int soLuong) {
        TicketQuoteRequest yeuCauBaoGia = new TicketQuoteRequest();
        yeuCauBaoGia.setMaLoaiVe(maLoaiVe);
        yeuCauBaoGia.setNgayThamQuan(ngayThamQuan != null ? ngayThamQuan : LocalDate.now());
        yeuCauBaoGia.setSoLuong(soLuong);
        return ticketBookingService.layBaoGiaVe(yeuCauBaoGia);
    }
}
