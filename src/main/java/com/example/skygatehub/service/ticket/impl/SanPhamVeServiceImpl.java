package com.example.skygatehub.service.ticket.impl;

import com.example.skygatehub.common.enums.LoaiVeType;
import com.example.skygatehub.dto.ticket.ComboItemRequest;
import com.example.skygatehub.dto.ticket.SanPhamVeRequest;
import com.example.skygatehub.entity.ticket.ComboThanhPhan;
import com.example.skygatehub.entity.ticket.LoaiVe;
import com.example.skygatehub.entity.ticket.ThanhPhanDichVu;
import com.example.skygatehub.repository.ticket.ComboThanhPhanRepository;
import com.example.skygatehub.repository.ticket.LoaiVeRepository;
import com.example.skygatehub.repository.ticket.ThanhPhanDichVuRepository;
import com.example.skygatehub.service.ticket.SanPhamVeService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class SanPhamVeServiceImpl implements SanPhamVeService {

    private final LoaiVeRepository loaiVeRepository;
    private final ComboThanhPhanRepository comboThanhPhanRepository;
    private final ThanhPhanDichVuRepository thanhPhanDichVuRepository;

    public SanPhamVeServiceImpl(LoaiVeRepository loaiVeRepository,
                                ComboThanhPhanRepository comboThanhPhanRepository,
                                ThanhPhanDichVuRepository thanhPhanDichVuRepository) {
        this.loaiVeRepository = loaiVeRepository;
        this.comboThanhPhanRepository = comboThanhPhanRepository;
        this.thanhPhanDichVuRepository = thanhPhanDichVuRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<LoaiVe> layTatCaLoaiVe(Pageable phanTrang) {
        return loaiVeRepository.findAll(phanTrang);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<LoaiVe> timKiemLoaiVe(String tuKhoa, Pageable phanTrang) {
        if (tuKhoa == null || tuKhoa.trim().isEmpty()) {
            return layTatCaLoaiVe(phanTrang);
        }
        return loaiVeRepository.findByTenLoaiVeContainingIgnoreCase(tuKhoa.trim(), phanTrang);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LoaiVe> layDanhSachVeDangBan() {
        return loaiVeRepository.findByTrangThai(true);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LoaiVe> timKiemVeDangBan(String tuKhoa) {
        if (tuKhoa == null || tuKhoa.trim().isEmpty()) {
            return layDanhSachVeDangBan();
        }
        List<LoaiVe> danhSachKhop = loaiVeRepository.findByTenLoaiVeContainingIgnoreCase(tuKhoa.trim());
        List<LoaiVe> danhSachHoatDong = new ArrayList<>();
        for (LoaiVe loaiVe : danhSachKhop) {
            if (Boolean.TRUE.equals(loaiVe.getTrangThai())) {
                danhSachHoatDong.add(loaiVe);
            }
        }
        return danhSachHoatDong;
    }

    @Override
    @Transactional(readOnly = true)
    public List<LoaiVe> layVeTheoPhanLoai(LoaiVeType loaiVe) {
        return loaiVeRepository.findByLoaiVeAndTrangThai(loaiVe, true);
    }

    @Override
    @Transactional(readOnly = true)
    public LoaiVe layVeTheoId(Long id) {
        Optional<LoaiVe> timThay = loaiVeRepository.findById(id);
        return timThay.orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public LoaiVe layVeTheoMa(String maLoaiVe) {
        Optional<LoaiVe> timThay = loaiVeRepository.findByMaLoaiVe(maLoaiVe);
        return timThay.orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public String generateNextMaLoaiVe() {
        List<String> codes = loaiVeRepository.findAllMaLoaiVe();
        int maxNumber = 0;
        for (String code : codes) {
            if (code != null && code.toUpperCase().startsWith("VE")) {
                try {
                    int num = Integer.parseInt(code.substring(2).trim());
                    if (num > maxNumber) {
                        maxNumber = num;
                    }
                } catch (NumberFormatException ignored) {}
            }
        }
        int nextNumber = maxNumber + 1;
        return String.format("VE%02d", nextNumber);
    }

    @Override
    public LoaiVe taoLoaiVe(SanPhamVeRequest yeuCau) {
        String maLoaiVe = yeuCau.getMaLoaiVe();
        if (maLoaiVe == null || maLoaiVe.trim().isEmpty() || maLoaiVe.startsWith("LV")) {
            maLoaiVe = generateNextMaLoaiVe();
        }

        LoaiVe loaiVe = new LoaiVe();
        loaiVe.setMaLoaiVe(maLoaiVe.trim());
        loaiVe.setTenLoaiVe(yeuCau.getTenLoaiVe().trim());
        loaiVe.setLoaiVe(yeuCau.getLoaiVe() != null ? yeuCau.getLoaiVe() : LoaiVeType.NGUOI_LON);
        loaiVe.setMoTa(yeuCau.getMoTa());
        loaiVe.setGiaVe(yeuCau.getGiaVe());
        loaiVe.setTrangThai(yeuCau.getTrangThai() != null ? yeuCau.getTrangThai() : true);
        loaiVe.setNgayTao(LocalDateTime.now());

        return loaiVeRepository.save(loaiVe);
    }

    @Override
    public LoaiVe capNhatLoaiVe(Long id, SanPhamVeRequest yeuCau) {
        Optional<LoaiVe> timThay = loaiVeRepository.findById(id);
        if (!timThay.isPresent()) {
            throw new IllegalArgumentException("Không tìm thấy loại vé với ID: " + id);
        }

        LoaiVe loaiVe = timThay.get();
        if (yeuCau.getTenLoaiVe() != null) loaiVe.setTenLoaiVe(yeuCau.getTenLoaiVe().trim());
        if (yeuCau.getLoaiVe() != null) loaiVe.setLoaiVe(yeuCau.getLoaiVe());
        if (yeuCau.getMoTa() != null) loaiVe.setMoTa(yeuCau.getMoTa());
        if (yeuCau.getGiaVe() != null) loaiVe.setGiaVe(yeuCau.getGiaVe());
        if (yeuCau.getTrangThai() != null) loaiVe.setTrangThai(yeuCau.getTrangThai());

        return loaiVeRepository.save(loaiVe);
    }

    @Override
    public void xoaLoaiVe(Long id) {
        Optional<LoaiVe> timThay = loaiVeRepository.findById(id);
        if (timThay.isPresent()) {
            LoaiVe loaiVe = timThay.get();
            comboThanhPhanRepository.deleteByLoaiVe_MaLoaiVe(loaiVe.getMaLoaiVe());
            loaiVeRepository.delete(loaiVe);
        }
    }

    @Override
    public void chuyenTrangThaiVe(Long id) {
        Optional<LoaiVe> timThay = loaiVeRepository.findById(id);
        if (timThay.isPresent()) {
            LoaiVe loaiVe = timThay.get();
            loaiVe.setTrangThai(!Boolean.TRUE.equals(loaiVe.getTrangThai()));
            loaiVeRepository.save(loaiVe);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ComboThanhPhan> layThanhPhanCombo(String maLoaiVe) {
        return comboThanhPhanRepository.findByLoaiVe_MaLoaiVe(maLoaiVe);
    }

    @Override
    public void themThanhPhanCombo(String maLoaiVe, ComboItemRequest yeuCau) {
        Optional<LoaiVe> timThayVe = loaiVeRepository.findByMaLoaiVe(maLoaiVe);
        if (!timThayVe.isPresent()) {
            throw new IllegalArgumentException("Không tìm thấy loại vé: " + maLoaiVe);
        }
        Optional<ThanhPhanDichVu> timThayThanhPhan = thanhPhanDichVuRepository.findByMaThanhPhan(yeuCau.getMaThanhPhan());
        if (!timThayThanhPhan.isPresent()) {
            throw new IllegalArgumentException("Không tìm thấy thành phần dịch vụ: " + yeuCau.getMaThanhPhan());
        }

        LoaiVe loaiVe = timThayVe.get();
        ThanhPhanDichVu thanhPhan = timThayThanhPhan.get();

        Optional<ComboThanhPhan> tonTai = comboThanhPhanRepository.findByLoaiVe_MaLoaiVeAndThanhPhanDichVu_MaThanhPhan(maLoaiVe, yeuCau.getMaThanhPhan());
        if (tonTai.isPresent()) {
            ComboThanhPhan phanTu = tonTai.get();
            phanTu.setSoLuongMoiCombo(yeuCau.getSoLuongMoiCombo());
            phanTu.setTheoDoiSuDung(yeuCau.getTheoDoiSuDung());
            phanTu.setGhiChu(yeuCau.getGhiChu());
            comboThanhPhanRepository.save(phanTu);
        } else {
            ComboThanhPhan phanTu = new ComboThanhPhan();
            phanTu.setLoaiVe(loaiVe);
            phanTu.setThanhPhanDichVu(thanhPhan);
            phanTu.setSoLuongMoiCombo(yeuCau.getSoLuongMoiCombo() != null ? yeuCau.getSoLuongMoiCombo() : 1);
            phanTu.setTheoDoiSuDung(yeuCau.getTheoDoiSuDung() != null ? yeuCau.getTheoDoiSuDung() : true);
            phanTu.setGhiChu(yeuCau.getGhiChu());
            comboThanhPhanRepository.save(phanTu);
        }
    }

    @Override
    public void xoaThanhPhanCombo(Long idCombo) {
        comboThanhPhanRepository.deleteById(idCombo);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ThanhPhanDichVu> layTatCaDichVuThanhPhan() {
        return thanhPhanDichVuRepository.findByTrangThai(true);
    }
}
