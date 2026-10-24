package com.example.skygatehub.service.dining.impl;

import com.example.skygatehub.common.enums.TrangThaiBanAn;
import com.example.skygatehub.common.enums.TrangThaiDatBan;
import com.example.skygatehub.dto.dining.BanAnRequest;
import com.example.skygatehub.entity.dining.BanAn;
import com.example.skygatehub.repository.dining.BanAnRepository;
import com.example.skygatehub.service.dining.BanAnService;
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
public class BanAnServiceImpl implements BanAnService {

    private static final List<TrangThaiDatBan> TRANG_THAI_KET_THUC =
            List.of(TrangThaiDatBan.DA_HUY, TrangThaiDatBan.DA_HOAN_THANH);

    private final BanAnRepository banAnRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<BanAn> search(String keyword, String khuVuc, TrangThaiBanAn trangThai, Pageable pageable) {
        String cleanKeyword = (keyword != null && !keyword.trim().isEmpty()) ? keyword.trim() : null;
        String cleanKhuVuc = (khuVuc != null && !khuVuc.trim().isEmpty()) ? khuVuc.trim() : null;
        return banAnRepository.searchBanAn(cleanKeyword, cleanKhuVuc, trangThai, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public BanAn getById(Long id) {
        return banAnRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy bàn ăn với ID: " + id));
    }

    @Override
    public BanAn create(BanAnRequest request) {
        String maBanAn = request.getMaBanAn().trim().toUpperCase();
        if (banAnRepository.existsByMaBanAn(maBanAn)) {
            throw new IllegalArgumentException("Mã bàn ăn đã tồn tại: " + maBanAn);
        }

        String soBan = request.getSoBan().trim();
        if (banAnRepository.existsBySoBan(soBan)) {
            throw new IllegalArgumentException("Số bàn đã tồn tại: " + soBan);
        }

        TrangThaiBanAn trangThai = request.getTrangThai() != null ? request.getTrangThai() : TrangThaiBanAn.SAN_SANG;
        if (trangThai == TrangThaiBanAn.DANG_SU_DUNG) {
            throw new IllegalArgumentException("Không thể tạo bàn ở trạng thái đang phục vụ. Chỉ chọn Sẵn sàng hoặc Bảo trì.");
        }

        BanAn b = new BanAn();
        b.setMaBanAn(maBanAn);
        b.setSoBan(soBan);
        b.setKhuVuc(request.getKhuVuc().trim());
        b.setSucChua(request.getSucChua());
        b.setTrangThai(trangThai);

        log.info("Tạo mới bàn ăn mã {}: {}", maBanAn, soBan);
        return banAnRepository.save(b);
    }

    @Override
    public BanAn update(Long id, BanAnRequest request) {
        BanAn b = getById(id);

        String soBan = request.getSoBan().trim();
        if (banAnRepository.existsBySoBanAndMaBanAnNot(soBan, b.getMaBanAn())) {
            throw new IllegalArgumentException("Số bàn đã được đặt cho bàn khác: " + soBan);
        }

        TrangThaiBanAn requested = request.getTrangThai() != null ? request.getTrangThai() : b.getTrangThai();
        if (requested == TrangThaiBanAn.DANG_SU_DUNG && b.getTrangThai() != TrangThaiBanAn.DANG_SU_DUNG) {
            throw new IllegalArgumentException("Không thể đặt trạng thái đang phục vụ thủ công. Trạng thái này do check-in bàn thiết lập.");
        }

        // Bàn đang phục vụ giữ nguyên trạng thái; chỉ SAN_SANG -> BAO_TRI mới cần kiểm tra
        if (b.getTrangThai() == TrangThaiBanAn.SAN_SANG && requested == TrangThaiBanAn.BAO_TRI) {
            checkCanMaintain(b);
            b.setTrangThai(TrangThaiBanAn.BAO_TRI);
        } else if (b.getTrangThai() != TrangThaiBanAn.DANG_SU_DUNG) {
            b.setTrangThai(requested);
        }

        b.setSoBan(soBan);
        b.setKhuVuc(request.getKhuVuc().trim());
        b.setSucChua(request.getSucChua());

        log.info("Cập nhật thông tin bàn ăn ID {}: {}", id, soBan);
        return banAnRepository.save(b);
    }

    @Override
    public void toggleMaintenance(Long id, String maNhanVien) {
        BanAn b = getById(id);

        if (b.getTrangThai() == TrangThaiBanAn.BAO_TRI) {
            b.setTrangThai(TrangThaiBanAn.SAN_SANG);
            banAnRepository.save(b);
            log.info("Chuyển bàn {} từ BẢO TRÌ sang SẴN SÀNG bởi nhân viên {}", b.getSoBan(), maNhanVien);
            return;
        }

        checkCanMaintain(b);

        b.setTrangThai(TrangThaiBanAn.BAO_TRI);
        banAnRepository.save(b);
        log.info("Chuyển bàn {} sang trạng thái BẢO TRÌ bởi nhân viên {}", b.getSoBan(), maNhanVien);
    }

    // Giải phóng bàn bị kẹt DANG_SU_DUNG (dữ liệu cũ, không còn lượt DA_CHECK_IN nào)
    @Override
    public void releaseTable(Long id, String maNhanVien) {
        BanAn b = getById(id);

        if (b.getTrangThai() != TrangThaiBanAn.DANG_SU_DUNG) {
            throw new IllegalStateException("Bàn [" + b.getSoBan() + "] không ở trạng thái đang phục vụ, không cần giải phóng.");
        }

        List<String> dangAn = banAnRepository.findMaDatBanByBanAnAndTrangThai(b.getMaBanAn(), TrangThaiDatBan.DA_CHECK_IN);
        if (!dangAn.isEmpty()) {
            throw new IllegalStateException("Bàn còn khách của lượt " + dangAn.get(0)
                    + ", hãy dùng Hoàn tất bàn ở trang xếp bàn.");
        }

        b.setTrangThai(TrangThaiBanAn.SAN_SANG);
        banAnRepository.save(b);
        log.info("Giải phóng bàn {} về SẴN SÀNG bởi nhân viên {}", b.getSoBan(), maNhanVien);
    }

    @Override
    public void delete(Long id) {
        BanAn b = getById(id);

        long datBanCount = banAnRepository.countDatBanByMaBanAn(b.getMaBanAn());
        if (datBanCount > 0) {
            throw new IllegalStateException("Không thể xóa bàn [" + b.getSoBan() + "] vì bàn đã có "
                    + datBanCount + " lượt đặt bàn trong hệ thống. Hãy chuyển bàn sang trạng thái bảo trì thay vì xóa.");
        }

        try {
            banAnRepository.delete(b);
            log.info("Đã xóa bàn ăn ID {}: {}", id, b.getSoBan());
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalStateException("Không thể xóa bàn do vi phạm ràng buộc dữ liệu khóa ngoại.");
        }
    }

    // Chặn bảo trì khi bàn đang phục vụ hoặc còn đặt bàn hiệu lực từ hôm nay
    private void checkCanMaintain(BanAn b) {
        if (b.getTrangThai() == TrangThaiBanAn.DANG_SU_DUNG) {
            throw new IllegalStateException("Bàn [" + b.getSoBan()
                    + "] đang phục vụ khách (DANG_SU_DUNG), không thể chuyển sang trạng thái bảo trì!");
        }

        long active = banAnRepository.countActiveDatBanFrom(b.getMaBanAn(), LocalDate.now(), TRANG_THAI_KET_THUC);
        if (active > 0) {
            throw new IllegalStateException("Bàn [" + b.getSoBan() + "] còn " + active
                    + " đặt bàn chưa hoàn tất từ hôm nay trở đi, không thể bật chế độ bảo trì!");
        }
    }
}
