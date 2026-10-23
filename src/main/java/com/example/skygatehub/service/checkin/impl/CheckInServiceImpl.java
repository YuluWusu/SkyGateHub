package com.example.skygatehub.service.checkin.impl;

import com.example.skygatehub.common.enums.LoaiCheckIn;
import com.example.skygatehub.common.enums.TrangThaiCheckIn;
import com.example.skygatehub.common.enums.TrangThaiDon;
import com.example.skygatehub.common.enums.TrangThaiVe;
import com.example.skygatehub.dto.checkin.CheckInLookupResponse;
import com.example.skygatehub.entity.account.NguoiDung;
import com.example.skygatehub.entity.checkin.CheckIn;
import com.example.skygatehub.entity.order.DonDat;
import com.example.skygatehub.entity.order.Ve;
import com.example.skygatehub.repository.account.NguoiDungRepository;
import com.example.skygatehub.repository.checkin.CheckInRepository;
import com.example.skygatehub.repository.order.DonDatRepository;
import com.example.skygatehub.repository.order.VeRepository;
import com.example.skygatehub.service.checkin.CheckInService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CheckInServiceImpl implements CheckInService {

    private final VeRepository veRepository;
    private final DonDatRepository donDatRepository;
    private final CheckInRepository checkInRepository;
    private final NguoiDungRepository nguoiDungRepository;

    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy");

    @Override
    @Transactional(readOnly = true)
    public CheckInLookupResponse lookupTicket(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            throw new IllegalArgumentException("Vui lòng nhập mã vé, mã đơn hàng, mã QR hoặc số điện thoại để tra cứu.");
        }

        String kw = keyword.trim();
        DonDat donDat = null;
        Ve targetVe = null;

        // 1. Tìm theo mã vé vật lý (VD: VE_TK001)
        Optional<Ve> veOpt = veRepository.findByMaVe(kw);
        if (veOpt.isPresent()) {
            targetVe = veOpt.get();
            donDat = targetVe.getChiTietDon().getDonDat();
        }

        // 2. Tìm theo mã vé chuỗi token (VD: TK-260501-0001-A1)
        if (donDat == null) {
            veOpt = veRepository.findByMaVeChuoi(kw);
            if (veOpt.isPresent()) {
                targetVe = veOpt.get();
                donDat = targetVe.getChiTietDon().getDonDat();
            }
        }

        // 3. Tìm theo mã đơn hàng hiển thị (VD: CTDG-260501-0001)
        if (donDat == null) {
            donDat = donDatRepository.findByMaDonHang(kw).orElse(null);
        }

        // 4. Tìm theo mã QR (VD: QR-CTDG-0001)
        if (donDat == null) {
            donDat = donDatRepository.findByMaQr(kw).orElse(null);
        }

        // 5. Tìm theo mã đơn đặt nội bộ (VD: DD001)
        if (donDat == null) {
            donDat = donDatRepository.findByMaDonDat(kw).orElse(null);
        }

        // 6. Tìm theo số điện thoại khách hàng
        if (donDat == null) {
            List<DonDat> dsDon = donDatRepository.findBySdtKhachOrderByThoiGianDatDesc(kw);
            if (!dsDon.isEmpty()) {
                donDat = dsDon.get(0);
            }
        }

        if (donDat == null) {
            throw new IllegalArgumentException("Không tìm thấy thông tin vé hoặc đơn hàng phù hợp với từ khóa: [" + kw + "]. Vui lòng kiểm tra lại!");
        }

        // Lấy toàn bộ danh sách vé thuộc đơn đặt này
        List<Ve> allTickets = veRepository.findByChiTietDon_DonDat_MaDonDat(donDat.getMaDonDat());

        List<CheckInLookupResponse.TicketItemDto> ticketDtos = new ArrayList<>();
        int soChuaDung = 0;
        int soDaDung = 0;

        for (Ve v : allTickets) {
            boolean isTarget = (targetVe != null && targetVe.getMaVe().equals(v.getMaVe()));
            boolean canCheckIn = (v.getTrangThai() == TrangThaiVe.CHUA_SU_DUNG && donDat.getTrangThaiDon() != TrangThaiDon.DA_HUY);

            String thongBao;
            if (v.getTrangThai() == TrangThaiVe.DA_SU_DUNG) {
                soDaDung++;
                String timeStr = (v.getThoiGianSuDung() != null) ? v.getThoiGianSuDung().format(TIME_FMT) : "trước đó";
                thongBao = "Đã vào cổng (" + timeStr + ")";
            } else if (v.getTrangThai() == TrangThaiVe.DA_HUY) {
                thongBao = "Vé đã bị hủy";
            } else if (donDat.getTrangThaiDon() == TrangThaiDon.DA_HUY) {
                thongBao = "Đơn hàng đã bị hủy";
            } else {
                soChuaDung++;
                thongBao = isTarget ? "Vé tra cứu: Hợp lệ - Sẵn sàng Check-in" : "Hợp lệ - Sẵn sàng Check-in";
            }

            CheckInLookupResponse.TicketItemDto dto = CheckInLookupResponse.TicketItemDto.builder()
                    .maVe(v.getMaVe())
                    .maVeChuoi(v.getMaVeChuoi())
                    .tenLoaiVe(v.getLoaiVe() != null ? v.getLoaiVe().getTenLoaiVe() : "Vé tham quan")
                    .loaiVeType(v.getLoaiVe() != null && v.getLoaiVe().getLoaiVe() != null ? v.getLoaiVe().getLoaiVe().name() : "THUONG")
                    .ngayThamQuan(v.getNgayThamQuan())
                    .trangThai(v.getTrangThai())
                    .thoiGianSuDung(v.getThoiGianSuDung())
                    .maChiTiet(v.getChiTietDon() != null ? v.getChiTietDon().getMaChiTiet() : "")
                    .donGia(v.getChiTietDon() != null ? v.getChiTietDon().getDonGia() : null)
                    .canCheckIn(canCheckIn)
                    .thongBaoTrangThai(thongBao)
                    .build();

            // Nếu tìm chính xác 1 vé thì đưa vé đó lên đầu danh sách
            if (isTarget) {
                ticketDtos.add(0, dto);
            } else {
                ticketDtos.add(dto);
            }
        }

        return CheckInLookupResponse.builder()
                .maDonDat(donDat.getMaDonDat())
                .maDonHang(donDat.getMaDonHang())
                .hoTenKhach(donDat.getHoTenKhach())
                .sdtKhach(donDat.getSdtKhach())
                .emailKhach(donDat.getEmailKhach())
                .ghiChu(donDat.getGhiChu())
                .thoiGianDat(donDat.getThoiGianDat())
                .tongTien(donDat.getTongTien())
                .trangThaiDon(donDat.getTrangThaiDon())
                .trangThaiThanhToan(donDat.getTrangThaiThanhToan())
                .maQr(donDat.getMaQr())
                .tickets(ticketDtos)
                .tongSoVe(allTickets.size())
                .soVeChuaDung(soChuaDung)
                .soVeDaDung(soDaDung)
                .build();
    }

    @Override
    public CheckIn checkInSingleTicket(String maVe, String maNhanVien, String ghiChu) {
        if (maVe == null || maVe.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã vé không được để trống!");
        }

        Ve ve = veRepository.findByMaVe(maVe.trim())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy thông tin vé có mã: " + maVe));

        if (ve.getTrangThai() == TrangThaiVe.DA_SU_DUNG) {
            String timeStr = (ve.getThoiGianSuDung() != null) ? ve.getThoiGianSuDung().format(TIME_FMT) : "trước đó";
            throw new IllegalStateException("CẢNH BÁO QUÉT TRÙNG: Vé [" + maVe + "] đã được sử dụng lúc " + timeStr + "!");
        }

        if (ve.getTrangThai() == TrangThaiVe.DA_HUY) {
            throw new IllegalStateException("Vé [" + maVe + "] đã bị hủy bỏ, không có giá trị vào cổng!");
        }

        DonDat donDat = ve.getChiTietDon().getDonDat();
        if (donDat.getTrangThaiDon() == TrangThaiDon.DA_HUY) {
            throw new IllegalStateException("Đơn hàng [" + donDat.getMaDonHang() + "] đã bị hủy, không thể sử dụng vé!");
        }

        // Cập nhật trạng thái vé
        ve.setTrangThai(TrangThaiVe.DA_SU_DUNG);
        ve.setThoiGianSuDung(LocalDateTime.now());
        veRepository.save(ve);

        // Tìm nhân viên soát vé
        NguoiDung nhanVien = null;
        if (maNhanVien != null && !maNhanVien.trim().isEmpty()) {
            nhanVien = nguoiDungRepository.findByMaNguoiDung(maNhanVien.trim()).orElse(null);
        }
        if (nhanVien == null) {
            nhanVien = nguoiDungRepository.findAll().stream().findFirst().orElse(null);
        }

        // Ghi nhận bản ghi Check-in vào bảng CHECK_IN
        CheckIn checkIn = new CheckIn();
        checkIn.setMaCheckIn("CI-V-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        checkIn.setDonDat(donDat);
        checkIn.setLoaiCheckIn(LoaiCheckIn.VE);
        checkIn.setVe(ve);
        checkIn.setNhanVien(nhanVien);
        checkIn.setSoLuong(1);
        checkIn.setThoiGianCheckIn(LocalDateTime.now());
        checkIn.setTrangThai(TrangThaiCheckIn.DA_CHECK_IN);
        checkIn.setGhiChu((ghiChu != null && !ghiChu.trim().isEmpty()) ? ghiChu.trim() : ("Soát vé vào cổng: " + ve.getMaVe()));

        CheckIn savedCheckIn = checkInRepository.save(checkIn);
        log.info("[CHECKIN-VE] Check-in thành công vé: {} cho khách {} (Mã CI: {})",
                ve.getMaVe(), donDat.getHoTenKhach(), savedCheckIn.getMaCheckIn());

        return savedCheckIn;
    }

    @Override
    public List<CheckIn> checkInAllTicketsInOrder(String maDonDat, String maNhanVien, String ghiChu) {
        if (maDonDat == null || maDonDat.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã đơn đặt không được để trống!");
        }

        List<Ve> tickets = veRepository.findByChiTietDon_DonDat_MaDonDat(maDonDat.trim());
        List<CheckIn> results = new ArrayList<>();

        for (Ve v : tickets) {
            if (v.getTrangThai() == TrangThaiVe.CHUA_SU_DUNG) {
                CheckIn ci = checkInSingleTicket(v.getMaVe(), maNhanVien, ghiChu);
                results.add(ci);
            }
        }

        if (results.isEmpty()) {
            throw new IllegalStateException("Không có vé nào ở trạng thái CHƯA SỬ DỤNG trong đơn hàng này để Check-in!");
        }

        return results;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CheckIn> getRecentTicketCheckIns(int limit) {
        int pageSize = limit > 0 ? limit : 20;
        return checkInRepository.findRecentByLoaiCheckIn(LoaiCheckIn.VE, org.springframework.data.domain.PageRequest.of(0, pageSize));
    }
}
