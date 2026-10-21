package com.example.skygatehub.service.ticket.impl;

import com.example.skygatehub.entity.ticket.BangGiaVe;
import com.example.skygatehub.entity.ticket.LoaiVe;
import com.example.skygatehub.repository.ticket.BangGiaVeRepository;
import com.example.skygatehub.repository.ticket.LoaiVeRepository;
import com.example.skygatehub.repository.ticket.NgayDacBietRepository;
import com.example.skygatehub.service.ticket.TicketPricingService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class TicketPricingServiceImpl implements TicketPricingService {

    private final BangGiaVeRepository bangGiaVeRepository;
    private final NgayDacBietRepository ngayDacBietRepository;
    private final LoaiVeRepository loaiVeRepository;

    public TicketPricingServiceImpl(BangGiaVeRepository bangGiaVeRepository,
                                    NgayDacBietRepository ngayDacBietRepository,
                                    LoaiVeRepository loaiVeRepository) {
        this.bangGiaVeRepository = bangGiaVeRepository;
        this.ngayDacBietRepository = ngayDacBietRepository;
        this.loaiVeRepository = loaiVeRepository;
    }

    @Override
    public BigDecimal tinhGiaVe(String maLoaiVe, LocalDate ngayThamQuan) {
        if (ngayThamQuan == null) {
            ngayThamQuan = LocalDate.now();
        }

        String dieuKienNgay = xacDinhDieuKienNgay(ngayThamQuan);
        List<BangGiaVe> danhSachGiaHopLe = bangGiaVeRepository.timBangGiaHopLe(maLoaiVe, ngayThamQuan);

        for (BangGiaVe bangGia : danhSachGiaHopLe) {
            if (dieuKienNgay.equalsIgnoreCase(bangGia.getDieuKienGia())) {
                return bangGia.getDonGia();
            }
        }

        for (BangGiaVe bangGia : danhSachGiaHopLe) {
            if ("MAC_DINH".equalsIgnoreCase(bangGia.getDieuKienGia())) {
                return bangGia.getDonGia();
            }
        }

        if (!danhSachGiaHopLe.isEmpty()) {
            return danhSachGiaHopLe.get(0).getDonGia();
        }

        Optional<LoaiVe> timThayLoaiVe = loaiVeRepository.findByMaLoaiVe(maLoaiVe);
        if (timThayLoaiVe.isPresent() && timThayLoaiVe.get().getGiaVe() != null) {
            return timThayLoaiVe.get().getGiaVe();
        }

        return BigDecimal.ZERO;
    }

    @Override
    public String xacDinhDieuKienNgay(LocalDate ngay) {
        if (ngayDacBietRepository.existsByNgayApDungAndTrangThai(ngay, true)) {
            return "NGAY_LE";
        }
        DayOfWeek thuTrongTuan = ngay.getDayOfWeek();
        if (thuTrongTuan == DayOfWeek.SATURDAY || thuTrongTuan == DayOfWeek.SUNDAY) {
            return "CUOI_TUAN";
        }
        return "MAC_DINH";
    }
}
