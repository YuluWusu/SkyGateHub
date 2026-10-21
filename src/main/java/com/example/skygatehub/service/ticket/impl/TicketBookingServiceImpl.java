package com.example.skygatehub.service.ticket.impl;

import com.example.skygatehub.dto.ticket.TicketQuoteRequest;
import com.example.skygatehub.dto.ticket.TicketQuoteResponse;
import com.example.skygatehub.entity.ticket.KhoVe;
import com.example.skygatehub.entity.ticket.LoaiVe;
import com.example.skygatehub.repository.ticket.KhoVeRepository;
import com.example.skygatehub.repository.ticket.LoaiVeRepository;
import com.example.skygatehub.service.ticket.TicketBookingService;
import com.example.skygatehub.service.ticket.TicketPricingService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class TicketBookingServiceImpl implements TicketBookingService {

    private final KhoVeRepository khoVeRepository;
    private final LoaiVeRepository loaiVeRepository;
    private final TicketPricingService ticketPricingService;

    public TicketBookingServiceImpl(KhoVeRepository khoVeRepository,
                                    LoaiVeRepository loaiVeRepository,
                                    TicketPricingService ticketPricingService) {
        this.khoVeRepository = khoVeRepository;
        this.loaiVeRepository = loaiVeRepository;
        this.ticketPricingService = ticketPricingService;
    }

    @Override
    @Transactional(readOnly = true)
    public TicketQuoteResponse layBaoGiaVe(TicketQuoteRequest yeuCau) {
        if (yeuCau == null || yeuCau.getMaLoaiVe() == null) {
            return null;
        }

        Optional<LoaiVe> timThayLoaiVe = loaiVeRepository.findByMaLoaiVe(yeuCau.getMaLoaiVe());
        if (!timThayLoaiVe.isPresent()) {
            return null;
        }
        LoaiVe loaiVe = timThayLoaiVe.get();

        LocalDate ngay = yeuCau.getNgayThamQuan() != null ? yeuCau.getNgayThamQuan() : LocalDate.now();
        int soLuong = (yeuCau.getSoLuong() != null && yeuCau.getSoLuong() > 0) ? yeuCau.getSoLuong() : 1;

        BigDecimal donGia = ticketPricingService.tinhGiaVe(loaiVe.getMaLoaiVe(), ngay);
        BigDecimal tongTien = donGia.multiply(BigDecimal.valueOf(soLuong));

        Optional<KhoVe> timThayKhoVe = khoVeRepository.findByLoaiVe_MaLoaiVeAndNgayThamQuan(loaiVe.getMaLoaiVe(), ngay);
        boolean conHang = true;
        int conLai = 500;

        if (timThayKhoVe.isPresent()) {
            KhoVe khoVe = timThayKhoVe.get();
            if (khoVe.getSoLuongToiDa() != null) {
                conLai = Math.max(0, khoVe.getSoLuongToiDa() - khoVe.getSoLuongDaBan());
                conHang = conLai >= soLuong;
            }
        }

        String dieuKienNgay = ticketPricingService.xacDinhDieuKienNgay(ngay);

        return new TicketQuoteResponse(
                loaiVe.getMaLoaiVe(),
                loaiVe.getTenLoaiVe(),
                ngay,
                soLuong,
                donGia,
                tongTien,
                conHang,
                conLai,
                dieuKienNgay
        );
    }

    @Override
    public boolean kiemTraVaGiuCho(String maLoaiVe, LocalDate ngayThamQuan, int soLuong) {
        if (maLoaiVe == null || ngayThamQuan == null || soLuong <= 0) {
            return false;
        }

        Optional<LoaiVe> timThayLoaiVe = loaiVeRepository.findByMaLoaiVe(maLoaiVe);
        if (!timThayLoaiVe.isPresent()) {
            return false;
        }
        LoaiVe loaiVe = timThayLoaiVe.get();

        Optional<KhoVe> timThayKhoVe = khoVeRepository.timTheoLoaiVeVaNgayKhoaBiQuan(maLoaiVe, ngayThamQuan);
        KhoVe khoVe;

        if (timThayKhoVe.isPresent()) {
            khoVe = timThayKhoVe.get();
            if (khoVe.getSoLuongToiDa() != null) {
                int sucChuaConLai = khoVe.getSoLuongToiDa() - khoVe.getSoLuongDaBan();
                if (sucChuaConLai < soLuong) {
                    return false;
                }
            }
            khoVe.setSoLuongDaBan(khoVe.getSoLuongDaBan() + soLuong);
        } else {
            khoVe = new KhoVe();
            khoVe.setMaKhoVe("KV" + UUID.randomUUID().toString().substring(0, 6).toUpperCase());
            khoVe.setLoaiVe(loaiVe);
            khoVe.setNgayThamQuan(ngayThamQuan);
            khoVe.setSoLuongToiDa(500);
            khoVe.setSoLuongDaBan(soLuong);
        }

        khoVeRepository.save(khoVe);
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public List<KhoVe> layTonKhoTheoNgay(LocalDate ngayThamQuan) {
        return khoVeRepository.findByNgayThamQuan(ngayThamQuan);
    }

    @Override
    public KhoVe capNhatHanMucKho(String maLoaiVe, LocalDate ngayThamQuan, Integer hanMucToiDa) {
        Optional<LoaiVe> timThayLoaiVe = loaiVeRepository.findByMaLoaiVe(maLoaiVe);
        if (!timThayLoaiVe.isPresent()) {
            throw new IllegalArgumentException("Không tìm thấy loại vé: " + maLoaiVe);
        }
        LoaiVe loaiVe = timThayLoaiVe.get();

        Optional<KhoVe> timThayKhoVe = khoVeRepository.findByLoaiVeAndNgayWithLock(maLoaiVe, ngayThamQuan);
        KhoVe khoVe;
        if (timThayKhoVe.isPresent()) {
            khoVe = timThayKhoVe.get();
            khoVe.setSoLuongToiDa(hanMucToiDa);
        } else {
            khoVe = new KhoVe();
            khoVe.setMaKhoVe("KV" + UUID.randomUUID().toString().substring(0, 6).toUpperCase());
            khoVe.setLoaiVe(loaiVe);
            khoVe.setNgayThamQuan(ngayThamQuan);
            khoVe.setSoLuongToiDa(hanMucToiDa);
            khoVe.setSoLuongDaBan(0);
        }
        return khoVeRepository.save(khoVe);
    }

    @Override
    @Transactional(readOnly = true)
    public List<KhoVe> layTonKhoTheoVe(String maLoaiVe) {
        return khoVeRepository.findByLoaiVe_MaLoaiVe(maLoaiVe);
    }
}
