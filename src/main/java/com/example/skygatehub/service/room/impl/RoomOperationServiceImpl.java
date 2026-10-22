package com.example.skygatehub.service.room.impl;

import com.example.skygatehub.common.enums.LoaiCheckIn;
import com.example.skygatehub.common.enums.TrangThaiCheckIn;
import com.example.skygatehub.common.enums.TrangThaiDatPhong;
import com.example.skygatehub.common.enums.TrangThaiPhanPhong;
import com.example.skygatehub.common.enums.TrangThaiPhong;
import com.example.skygatehub.dto.room.PendingAssignmentResponse;
import com.example.skygatehub.dto.room.RoomMapCell;
import com.example.skygatehub.dto.room.RoomMapSummaryDto;
import com.example.skygatehub.entity.account.NguoiDung;
import com.example.skygatehub.entity.checkin.CheckIn;
import com.example.skygatehub.entity.order.ChiTietDon;
import com.example.skygatehub.entity.order.DonDat;
import com.example.skygatehub.entity.room.PhanPhong;
import com.example.skygatehub.entity.room.Phong;
import com.example.skygatehub.entity.roombooking.DatPhong;
import com.example.skygatehub.repository.account.NguoiDungRepository;
import com.example.skygatehub.repository.checkin.CheckInRepository;
import com.example.skygatehub.repository.room.PhanPhongRepository;
import com.example.skygatehub.repository.room.PhongRepository;
import com.example.skygatehub.repository.roombooking.DatPhongRepository;
import com.example.skygatehub.service.room.RoomOperationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RoomOperationServiceImpl implements RoomOperationService {

    private final PhongRepository phongRepository;
    private final PhanPhongRepository phanPhongRepository;
    private final DatPhongRepository datPhongRepository;
    private final CheckInRepository checkInRepository;
    private final NguoiDungRepository nguoiDungRepository;

    @Override
    @Transactional(readOnly = true)
    public List<RoomMapCell> buildRoomMap(LocalDate targetDate, Integer maKhu, String maLoaiPhong, String trangThaiFilter) {
        LocalDate date = (targetDate != null) ? targetDate : LocalDate.now();

        String cleanMaLoai = (maLoaiPhong != null && !maLoaiPhong.trim().isEmpty()) ? maLoaiPhong.trim() : null;
        List<Phong> rooms = phongRepository.findAllForRoomMap(maKhu, cleanMaLoai);

        List<PhanPhong> activeAssignments = phanPhongRepository.findActiveAssignmentsOnDate(date);
        Map<String, PhanPhong> roomAssignmentMap = new HashMap<>();
        for (PhanPhong pp : activeAssignments) {
            roomAssignmentMap.put(pp.getPhong().getMaPhong(), pp);
        }

        List<RoomMapCell> cells = new ArrayList<>();
        for (Phong p : rooms) {
            RoomMapCell cell = new RoomMapCell();
            cell.setMaPhong(p.getMaPhong());
            cell.setSoPhong(p.getSoPhong());
            cell.setMaLoaiPhong(p.getLoaiPhong().getMaLoaiPhong());
            cell.setTenLoaiPhong(p.getLoaiPhong().getTenLoaiPhong());
            cell.setMaKhu(p.getLoaiPhong().getKhuLuuTru().getMaKhu());
            cell.setTenKhu(p.getLoaiPhong().getKhuLuuTru().getTenKhu());
            cell.setGiaCoBan(p.getLoaiPhong().getGiaCoBan());
            cell.setLoaiGiuong(p.getLoaiPhong().getLoaiGiuong());
            cell.setDienTich(p.getLoaiPhong().getDienTich());
            cell.setSucChuaNguoiLon(p.getLoaiPhong().getSucChuaNguoiLon());
            cell.setSucChuaTreEm(p.getLoaiPhong().getSucChuaTreEm());
            cell.setTrangThaiVatLy(p.getTrangThai());

            if (p.getTrangThai() == TrangThaiPhong.BAO_TRI) {
                cell.setTrangThaiHienThi("BAO_TRI");
                cell.setCssBadge("bg-secondary");
                cell.setBadgeText("Bảo trì");
            } else {
                PhanPhong pp = roomAssignmentMap.get(p.getMaPhong());
                if (pp != null) {
                    DatPhong dp = pp.getDatPhong();
                    ChiTietDon ctd = dp.getChiTietDon();
                    DonDat dd = ctd.getDonDat();

                    cell.setMaPhanPhong(pp.getMaPhanPhong());
                    cell.setMaDatPhong(dp.getMaDatPhong());
                    cell.setMaDonHang(dd.getMaDonHang());
                    cell.setHoTenKhach(dd.getHoTenKhach());
                    cell.setSdtKhach(dd.getSdtKhach());
                    cell.setEmailKhach(dd.getEmailKhach());
                    cell.setNgayNhan(dp.getNgayNhanPhong());
                    cell.setNgayTra(dp.getNgayTraPhong());
                    cell.setSoLuongPhongDat(dp.getSoLuongPhong());
                    cell.setSoKhach(dp.getSoKhach());
                    cell.setThoiGianPhan(pp.getThoiGianPhan());

                    if (pp.getTrangThai() == TrangThaiPhanPhong.DA_NHAN_PHONG
                            || p.getTrangThai() == TrangThaiPhong.DANG_SU_DUNG
                            || dp.getTrangThai() == TrangThaiDatPhong.DA_NHAN_PHONG) {
                        cell.setTrangThaiHienThi("DANG_O");
                        cell.setCssBadge("bg-danger");
                        cell.setBadgeText("Khách đang ở");
                    } else {
                        cell.setTrangThaiHienThi("DA_PHAN_CHO_NHAN");
                        cell.setCssBadge("bg-warning text-dark");
                        cell.setBadgeText("Chờ nhận phòng");
                    }
                } else {
                    cell.setTrangThaiHienThi("TRONG");
                    cell.setCssBadge("bg-success");
                    cell.setBadgeText("Sẵn sàng");
                }
            }

            if (trangThaiFilter != null && !trangThaiFilter.trim().isEmpty() && !"ALL".equalsIgnoreCase(trangThaiFilter.trim())) {
                if (cell.getTrangThaiHienThi().equalsIgnoreCase(trangThaiFilter.trim())) {
                    cells.add(cell);
                }
            } else {
                cells.add(cell);
            }
        }

        return cells;
    }

    @Override
    @Transactional(readOnly = true)
    public RoomMapSummaryDto calculateRoomMapSummary(LocalDate targetDate, Integer maKhu, String maLoaiPhong) {
        List<RoomMapCell> allCells = buildRoomMap(targetDate, maKhu, maLoaiPhong, null);

        long trong = 0;
        long daPhan = 0;
        long dangO = 0;
        long baoTri = 0;

        for (RoomMapCell c : allCells) {
            switch (c.getTrangThaiHienThi()) {
                case "TRONG" -> trong++;
                case "DA_PHAN_CHO_NHAN" -> daPhan++;
                case "DANG_O" -> dangO++;
                case "BAO_TRI" -> baoTri++;
            }
        }

        return new RoomMapSummaryDto(allCells.size(), trong, daPhan, dangO, baoTri);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PendingAssignmentResponse> getPendingBookingsForAssignment(LocalDate targetDate) {
        List<DatPhong> bookings = datPhongRepository.findAllPendingBookingsWithDetails();
        List<PendingAssignmentResponse> result = new ArrayList<>();

        for (DatPhong dp : bookings) {
            List<PhanPhong> active = phanPhongRepository.findByDatPhong_MaDatPhongAndTrangThaiIn(
                    dp.getMaDatPhong(),
                    List.of(TrangThaiPhanPhong.DANG_PHAN, TrangThaiPhanPhong.DA_NHAN_PHONG)
            );

            int daPhan = active.size();
            int conThieu = dp.getSoLuongPhong() - daPhan;

            PendingAssignmentResponse item = new PendingAssignmentResponse();
            item.setMaDatPhong(dp.getMaDatPhong());
            item.setMaDonHang(dp.getChiTietDon().getDonDat().getMaDonHang());
            item.setHoTenKhach(dp.getChiTietDon().getDonDat().getHoTenKhach());
            item.setSdtKhach(dp.getChiTietDon().getDonDat().getSdtKhach());
            item.setEmailKhach(dp.getChiTietDon().getDonDat().getEmailKhach());
            item.setGhiChu(dp.getChiTietDon().getDonDat().getGhiChu());

            item.setMaLoaiPhong(dp.getLoaiPhong().getMaLoaiPhong());
            item.setTenLoaiPhong(dp.getLoaiPhong().getTenLoaiPhong());
            item.setMaKhu(dp.getLoaiPhong().getKhuLuuTru().getMaKhu());
            item.setTenKhu(dp.getLoaiPhong().getKhuLuuTru().getTenKhu());

            item.setNgayNhanPhong(dp.getNgayNhanPhong());
            item.setNgayTraPhong(dp.getNgayTraPhong());
            item.setSoLuongPhong(dp.getSoLuongPhong());
            item.setSoKhach(dp.getSoKhach());
            item.setTrangThai(dp.getTrangThai());

            item.setSoLuongDaPhan(daPhan);
            item.setSoLuongConThieu(Math.max(0, conThieu));

            List<String> assignedNames = new ArrayList<>();
            List<String> assignedCodes = new ArrayList<>();
            for (PhanPhong pp : active) {
                assignedNames.add(pp.getPhong().getSoPhong());
                assignedCodes.add(pp.getMaPhanPhong());
            }
            item.setDanhSachPhongDaPhan(assignedNames);
            item.setDanhSachMaPhanPhong(assignedCodes);

            result.add(item);
        }

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Phong> getAvailablePhysicalRoomsForBooking(String maDatPhong) {
        DatPhong dp = datPhongRepository.findByMaDatPhongWithDetails(maDatPhong)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn đặt phòng: " + maDatPhong));

        return phongRepository.findAvailableRooms(
                dp.getLoaiPhong().getMaLoaiPhong(),
                dp.getNgayNhanPhong(),
                dp.getNgayTraPhong()
        );
    }

    @Override
    public PhanPhong assignRoomToBooking(String maDatPhong, String maPhong, String maNhanVien) {
        DatPhong dp = datPhongRepository.findByMaDatPhongWithDetails(maDatPhong)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn đặt phòng: " + maDatPhong));

        if (dp.getTrangThai() == TrangThaiDatPhong.DA_HUY) {
            throw new IllegalStateException("Đơn đặt phòng [" + maDatPhong + "] đã bị hủy, không thể phân phòng!");
        }
        if (dp.getTrangThai() == TrangThaiDatPhong.DA_TRA_PHONG) {
            throw new IllegalStateException("Đơn đặt phòng [" + maDatPhong + "] đã hoàn tất trả phòng!");
        }

        Phong phong = phongRepository.findByMaPhongWithLock(maPhong)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy phòng vật lý: " + maPhong));

        if (phong.getTrangThai() == TrangThaiPhong.BAO_TRI) {
            throw new IllegalStateException("Phòng [" + phong.getSoPhong() + "] đang bảo trì, không thể phân cho khách!");
        }

        if (!phong.getLoaiPhong().getMaLoaiPhong().equalsIgnoreCase(dp.getLoaiPhong().getMaLoaiPhong())) {
            throw new IllegalArgumentException("Loại phòng của phòng " + phong.getSoPhong()
                    + " (" + phong.getLoaiPhong().getTenLoaiPhong()
                    + ") không khớp với loại phòng của đơn đặt (" + dp.getLoaiPhong().getTenLoaiPhong() + ")!");
        }

        // Kiểm tra khóa bi quan chống 2 nhân viên lễ tân gán trùng phòng
        boolean overlap = phanPhongRepository.existsOverlappingAssignmentWithLock(
                maPhong, dp.getNgayNhanPhong(), dp.getNgayTraPhong()
        );
        if (overlap) {
            throw new IllegalStateException("Phòng [" + phong.getSoPhong()
                    + "] đã có lịch phân phòng trùng trong khoảng từ "
                    + dp.getNgayNhanPhong() + " đến " + dp.getNgayTraPhong() + "!");
        }

        long activeCount = phanPhongRepository.countByDatPhong_MaDatPhongAndTrangThaiNot(
                maDatPhong, TrangThaiPhanPhong.DA_HUY
        );
        if (activeCount >= dp.getSoLuongPhong()) {
            throw new IllegalStateException("Đơn đặt phòng [" + maDatPhong
                    + "] đã được phân đủ số lượng yêu cầu (" + dp.getSoLuongPhong() + " phòng)!");
        }

        // Sinh mã MA_PHAN_PHONG dạng PP023, PP024...
        String nextMa = generateNextMaPhanPhong();

        // Tìm nhân viên phân phòng (hoặc người dùng đầu tiên)
        NguoiDung nhanVien = null;
        if (maNhanVien != null && !maNhanVien.trim().isEmpty()) {
            nhanVien = nguoiDungRepository.findByMaNguoiDung(maNhanVien.trim()).orElse(null);
        }
        if (nhanVien == null) {
            nhanVien = nguoiDungRepository.findAll().stream().findFirst().orElse(null);
        }

        PhanPhong pp = new PhanPhong();
        pp.setMaPhanPhong(nextMa);
        pp.setDatPhong(dp);
        pp.setPhong(phong);
        pp.setNhanVien(nhanVien);
        pp.setThoiGianPhan(LocalDateTime.now());
        pp.setTrangThai(TrangThaiPhanPhong.DANG_PHAN);

        PhanPhong savedPp = phanPhongRepository.save(pp);
        log.info("Phân phòng thành công: Mã PP={}, Đơn={}, Phòng={}", nextMa, maDatPhong, phong.getSoPhong());

        // Nếu đã phân đủ số phòng yêu cầu -> tự động chuyển DatPhong sang DA_XAC_NHAN
        if (activeCount + 1 >= dp.getSoLuongPhong()) {
            if (dp.getTrangThai() == TrangThaiDatPhong.CHO_XAC_NHAN) {
                dp.setTrangThai(TrangThaiDatPhong.DA_XAC_NHAN);
                datPhongRepository.save(dp);
                log.info("Đơn đặt phòng {} đã phân đủ {} phòng -> chuyển sang DA_XAC_NHAN", maDatPhong, dp.getSoLuongPhong());
            }
        }

        return savedPp;
    }

    @Override
    public void unassignRoom(String maPhanPhong) {
        PhanPhong pp = phanPhongRepository.findByMaPhanPhong(maPhanPhong)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy bản ghi phân phòng: " + maPhanPhong));

        if (pp.getTrangThai() == TrangThaiPhanPhong.DA_NHAN_PHONG) {
            throw new IllegalStateException("Không thể hủy phân phòng vì khách đã làm thủ tục nhận phòng (Check-in)!");
        }
        if (pp.getTrangThai() == TrangThaiPhanPhong.DA_TRA_PHONG) {
            throw new IllegalStateException("Phân phòng đã kết thúc trả phòng.");
        }

        pp.setTrangThai(TrangThaiPhanPhong.DA_HUY);
        phanPhongRepository.save(pp);
        log.info("Đã hủy phân phòng {}", maPhanPhong);

        DatPhong dp = pp.getDatPhong();
        long remaining = phanPhongRepository.countByDatPhong_MaDatPhongAndTrangThaiNot(
                dp.getMaDatPhong(), TrangThaiPhanPhong.DA_HUY
        );

        if (remaining < dp.getSoLuongPhong() && dp.getTrangThai() == TrangThaiDatPhong.DA_XAC_NHAN) {
            dp.setTrangThai(TrangThaiDatPhong.CHO_XAC_NHAN);
            datPhongRepository.save(dp);
            log.info("Đơn đặt phòng {} thiếu phòng sau khi hủy -> chuyển về CHO_XAC_NHAN", dp.getMaDatPhong());
        }
    }

    @Override
    public String checkInRoom(String maDatPhong, String maNhanVien) {
        DatPhong dp = datPhongRepository.findByMaDatPhongWithDetails(maDatPhong)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn đặt phòng: " + maDatPhong));

        if (dp.getTrangThai() == TrangThaiDatPhong.DA_NHAN_PHONG) {
            throw new IllegalStateException("Đơn đặt phòng [" + maDatPhong + "] đã hoàn tất Check-in nhận phòng trước đó.");
        }
        if (dp.getTrangThai() == TrangThaiDatPhong.DA_HUY) {
            throw new IllegalStateException("Đơn đặt phòng [" + maDatPhong + "] đã bị hủy, không thể Check-in.");
        }

        List<PhanPhong> assignments = phanPhongRepository.findByDatPhong_MaDatPhongAndTrangThaiIn(
                maDatPhong,
                List.of(TrangThaiPhanPhong.DANG_PHAN, TrangThaiPhanPhong.DA_NHAN_PHONG)
        );

        if (assignments.isEmpty()) {
            throw new IllegalStateException("Đơn đặt phòng [" + maDatPhong
                    + "] chưa được phân bất kỳ phòng vật lý nào. Vui lòng phân phòng trước khi Check-in!");
        }

        dp.setTrangThai(TrangThaiDatPhong.DA_NHAN_PHONG);
        datPhongRepository.save(dp);

        StringBuilder sbRooms = new StringBuilder();
        for (PhanPhong pp : assignments) {
            pp.setTrangThai(TrangThaiPhanPhong.DA_NHAN_PHONG);
            phanPhongRepository.save(pp);

            Phong p = pp.getPhong();
            p.setTrangThai(TrangThaiPhong.DANG_SU_DUNG);
            phongRepository.save(p);

            if (sbRooms.length() > 0) sbRooms.append(", ");
            sbRooms.append(p.getSoPhong());
        }

        // Tìm nhân viên
        NguoiDung nv = null;
        if (maNhanVien != null && !maNhanVien.trim().isEmpty()) {
            nv = nguoiDungRepository.findByMaNguoiDung(maNhanVien.trim()).orElse(null);
        }
        if (nv == null) {
            nv = nguoiDungRepository.findAll().stream().findFirst().orElse(null);
        }

        // Ghi nhận bản ghi CHECK_IN nếu chưa có
        Optional<CheckIn> existingCi = checkInRepository.findByDatPhong_MaDatPhong(maDatPhong);
        if (existingCi.isEmpty()) {
            CheckIn ci = new CheckIn();
            ci.setMaCheckIn("CI-P-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
            ci.setDonDat(dp.getChiTietDon().getDonDat());
            ci.setLoaiCheckIn(LoaiCheckIn.PHONG);
            ci.setDatPhong(dp);
            ci.setNhanVien(nv);
            ci.setSoLuong(assignments.size());
            ci.setThoiGianCheckIn(LocalDateTime.now());
            ci.setTrangThai(TrangThaiCheckIn.DA_CHECK_IN);
            ci.setGhiChu("Lễ tân hoàn tất nhận phòng (" + sbRooms + ") cho đơn " + maDatPhong);
            checkInRepository.save(ci);
        }

        log.info("[CHECKIN-PHONG] Check-in thành công đơn {}: Phòng [{}]", maDatPhong, sbRooms);
        return sbRooms.toString();
    }

    @Override
    public String checkOutRoom(String maDatPhong, String maNhanVien) {
        DatPhong dp = datPhongRepository.findByMaDatPhongWithDetails(maDatPhong)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn đặt phòng: " + maDatPhong));

        if (dp.getTrangThai() != TrangThaiDatPhong.DA_NHAN_PHONG) {
            throw new IllegalStateException("Đơn đặt phòng [" + maDatPhong + "] chưa thực hiện Check-in nhận phòng hoặc đã trả phòng.");
        }

        dp.setTrangThai(TrangThaiDatPhong.DA_TRA_PHONG);
        datPhongRepository.save(dp);

        List<PhanPhong> assignments = phanPhongRepository.findByDatPhong_MaDatPhongAndTrangThaiIn(
                maDatPhong,
                List.of(TrangThaiPhanPhong.DA_NHAN_PHONG, TrangThaiPhanPhong.DANG_PHAN)
        );

        StringBuilder sbRooms = new StringBuilder();
        for (PhanPhong pp : assignments) {
            pp.setTrangThai(TrangThaiPhanPhong.DA_TRA_PHONG);
            phanPhongRepository.save(pp);

            Phong p = pp.getPhong();
            p.setTrangThai(TrangThaiPhong.SAN_SANG);
            phongRepository.save(p);

            if (sbRooms.length() > 0) sbRooms.append(", ");
            sbRooms.append(p.getSoPhong());
        }

        log.info("[CHECKOUT-PHONG] Trả phòng thành công đơn {}: Phòng [{}] đã sẵn sàng", maDatPhong, sbRooms);
        return sbRooms.toString();
    }

    private String generateNextMaPhanPhong() {
        List<String> latest = phanPhongRepository.findLatestMaPhanPhong();
        int maxIndex = 0;
        for (String c : latest) {
            if (c != null && c.startsWith("PP")) {
                try {
                    int num = Integer.parseInt(c.substring(2));
                    if (num > maxIndex) {
                        maxIndex = num;
                    }
                } catch (NumberFormatException ignored) {}
            }
        }
        return String.format("PP%03d", maxIndex + 1);
    }
}
