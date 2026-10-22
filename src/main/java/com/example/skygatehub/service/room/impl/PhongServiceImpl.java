package com.example.skygatehub.service.room.impl;

import com.example.skygatehub.common.enums.TrangThaiPhanPhong;
import com.example.skygatehub.common.enums.TrangThaiPhong;
import com.example.skygatehub.dto.room.PhongRequest;
import com.example.skygatehub.entity.room.LoaiPhong;
import com.example.skygatehub.entity.room.PhanPhong;
import com.example.skygatehub.entity.room.Phong;
import com.example.skygatehub.repository.room.LoaiPhongRepository;
import com.example.skygatehub.repository.room.PhanPhongRepository;
import com.example.skygatehub.repository.room.PhongRepository;
import com.example.skygatehub.service.room.PhongService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PhongServiceImpl implements PhongService {

    private final PhongRepository phongRepository;
    private final LoaiPhongRepository loaiPhongRepository;
    private final PhanPhongRepository phanPhongRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Phong> getAllPhong() {
        return phongRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Phong getPhongById(Long id) {
        return phongRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy phòng với ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Phong getPhongByMa(String maPhong) {
        return phongRepository.findByMaPhong(maPhong)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy phòng với mã: " + maPhong));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Phong> searchPhong(Integer maKhu, String maLoaiPhong, TrangThaiPhong trangThai, String keyword, Pageable pageable) {
        String cleanKeyword = (keyword != null && !keyword.trim().isEmpty()) ? keyword.trim() : null;
        String cleanMaLoai = (maLoaiPhong != null && !maLoaiPhong.trim().isEmpty()) ? maLoaiPhong.trim() : null;
        return phongRepository.searchPhong(maKhu, cleanMaLoai, trangThai, cleanKeyword, pageable);
    }

    @Override
    public Phong createPhong(PhongRequest request) {
        String maPhong = request.getMaPhong().trim().toUpperCase();
        if (phongRepository.existsByMaPhong(maPhong)) {
            throw new IllegalArgumentException("Mã phòng đã tồn tại: " + maPhong);
        }

        String soPhong = request.getSoPhong().trim();
        if (phongRepository.existsBySoPhong(soPhong)) {
            throw new IllegalArgumentException("Số phòng/Tên phòng đã tồn tại: " + soPhong);
        }

        LoaiPhong lp = loaiPhongRepository.findByMaLoaiPhong(request.getMaLoaiPhong().trim())
                .orElseThrow(() -> new IllegalArgumentException("Loại phòng không tồn tại: " + request.getMaLoaiPhong()));

        Phong p = new Phong();
        p.setMaPhong(maPhong);
        p.setSoPhong(soPhong);
        p.setLoaiPhong(lp);
        p.setTrangThai(request.getTrangThai() != null ? request.getTrangThai() : TrangThaiPhong.SAN_SANG);

        log.info("Tạo mới phòng vật lý mã {}: {}", maPhong, soPhong);
        return phongRepository.save(p);
    }

    @Override
    public Phong updatePhong(Long id, PhongRequest request) {
        Phong p = getPhongById(id);

        String soPhong = request.getSoPhong().trim();
        if (phongRepository.existsBySoPhongAndMaPhongNot(soPhong, p.getMaPhong())) {
            throw new IllegalArgumentException("Số phòng/Tên phòng đã được đặt cho phòng khác: " + soPhong);
        }

        LoaiPhong lp = loaiPhongRepository.findByMaLoaiPhong(request.getMaLoaiPhong().trim())
                .orElseThrow(() -> new IllegalArgumentException("Loại phòng không tồn tại: " + request.getMaLoaiPhong()));

        p.setSoPhong(soPhong);
        p.setLoaiPhong(lp);
        if (request.getTrangThai() != null) {
            p.setTrangThai(request.getTrangThai());
        }

        log.info("Cập nhật thông tin phòng ID {}: {}", id, soPhong);
        return phongRepository.save(p);
    }

    @Override
    public void deletePhong(Long id) {
        Phong p = getPhongById(id);

        long phanPhongCount = phongRepository.countPhanPhongByMaPhong(p.getMaPhong());
        if (phanPhongCount > 0) {
            throw new IllegalStateException("Không thể xóa phòng [" + p.getSoPhong()
                    + "] vì phòng đã có " + phanPhongCount + " lượt phân phòng / lưu trú trong hệ thống!");
        }

        try {
            phongRepository.delete(p);
            log.info("Đã xóa phòng vật lý ID {}: {}", id, p.getSoPhong());
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalStateException("Không thể xóa phòng do vi phạm ràng buộc dữ liệu khóa ngoại.");
        }
    }

    @Override
    public void toggleMaintenance(Long id, String maNhanVien) {
        Phong p = getPhongById(id);

        if (p.getTrangThai() == TrangThaiPhong.BAO_TRI) {
            p.setTrangThai(TrangThaiPhong.SAN_SANG);
            phongRepository.save(p);
            log.info("Chuyển phòng {} từ BẢO TRÌ sang SẴN SÀNG", p.getSoPhong());
            return;
        }

        // Nếu phòng đang là DANG_SU_DUNG -> chặn
        if (p.getTrangThai() == TrangThaiPhong.DANG_SU_DUNG) {
            throw new IllegalStateException("Phòng [" + p.getSoPhong()
                    + "] đang có khách lưu trú (DANG_SU_DUNG), không thể chuyển sang trạng thái bảo trì!");
        }

        // Kiểm tra xem hôm nay có lượt phân phòng nào đang hiệu lực không
        LocalDate today = LocalDate.now();
        List<PhanPhong> activeAssignments = phanPhongRepository.findActiveAssignmentsOnDate(today);
        boolean isOccupiedToday = activeAssignments.stream()
                .anyMatch(pp -> pp.getPhong().getMaPhong().equalsIgnoreCase(p.getMaPhong())
                        && pp.getTrangThai() != TrangThaiPhanPhong.DA_HUY
                        && pp.getTrangThai() != TrangThaiPhanPhong.DA_TRA_PHONG);

        if (isOccupiedToday) {
            throw new IllegalStateException("Phòng [" + p.getSoPhong()
                    + "] đang được gán giữ chỗ hoặc có khách ở trong ngày hôm nay, không thể bật chế độ bảo trì!");
        }

        p.setTrangThai(TrangThaiPhong.BAO_TRI);
        phongRepository.save(p);
        log.info("Chuyển phòng {} sang trạng thái BẢO TRÌ bởi nhân viên {}", p.getSoPhong(), maNhanVien);
    }

    @Override
    @Transactional(readOnly = true)
    public long countTotalRooms() {
        return phongRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public long countRoomsByStatus(TrangThaiPhong status) {
        return phongRepository.countByTrangThai(status);
    }
}
