package com.example.skygatehub.service.ticket.impl;

import com.example.skygatehub.dto.ticket.BangGiaVeRequest;
import com.example.skygatehub.entity.ticket.BangGiaVe;
import com.example.skygatehub.entity.ticket.LoaiVe;
import com.example.skygatehub.entity.ticket.NgayDacBiet;
import com.example.skygatehub.repository.ticket.BangGiaVeRepository;
import com.example.skygatehub.repository.ticket.LoaiVeRepository;
import com.example.skygatehub.repository.ticket.NgayDacBietRepository;
import com.example.skygatehub.service.ticket.BangGiaVeService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class BangGiaVeServiceImpl implements BangGiaVeService {

    private final BangGiaVeRepository bangGiaVeRepository;
    private final LoaiVeRepository loaiVeRepository;
    private final NgayDacBietRepository ngayDacBietRepository;

    public BangGiaVeServiceImpl(BangGiaVeRepository bangGiaVeRepository,
                                LoaiVeRepository loaiVeRepository,
                                NgayDacBietRepository ngayDacBietRepository) {
        this.bangGiaVeRepository = bangGiaVeRepository;
        this.loaiVeRepository = loaiVeRepository;
        this.ngayDacBietRepository = ngayDacBietRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BangGiaVe> layTatCaBangGia(Pageable phanTrang) {
        return bangGiaVeRepository.findAll(phanTrang);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BangGiaVe> timKiemBangGia(String tuKhoa, Pageable phanTrang) {
        if (tuKhoa == null || tuKhoa.trim().isEmpty()) {
            return layTatCaBangGia(phanTrang);
        }
        return bangGiaVeRepository.findByTenBangGiaContainingIgnoreCase(tuKhoa.trim(), phanTrang);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BangGiaVe> layBangGiaTheoVe(String maLoaiVe) {
        return bangGiaVeRepository.findByLoaiVe_MaLoaiVeOrderByUuTienDesc(maLoaiVe);
    }

    @Override
    @Transactional(readOnly = true)
    public BangGiaVe layBangGiaTheoId(Long id) {
        Optional<BangGiaVe> timThay = bangGiaVeRepository.findById(id);
        return timThay.orElse(null);
    }

    @Override
    public BangGiaVe taoBangGia(BangGiaVeRequest yeuCau) {
        Optional<LoaiVe> timThayLoaiVe = loaiVeRepository.findByMaLoaiVe(yeuCau.getMaLoaiVe());
        if (!timThayLoaiVe.isPresent()) {
            throw new IllegalArgumentException("Không tìm thấy loại vé: " + yeuCau.getMaLoaiVe());
        }
        LoaiVe loaiVe = timThayLoaiVe.get();

        String maBangGia = yeuCau.getMaBangGia();
        if (maBangGia == null || maBangGia.trim().isEmpty()) {
            maBangGia = "BG" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        }

        BangGiaVe bangGia = new BangGiaVe();
        bangGia.setMaBangGia(maBangGia.trim());
        bangGia.setLoaiVe(loaiVe);
        bangGia.setTenBangGia(yeuCau.getTenBangGia().trim());
        bangGia.setDieuKienGia(yeuCau.getDieuKienGia() != null ? yeuCau.getDieuKienGia() : "MAC_DINH");
        bangGia.setHieuLucTu(yeuCau.getHieuLucTu());
        bangGia.setHieuLucDen(yeuCau.getHieuLucDen());
        bangGia.setDonGia(yeuCau.getDonGia());
        bangGia.setUuTien(yeuCau.getUuTien() != null ? yeuCau.getUuTien() : 0);
        bangGia.setTrangThai(yeuCau.getTrangThai() != null ? yeuCau.getTrangThai() : true);
        bangGia.setGhiChu(yeuCau.getGhiChu());

        return bangGiaVeRepository.save(bangGia);
    }

    @Override
    public BangGiaVe capNhatBangGia(Long id, BangGiaVeRequest yeuCau) {
        Optional<BangGiaVe> timThay = bangGiaVeRepository.findById(id);
        if (!timThay.isPresent()) {
            throw new IllegalArgumentException("Không tìm thấy bảng giá ID: " + id);
        }

        BangGiaVe bangGia = timThay.get();
        if (yeuCau.getMaLoaiVe() != null) {
            Optional<LoaiVe> timThayLoaiVe = loaiVeRepository.findByMaLoaiVe(yeuCau.getMaLoaiVe());
            if (timThayLoaiVe.isPresent()) {
                bangGia.setLoaiVe(timThayLoaiVe.get());
            }
        }
        if (yeuCau.getTenBangGia() != null) bangGia.setTenBangGia(yeuCau.getTenBangGia().trim());
        if (yeuCau.getDieuKienGia() != null) bangGia.setDieuKienGia(yeuCau.getDieuKienGia());
        if (yeuCau.getHieuLucTu() != null) bangGia.setHieuLucTu(yeuCau.getHieuLucTu());
        bangGia.setHieuLucDen(yeuCau.getHieuLucDen());
        if (yeuCau.getDonGia() != null) bangGia.setDonGia(yeuCau.getDonGia());
        if (yeuCau.getUuTien() != null) bangGia.setUuTien(yeuCau.getUuTien());
        if (yeuCau.getTrangThai() != null) bangGia.setTrangThai(yeuCau.getTrangThai());
        if (yeuCau.getGhiChu() != null) bangGia.setGhiChu(yeuCau.getGhiChu());

        return bangGiaVeRepository.save(bangGia);
    }

    @Override
    public void xoaBangGia(Long id) {
        bangGiaVeRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NgayDacBiet> layTatCaNgayDacBiet() {
        return ngayDacBietRepository.findAll();
    }

    @Override
    public NgayDacBiet taoNgayDacBiet(NgayDacBiet ngayDacBiet) {
        if (ngayDacBiet.getMaNgayDacBiet() == null || ngayDacBiet.getMaNgayDacBiet().trim().isEmpty()) {
            ngayDacBiet.setMaNgayDacBiet("NDB" + UUID.randomUUID().toString().substring(0, 6).toUpperCase());
        }
        return ngayDacBietRepository.save(ngayDacBiet);
    }

    @Override
    public void xoaNgayDacBiet(Long id) {
        ngayDacBietRepository.deleteById(id);
    }
}
