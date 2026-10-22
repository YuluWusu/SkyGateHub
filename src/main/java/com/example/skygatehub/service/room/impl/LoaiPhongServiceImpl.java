package com.example.skygatehub.service.room.impl;

import com.example.skygatehub.dto.room.KhuLuuTruDto;
import com.example.skygatehub.dto.room.LoaiPhongRequest;
import com.example.skygatehub.entity.room.KhuLuuTru;
import com.example.skygatehub.entity.room.LoaiPhong;
import com.example.skygatehub.repository.room.KhuLuuTruRepository;
import com.example.skygatehub.repository.room.LoaiPhongRepository;
import com.example.skygatehub.service.room.LoaiPhongService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class LoaiPhongServiceImpl implements LoaiPhongService {

    private final KhuLuuTruRepository khuLuuTruRepository;
    private final LoaiPhongRepository loaiPhongRepository;

    // ==================== KHU LƯU TRÚ ====================

    @Override
    @Transactional(readOnly = true)
    public List<KhuLuuTru> getAllKhuLuuTru() {
        return khuLuuTruRepository.findAllByOrderByMaKhuAsc();
    }

    @Override
    @Transactional(readOnly = true)
    public KhuLuuTru getKhuLuuTruById(Integer maKhu) {
        return khuLuuTruRepository.findById(maKhu)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy khu lưu trú với mã: " + maKhu));
    }

    @Override
    public KhuLuuTru createKhuLuuTru(KhuLuuTruDto dto) {
        if (dto.getTenKhu() == null || dto.getTenKhu().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên khu lưu trú không được để trống");
        }
        String tenKhu = dto.getTenKhu().trim();
        if (khuLuuTruRepository.existsByTenKhu(tenKhu)) {
            throw new IllegalArgumentException("Tên khu lưu trú đã tồn tại: " + tenKhu);
        }

        KhuLuuTru klt = new KhuLuuTru();
        klt.setTenKhu(tenKhu);
        klt.setHangSao(dto.getHangSao() != null ? dto.getHangSao() : 5);
        klt.setDiaChi(dto.getDiaChi() != null ? dto.getDiaChi().trim() : null);
        klt.setEmail(dto.getEmail() != null ? dto.getEmail().trim() : null);
        klt.setHotline(dto.getHotline() != null ? dto.getHotline().trim() : null);
        klt.setHinhAnh(dto.getHinhAnh() != null ? dto.getHinhAnh().trim() : null);

        log.info("Tạo mới khu lưu trú: {}", tenKhu);
        return khuLuuTruRepository.save(klt);
    }

    @Override
    public KhuLuuTru updateKhuLuuTru(Integer maKhu, KhuLuuTruDto dto) {
        KhuLuuTru klt = getKhuLuuTruById(maKhu);
        String tenKhu = dto.getTenKhu().trim();

        if (khuLuuTruRepository.existsByTenKhuAndMaKhuNot(tenKhu, maKhu)) {
            throw new IllegalArgumentException("Tên khu lưu trú đã được dùng cho khu khác: " + tenKhu);
        }

        klt.setTenKhu(tenKhu);
        klt.setHangSao(dto.getHangSao() != null ? dto.getHangSao() : 5);
        klt.setDiaChi(dto.getDiaChi() != null ? dto.getDiaChi().trim() : null);
        klt.setEmail(dto.getEmail() != null ? dto.getEmail().trim() : null);
        klt.setHotline(dto.getHotline() != null ? dto.getHotline().trim() : null);
        if (dto.getHinhAnh() != null && !dto.getHinhAnh().trim().isEmpty()) {
            klt.setHinhAnh(dto.getHinhAnh().trim());
        }

        log.info("Cập nhật khu lưu trú ID {}: {}", maKhu, tenKhu);
        return khuLuuTruRepository.save(klt);
    }

    @Override
    public void deleteKhuLuuTru(Integer maKhu) {
        KhuLuuTru klt = getKhuLuuTruById(maKhu);

        long loaiPhongCount = khuLuuTruRepository.countLoaiPhongByMaKhu(maKhu);
        if (loaiPhongCount > 0) {
            throw new IllegalStateException("Không thể xóa khu lưu trú [" + klt.getTenKhu()
                    + "] vì đang có " + loaiPhongCount + " loại phòng trực thuộc!");
        }

        try {
            khuLuuTruRepository.delete(klt);
            log.info("Đã xóa khu lưu trú ID {}: {}", maKhu, klt.getTenKhu());
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalStateException("Không thể xóa khu lưu trú do có dữ liệu liên kết ràng buộc.");
        }
    }

    // ==================== LOẠI PHÒNG ====================

    @Override
    @Transactional(readOnly = true)
    public List<LoaiPhong> getAllLoaiPhong() {
        return loaiPhongRepository.findAllByOrderByMaLoaiPhongAsc();
    }

    @Override
    @Transactional(readOnly = true)
    public List<LoaiPhong> getActiveLoaiPhong() {
        return loaiPhongRepository.findByTrangThai(true);
    }

    @Override
    @Transactional(readOnly = true)
    public LoaiPhong getLoaiPhongById(Long id) {
        return loaiPhongRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy loại phòng với ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public LoaiPhong getLoaiPhongByMa(String maLoaiPhong) {
        return loaiPhongRepository.findByMaLoaiPhong(maLoaiPhong)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy loại phòng với mã: " + maLoaiPhong));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<LoaiPhong> searchLoaiPhong(Integer maKhu, Boolean trangThai, String keyword, Pageable pageable) {
        String cleanKeyword = (keyword != null && !keyword.trim().isEmpty()) ? keyword.trim() : null;
        return loaiPhongRepository.searchLoaiPhong(maKhu, trangThai, cleanKeyword, pageable);
    }

    @Override
    public LoaiPhong createLoaiPhong(LoaiPhongRequest request) {
        String maLoaiPhong = request.getMaLoaiPhong().trim().toUpperCase();
        if (loaiPhongRepository.existsByMaLoaiPhong(maLoaiPhong)) {
            throw new IllegalArgumentException("Mã loại phòng đã tồn tại: " + maLoaiPhong);
        }

        String tenLoaiPhong = request.getTenLoaiPhong().trim();
        if (loaiPhongRepository.existsByTenLoaiPhong(tenLoaiPhong)) {
            throw new IllegalArgumentException("Tên loại phòng đã tồn tại: " + tenLoaiPhong);
        }

        KhuLuuTru klt = getKhuLuuTruById(request.getMaKhu());

        LoaiPhong lp = new LoaiPhong();
        lp.setMaLoaiPhong(maLoaiPhong);
        lp.setKhuLuuTru(klt);
        lp.setTenLoaiPhong(tenLoaiPhong);
        lp.setDienTich(request.getDienTich());
        lp.setLoaiGiuong(request.getLoaiGiuong() != null ? request.getLoaiGiuong().trim() : "DBL");
        lp.setSucChuaNguoiLon(request.getSucChuaNguoiLon());
        lp.setSucChuaTreEm(request.getSucChuaTreEm() != null ? request.getSucChuaTreEm() : 1);
        lp.setGiaCoBan(request.getGiaCoBan());
        lp.setHinhAnh(request.getHinhAnh() != null ? request.getHinhAnh().trim() : null);
        lp.setMoTa(request.getMoTa() != null ? request.getMoTa().trim() : null);
        lp.setTrangThai(request.getTrangThai() != null ? request.getTrangThai() : true);
        lp.setNgayTao(LocalDateTime.now());

        log.info("Tạo mới loại phòng mã {}: {}", maLoaiPhong, tenLoaiPhong);
        return loaiPhongRepository.save(lp);
    }

    @Override
    public LoaiPhong updateLoaiPhong(Long id, LoaiPhongRequest request) {
        LoaiPhong lp = getLoaiPhongById(id);

        String tenLoaiPhong = request.getTenLoaiPhong().trim();
        if (loaiPhongRepository.existsByTenLoaiPhongAndMaLoaiPhongNot(tenLoaiPhong, lp.getMaLoaiPhong())) {
            throw new IllegalArgumentException("Tên loại phòng đã được đặt cho loại phòng khác: " + tenLoaiPhong);
        }

        KhuLuuTru klt = getKhuLuuTruById(request.getMaKhu());

        lp.setKhuLuuTru(klt);
        lp.setTenLoaiPhong(tenLoaiPhong);
        lp.setDienTich(request.getDienTich());
        lp.setLoaiGiuong(request.getLoaiGiuong() != null ? request.getLoaiGiuong().trim() : "DBL");
        lp.setSucChuaNguoiLon(request.getSucChuaNguoiLon());
        lp.setSucChuaTreEm(request.getSucChuaTreEm() != null ? request.getSucChuaTreEm() : 1);
        lp.setGiaCoBan(request.getGiaCoBan());
        if (request.getHinhAnh() != null && !request.getHinhAnh().trim().isEmpty()) {
            lp.setHinhAnh(request.getHinhAnh().trim());
        }
        lp.setMoTa(request.getMoTa() != null ? request.getMoTa().trim() : null);
        if (request.getTrangThai() != null) {
            lp.setTrangThai(request.getTrangThai());
        }

        log.info("Cập nhật loại phòng ID {}: {}", id, tenLoaiPhong);
        return loaiPhongRepository.save(lp);
    }

    @Override
    public void toggleLoaiPhongStatus(Long id) {
        LoaiPhong lp = getLoaiPhongById(id);
        boolean current = Boolean.TRUE.equals(lp.getTrangThai());
        lp.setTrangThai(!current);
        loaiPhongRepository.save(lp);
        log.info("Đổi trạng thái loại phòng ID {} -> {}", id, !current);
    }

    @Override
    public void deleteLoaiPhong(Long id) {
        LoaiPhong lp = getLoaiPhongById(id);

        long phongCount = loaiPhongRepository.countPhongByMaLoaiPhong(lp.getMaLoaiPhong());
        if (phongCount > 0) {
            throw new IllegalStateException("Không thể xóa loại phòng [" + lp.getTenLoaiPhong()
                    + "] vì đang có " + phongCount + " phòng vật lý liên kết!");
        }

        long datPhongCount = loaiPhongRepository.countDatPhongByMaLoaiPhong(lp.getMaLoaiPhong());
        if (datPhongCount > 0) {
            throw new IllegalStateException("Không thể xóa loại phòng [" + lp.getTenLoaiPhong()
                    + "] vì đang có " + datPhongCount + " đơn đặt phòng liên kết!");
        }

        try {
            loaiPhongRepository.delete(lp);
            log.info("Đã xóa loại phòng ID {}: {}", id, lp.getTenLoaiPhong());
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalStateException("Không thể xóa loại phòng do vi phạm ràng buộc dữ liệu khóa ngoại.");
        }
    }
}
