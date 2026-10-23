package com.example.skygatehub.service.dining.impl;

import com.example.skygatehub.common.enums.LoaiCheckIn;
import com.example.skygatehub.common.enums.TrangThaiBanAn;
import com.example.skygatehub.common.enums.TrangThaiCheckIn;
import com.example.skygatehub.common.enums.TrangThaiDatBan;
import com.example.skygatehub.dto.dining.MealScheduleRow;
import com.example.skygatehub.dto.dining.TableMapCell;
import com.example.skygatehub.entity.account.NguoiDung;
import com.example.skygatehub.entity.checkin.CheckIn;
import com.example.skygatehub.entity.dining.BanAn;
import com.example.skygatehub.entity.dining.CaAn;
import com.example.skygatehub.entity.dining.DatBan;
import com.example.skygatehub.repository.account.NguoiDungRepository;
import com.example.skygatehub.repository.checkin.CheckInRepository;
import com.example.skygatehub.repository.dining.BanAnRepository;
import com.example.skygatehub.repository.dining.CaAnRepository;
import com.example.skygatehub.repository.dining.DatBanRepository;
import com.example.skygatehub.service.dining.TableAssignmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TableAssignmentServiceImpl implements TableAssignmentService {

    private final BanAnRepository banAnRepository;
    private final DatBanRepository datBanRepository;
    private final CaAnRepository caAnRepository;
    private final CheckInRepository checkInRepository;
    private final NguoiDungRepository nguoiDungRepository;

    @Override
    @Transactional(readOnly = true)
    public List<TableMapCell> buildTableMap(LocalDate targetDate, String maCaAn, String khuVuc, String statusFilter) {
        LocalDate date = (targetDate != null) ? targetDate : LocalDate.now();

        List<BanAn> allTables = banAnRepository.findAll();
        if (khuVuc != null && !khuVuc.trim().isEmpty() && !"ALL".equalsIgnoreCase(khuVuc.trim())) {
            allTables = allTables.stream()
                    .filter(b -> khuVuc.trim().equalsIgnoreCase(b.getKhuVuc()))
                    .collect(Collectors.toList());
        }

        List<DatBan> bookingsOnDate;
        if (maCaAn != null && !maCaAn.trim().isEmpty() && !"ALL".equalsIgnoreCase(maCaAn.trim())) {
            bookingsOnDate = datBanRepository.findByNgayAnAndDichVuCaAn_CaAn_MaCaAn(date, maCaAn.trim());
        } else {
            bookingsOnDate = datBanRepository.findByNgayAn(date);
        }

        // Tạo map: maBanAn -> DatBan
        Map<String, DatBan> tableToBooking = new HashMap<>();
        for (DatBan db : bookingsOnDate) {
            if (db.getBanAn() != null && db.getTrangThai() != TrangThaiDatBan.DA_HUY) {
                tableToBooking.put(db.getBanAn().getMaBanAn(), db);
            }
        }

        List<TableMapCell> cells = new ArrayList<>();
        for (BanAn b : allTables) {
            TableMapCell cell = TableMapCell.builder()
                    .maBanAn(b.getMaBanAn())
                    .soBan(b.getSoBan())
                    .khuVuc(b.getKhuVuc())
                    .sucChua(b.getSucChua())
                    .trangThaiVatLy(b.getTrangThai())
                    .build();

            if (b.getTrangThai() == TrangThaiBanAn.BAO_TRI) {
                cell.setTrangThaiHienThi("BAO_TRI");
                cell.setCssBadge("bg-secondary");
                cell.setBadgeText("Bảo trì");
            } else {
                DatBan db = tableToBooking.get(b.getMaBanAn());
                if (db != null) {
                    cell.setMaDatBan(db.getMaDatBan());
                    if (db.getDonDat() != null) {
                        cell.setMaDonHang(db.getDonDat().getMaDonHang());
                        cell.setHoTenKhach(db.getDonDat().getHoTenKhach());
                        cell.setSdtKhach(db.getDonDat().getSdtKhach());
                    }
                    cell.setSoKhach(db.getSoKhach());
                    if (db.getDichVuCaAn() != null) {
                        if (db.getDichVuCaAn().getDichVuAnUong() != null) {
                            cell.setTenDichVu(db.getDichVuCaAn().getDichVuAnUong().getTenDichVu());
                        }
                        if (db.getDichVuCaAn().getCaAn() != null) {
                            cell.setMaCaAn(db.getDichVuCaAn().getCaAn().getMaCaAn());
                            cell.setTenCaAn(db.getDichVuCaAn().getCaAn().getTenCaAn());
                        }
                    }
                    cell.setGhiChu(db.getGhiChu());

                    if (db.getTrangThai() == TrangThaiDatBan.DA_CHECK_IN || b.getTrangThai() == TrangThaiBanAn.DANG_SU_DUNG) {
                        cell.setTrangThaiHienThi("DANG_AN");
                        cell.setCssBadge("bg-danger");
                        cell.setBadgeText("Đang dùng bữa");
                    } else if (db.getTrangThai() == TrangThaiDatBan.DA_HOAN_THANH) {
                        cell.setTrangThaiHienThi("TRONG");
                        cell.setCssBadge("bg-success");
                        cell.setBadgeText("Sẵn sàng");
                    } else {
                        cell.setTrangThaiHienThi("DA_XEP_CHO_KHACH");
                        cell.setCssBadge("bg-warning text-dark");
                        cell.setBadgeText("Chờ khách nhận bàn");
                    }
                } else {
                    cell.setTrangThaiHienThi("TRONG");
                    cell.setCssBadge("bg-success");
                    cell.setBadgeText("Sẵn sàng");
                }
            }

            if (statusFilter != null && !statusFilter.trim().isEmpty() && !"ALL".equalsIgnoreCase(statusFilter.trim())) {
                if (cell.getTrangThaiHienThi().equalsIgnoreCase(statusFilter.trim())) {
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
    public List<DatBan> getPendingBookings(LocalDate targetDate, String maCaAn) {
        LocalDate date = (targetDate != null) ? targetDate : LocalDate.now();
        List<DatBan> list;
        if (maCaAn != null && !maCaAn.trim().isEmpty() && !"ALL".equalsIgnoreCase(maCaAn.trim())) {
            list = datBanRepository.findByNgayAnAndDichVuCaAn_CaAn_MaCaAn(date, maCaAn.trim());
        } else {
            list = datBanRepository.findByNgayAn(date);
        }

        return list.stream()
                .filter(b -> b.getTrangThai() != TrangThaiDatBan.DA_HUY && b.getTrangThai() != TrangThaiDatBan.DA_HOAN_THANH)
                .sorted(Comparator.comparing(DatBan::getMaDatBan))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BanAn> getAvailableTablesForBooking(String maDatBan) {
        DatBan db = datBanRepository.findByMaDatBan(maDatBan)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn đặt bàn: " + maDatBan));

        String caAnCode = (db.getDichVuCaAn() != null && db.getDichVuCaAn().getCaAn() != null)
                ? db.getDichVuCaAn().getCaAn().getMaCaAn() : "CA_TRUA";

        return banAnRepository.findAvailableTables(db.getSoKhach(), db.getNgayAn(), caAnCode);
    }

    @Override
    public void assignTableToBooking(String maDatBan, String maBanAn, String maNhanVien) {
        DatBan db = datBanRepository.findByMaDatBan(maDatBan)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn đặt bàn: " + maDatBan));

        BanAn ba = banAnRepository.findByMaBanAn(maBanAn)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy bàn ăn: " + maBanAn));

        if (ba.getTrangThai() == TrangThaiBanAn.BAO_TRI) {
            throw new IllegalStateException("Bàn [" + ba.getSoBan() + "] đang bảo trì, không thể phân!");
        }

        db.setBanAn(ba);
        if (db.getTrangThai() == TrangThaiDatBan.CHO_XAC_NHAN) {
            db.setTrangThai(TrangThaiDatBan.DA_XAC_NHAN);
        }
        datBanRepository.save(db);
        log.info("[ASSIGN-TABLE] Phân bàn {} thành công cho đơn đặt bàn {}", ba.getSoBan(), maDatBan);
    }

    @Override
    public void unassignTable(String maDatBan) {
        DatBan db = datBanRepository.findByMaDatBan(maDatBan)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn đặt bàn: " + maDatBan));

        if (db.getTrangThai() == TrangThaiDatBan.DA_CHECK_IN) {
            throw new IllegalStateException("Khách đã Check-in nhận bàn này, không thể hủy xếp bàn!");
        }

        db.setBanAn(null);
        db.setTrangThai(TrangThaiDatBan.CHO_XAC_NHAN);
        datBanRepository.save(db);
        log.info("[UNASSIGN-TABLE] Đã hủy xếp bàn cho đơn đặt bàn {}", maDatBan);
    }

    @Override
    public String checkInTable(String maDatBan, String maNhanVien) {
        DatBan db = datBanRepository.findByMaDatBan(maDatBan)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn đặt bàn: " + maDatBan));

        if (db.getBanAn() == null) {
            throw new IllegalStateException("Đơn đặt bàn [" + maDatBan + "] chưa được xếp bàn vật lý. Vui lòng xếp bàn trước khi Check-in!");
        }

        if (db.getTrangThai() == TrangThaiDatBan.DA_CHECK_IN) {
            throw new IllegalStateException("Đơn đặt bàn [" + maDatBan + "] đã hoàn tất Check-in trước đó.");
        }

        if (db.getTrangThai() == TrangThaiDatBan.DA_HUY) {
            throw new IllegalStateException("Đơn đặt bàn [" + maDatBan + "] đã bị hủy, không thể Check-in.");
        }

        // Cập nhật trạng thái đặt bàn
        db.setTrangThai(TrangThaiDatBan.DA_CHECK_IN);
        datBanRepository.save(db);

        // Đổi trạng thái bàn vật lý sang Đang sử dụng
        BanAn ba = db.getBanAn();
        ba.setTrangThai(TrangThaiBanAn.DANG_SU_DUNG);
        banAnRepository.save(ba);

        // Tìm nhân viên
        NguoiDung nv = null;
        if (maNhanVien != null && !maNhanVien.trim().isEmpty()) {
            nv = nguoiDungRepository.findByMaNguoiDung(maNhanVien.trim()).orElse(null);
        }
        if (nv == null) {
            nv = nguoiDungRepository.findAll().stream().findFirst().orElse(null);
        }

        // Ghi nhận bản ghi Check-in vào bảng CHECK_IN
        CheckIn ci = new CheckIn();
        ci.setMaCheckIn("CI-A-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        ci.setDonDat(db.getDonDat());
        ci.setLoaiCheckIn(LoaiCheckIn.AN_UONG);
        ci.setDatBan(db);
        ci.setNhanVien(nv);
        ci.setSoLuong(db.getSoKhach() != null ? db.getSoKhach() : 1);
        ci.setThoiGianCheckIn(LocalDateTime.now());
        ci.setTrangThai(TrangThaiCheckIn.DA_CHECK_IN);
        ci.setGhiChu("Khách nhận bàn " + ba.getSoBan() + " (" + ba.getKhuVuc() + ")");
        checkInRepository.save(ci);

        log.info("[CHECKIN-AN] Check-in thành công đơn đặt bàn {} vào bàn {}", maDatBan, ba.getSoBan());
        return ba.getSoBan();
    }

    @Override
    public String completeTable(String maDatBan, String maNhanVien) {
        DatBan db = datBanRepository.findByMaDatBan(maDatBan)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn đặt bàn: " + maDatBan));

        db.setTrangThai(TrangThaiDatBan.DA_HOAN_THANH);
        datBanRepository.save(db);

        BanAn ba = db.getBanAn();
        if (ba != null) {
            ba.setTrangThai(TrangThaiBanAn.SAN_SANG);
            banAnRepository.save(ba);
        }

        log.info("[COMPLETE-TABLE] Trả bàn thành công đơn đặt bàn {}", maDatBan);
        return (ba != null) ? ba.getSoBan() : "N/A";
    }

    @Override
    @Transactional(readOnly = true)
    public List<MealScheduleRow> getMealSchedule(LocalDate targetDate) {
        LocalDate date = (targetDate != null) ? targetDate : LocalDate.now();
        List<CaAn> caAnList = caAnRepository.findAll();
        List<DatBan> allBookings = datBanRepository.findByNgayAn(date);

        List<MealScheduleRow> schedule = new ArrayList<>();
        for (CaAn ca : caAnList) {
            List<DatBan> forThisShift = allBookings.stream()
                    .filter(b -> b.getDichVuCaAn() != null
                            && b.getDichVuCaAn().getCaAn() != null
                            && ca.getMaCaAn().equals(b.getDichVuCaAn().getCaAn().getMaCaAn())
                            && b.getTrangThai() != TrangThaiDatBan.DA_HUY)
                    .collect(Collectors.toList());

            int tongKhach = forThisShift.stream().mapToInt(DatBan::getSoKhach).sum();
            int daXep = (int) forThisShift.stream().filter(b -> b.getBanAn() != null).count();
            int chuaXep = forThisShift.size() - daXep;
            int daCheckIn = (int) forThisShift.stream().filter(b -> b.getTrangThai() == TrangThaiDatBan.DA_CHECK_IN).count();
            int daHoanThanh = (int) forThisShift.stream().filter(b -> b.getTrangThai() == TrangThaiDatBan.DA_HOAN_THANH).count();

            String gio = (ca.getGioBatDau() != null && ca.getGioKetThuc() != null)
                    ? ca.getGioBatDau() + " - " + ca.getGioKetThuc() : "Cả ca";

            MealScheduleRow row = MealScheduleRow.builder()
                    .maCaAn(ca.getMaCaAn())
                    .tenCaAn(ca.getTenCaAn())
                    .khungGio(gio)
                    .tongDonDat(forThisShift.size())
                    .tongSoKhach(tongKhach)
                    .soBanDaXep(daXep)
                    .soBanChuaXep(chuaXep)
                    .soDonDaCheckIn(daCheckIn)
                    .soDonDaHoanThanh(daHoanThanh)
                    .danhSachDatBan(forThisShift)
                    .build();

            schedule.add(row);
        }

        return schedule;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CaAn> getAllCaAn() {
        return caAnRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getAllKhuVuc() {
        return banAnRepository.findAll().stream()
                .map(BanAn::getKhuVuc)
                .filter(Objects::nonNull)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }
}
