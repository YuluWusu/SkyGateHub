package com.example.skygatehub.service.dining.impl;

import com.example.skygatehub.dto.dining.CaAnRequest;
import com.example.skygatehub.entity.dining.CaAn;
import com.example.skygatehub.repository.dining.CaAnRepository;
import com.example.skygatehub.service.dining.CaAnService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CaAnServiceImpl implements CaAnService {

    private final CaAnRepository caAnRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CaAn> getAll() {
        return caAnRepository.findAll().stream()
                .sorted(Comparator.comparing(CaAn::getGioBatDau, Comparator.nullsLast(Comparator.<LocalTime>naturalOrder())))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CaAn getById(Long id) {
        return caAnRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy ca ăn với ID: " + id));
    }

    @Override
    public CaAn create(CaAnRequest request) {
        String maCaAn = request.getMaCaAn().trim().toUpperCase();
        if (caAnRepository.existsByMaCaAn(maCaAn)) {
            throw new IllegalArgumentException("Mã ca ăn đã tồn tại: " + maCaAn);
        }
        validateGio(request.getGioBatDau(), request.getGioKetThuc());

        CaAn ca = new CaAn();
        ca.setMaCaAn(maCaAn);
        ca.setTenCaAn(request.getTenCaAn().trim());
        ca.setGioBatDau(request.getGioBatDau());
        ca.setGioKetThuc(request.getGioKetThuc());
        ca.setTrangThai(request.getTrangThai() != null ? request.getTrangThai() : true);

        log.info("Tạo mới ca ăn {}: {}", maCaAn, ca.getTenCaAn());
        return caAnRepository.save(ca);
    }

    @Override
    public CaAn update(Long id, CaAnRequest request) {
        CaAn ca = getById(id);
        validateGio(request.getGioBatDau(), request.getGioKetThuc());

        // Không đổi maCaAn vì được tham chiếu qua khóa ngoại
        ca.setTenCaAn(request.getTenCaAn().trim());
        ca.setGioBatDau(request.getGioBatDau());
        ca.setGioKetThuc(request.getGioKetThuc());
        ca.setTrangThai(request.getTrangThai() != null ? request.getTrangThai() : false);

        log.info("Cập nhật ca ăn ID {}: {}", id, ca.getTenCaAn());
        return caAnRepository.save(ca);
    }

    @Override
    public void toggleTrangThai(Long id) {
        CaAn ca = getById(id);
        ca.setTrangThai(!Boolean.TRUE.equals(ca.getTrangThai()));
        caAnRepository.save(ca);
        log.info("Đổi trạng thái ca ăn {} thành {}", ca.getMaCaAn(), ca.getTrangThai());
    }

    @Override
    public void delete(Long id) {
        CaAn ca = getById(id);

        long links = caAnRepository.countLinksByMaCaAn(ca.getMaCaAn());
        if (links > 0) {
            throw new IllegalStateException("Không thể xóa ca ăn [" + ca.getTenCaAn()
                    + "] vì đang gắn với " + links + " món. Hãy tắt ca ăn thay vì xóa.");
        }

        try {
            caAnRepository.delete(ca);
            log.info("Đã xóa ca ăn ID {}: {}", id, ca.getMaCaAn());
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalStateException("Không thể xóa ca ăn do vi phạm ràng buộc dữ liệu khóa ngoại.");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> countActiveDishesByCa() {
        Map<String, Long> result = new HashMap<>();
        for (Object[] row : caAnRepository.countActiveDishesGroupByCa()) {
            result.put((String) row[0], ((Number) row[1]).longValue());
        }
        return result;
    }

    private void validateGio(LocalTime batDau, LocalTime ketThuc) {
        if (batDau != null && ketThuc != null && !ketThuc.isAfter(batDau)) {
            throw new IllegalArgumentException("Giờ kết thúc phải sau giờ bắt đầu");
        }
    }
}
