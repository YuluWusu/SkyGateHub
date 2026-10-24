package com.example.skygatehub.service.dining.impl;

import com.example.skygatehub.dto.dining.DichVuAnRequest;
import com.example.skygatehub.entity.dining.CaAn;
import com.example.skygatehub.entity.dining.DichVuAnUong;
import com.example.skygatehub.entity.dining.DichVuCaAn;
import com.example.skygatehub.enums.dining.LoaiDichVuAn;
import com.example.skygatehub.repository.dining.CaAnRepository;
import com.example.skygatehub.repository.dining.DichVuAnUongRepository;
import com.example.skygatehub.repository.dining.DichVuCaAnRepository;
import com.example.skygatehub.service.dining.DichVuAnService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DichVuAnServiceImpl implements DichVuAnService {

    private final DichVuAnUongRepository dichVuAnUongRepository;
    private final DichVuCaAnRepository dichVuCaAnRepository;
    private final CaAnRepository caAnRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<DichVuAnUong> search(String keyword, String loai, String maCaAn, Boolean trangThai, Pageable pageable) {
        return dichVuAnUongRepository.searchDichVu(clean(keyword), clean(loai), clean(maCaAn), trangThai, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public DichVuAnUong getById(Long id) {
        return dichVuAnUongRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy dịch vụ ăn uống với ID: " + id));
    }

    @Override
    public DichVuAnUong create(DichVuAnRequest request) {
        String ma = request.getMaDichVu().trim().toUpperCase();
        if (dichVuAnUongRepository.existsByMaDichVu(ma)) {
            throw new IllegalArgumentException("Mã dịch vụ đã tồn tại: " + ma);
        }
        String loai = validateLoai(request.getLoaiDichVu());

        DichVuAnUong dv = new DichVuAnUong();
        dv.setMaDichVu(ma);
        applyFields(dv, request, loai);

        DichVuAnUong saved = dichVuAnUongRepository.save(dv);
        syncCaAn(saved, request.getMaCaAnList());
        log.info("Tạo mới dịch vụ ăn uống {}: {}", ma, dv.getTenDichVu());
        return saved;
    }

    @Override
    public DichVuAnUong update(Long id, DichVuAnRequest request) {
        DichVuAnUong dv = getById(id);
        String loai = validateLoai(request.getLoaiDichVu());

        // Không đổi mã dịch vụ vì mã được tham chiếu qua khóa ngoại
        applyFields(dv, request, loai);

        DichVuAnUong saved = dichVuAnUongRepository.save(dv);
        syncCaAn(saved, request.getMaCaAnList());
        log.info("Cập nhật dịch vụ ăn uống ID {}: {}", id, dv.getTenDichVu());
        return saved;
    }

    @Override
    public void toggleTrangThai(Long id) {
        DichVuAnUong dv = getById(id);
        dv.setTrangThai(!Boolean.TRUE.equals(dv.getTrangThai()));
        dichVuAnUongRepository.save(dv);
        log.info("Đổi trạng thái dịch vụ {} thành {}", dv.getMaDichVu(), dv.getTrangThai());
    }

    @Override
    public void delete(Long id) {
        DichVuAnUong dv = getById(id);
        String ma = dv.getMaDichVu();

        long chiTietDon = dichVuAnUongRepository.countChiTietDonByMaDichVu(ma);
        long thanhPhan = dichVuAnUongRepository.countThanhPhanDichVuByMaDichVu(ma);
        long datBan = dichVuAnUongRepository.countDatBanByMaDichVu(ma);
        long total = chiTietDon + thanhPhan + datBan;
        if (total > 0) {
            throw new IllegalStateException("Không thể xóa dịch vụ [" + dv.getTenDichVu() + "] vì đang được tham chiếu "
                    + total + " lần (chi tiết đơn: " + chiTietDon + ", thành phần gói: " + thanhPhan
                    + ", đặt bàn: " + datBan + "). Hãy dùng \"Ngừng kinh doanh\" thay thế.");
        }

        try {
            dichVuCaAnRepository.deleteAll(dichVuCaAnRepository.findByDichVuAnUong_MaDichVu(ma));
            dichVuAnUongRepository.delete(dv);
            dichVuAnUongRepository.flush();
            log.info("Đã xóa dịch vụ ăn uống ID {}: {}", id, ma);
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalStateException("Không thể xóa dịch vụ do vi phạm ràng buộc dữ liệu khóa ngoại.");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getMaCaAnDangPhucVu(String maDichVu) {
        return dichVuCaAnRepository.findByDichVuAnUong_MaDichVu(maDichVu).stream()
                .filter(l -> Boolean.TRUE.equals(l.getTrangThai()))
                .map(l -> l.getCaAn().getMaCaAn())
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, List<String>> getTenCaAnTheoDichVu(Collection<String> maDichVuList) {
        Map<String, List<String>> result = new LinkedHashMap<>();
        if (maDichVuList == null || maDichVuList.isEmpty()) {
            return result;
        }
        for (DichVuCaAn l : dichVuCaAnRepository.findByDichVuAnUong_MaDichVuInAndTrangThaiTrue(maDichVuList)) {
            result.computeIfAbsent(l.getDichVuAnUong().getMaDichVu(), k -> new ArrayList<>())
                    .add(l.getCaAn().getTenCaAn());
        }
        return result;
    }

    private void applyFields(DichVuAnUong dv, DichVuAnRequest request, String loai) {
        dv.setTenDichVu(request.getTenDichVu().trim());
        dv.setLoaiDichVu(loai);
        String moTa = request.getMoTa();
        dv.setMoTa(moTa != null && !moTa.trim().isEmpty() ? moTa.trim() : null);
        dv.setGiaDichVu(request.getGiaDichVu());
        dv.setTrangThai(request.getTrangThai() != null ? request.getTrangThai() : true);
    }

    private String validateLoai(String loai) {
        String value = loai == null ? "" : loai.trim();
        for (LoaiDichVuAn l : LoaiDichVuAn.values()) {
            if (l.name().equals(value)) {
                return value;
            }
        }
        throw new IllegalArgumentException("Loại dịch vụ không hợp lệ: " + loai);
    }

    // Link không còn được chọn chỉ tắt, không xóa, vì DAT_BAN có khóa ngoại tới cặp dịch vụ-ca
    private void syncCaAn(DichVuAnUong dv, List<String> maCaAnList) {
        Set<String> wanted = new LinkedHashSet<>();
        if (maCaAnList != null) {
            for (String ma : maCaAnList) {
                if (ma != null && !ma.trim().isEmpty()) {
                    wanted.add(ma.trim());
                }
            }
        }

        List<DichVuCaAn> existing = dichVuCaAnRepository.findByDichVuAnUong_MaDichVu(dv.getMaDichVu());

        for (String maCaAn : wanted) {
            CaAn ca = caAnRepository.findByMaCaAn(maCaAn)
                    .orElseThrow(() -> new IllegalArgumentException("Ca ăn không tồn tại: " + maCaAn));
            DichVuCaAn link = existing.stream()
                    .filter(l -> l.getCaAn().getMaCaAn().equals(maCaAn))
                    .findFirst().orElse(null);
            if (link == null) {
                link = new DichVuCaAn();
                link.setDichVuAnUong(dv);
                link.setCaAn(ca);
            }
            link.setTrangThai(true);
            dichVuCaAnRepository.save(link);
        }

        for (DichVuCaAn link : existing) {
            if (!wanted.contains(link.getCaAn().getMaCaAn())) {
                link.setTrangThai(false);
                dichVuCaAnRepository.save(link);
            }
        }
    }

    private String clean(String s) {
        return (s != null && !s.trim().isEmpty()) ? s.trim() : null;
    }
}
