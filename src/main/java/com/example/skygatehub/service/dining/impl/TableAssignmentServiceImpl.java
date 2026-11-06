package com.example.skygatehub.service.dining.impl;

import com.example.skygatehub.common.enums.LoaiCheckIn;
import com.example.skygatehub.common.enums.LoaiDichVuDon;
import com.example.skygatehub.common.enums.TrangThaiBanAn;
import com.example.skygatehub.common.enums.TrangThaiCheckIn;
import com.example.skygatehub.common.enums.TrangThaiChiTiet;
import com.example.skygatehub.common.enums.TrangThaiDatBan;
import com.example.skygatehub.common.enums.TrangThaiDon;
import com.example.skygatehub.common.enums.TrangThaiQuyenLoi;
import com.example.skygatehub.common.enums.TrangThaiThanhToanDon;
import com.example.skygatehub.dto.dining.MealScheduleRow;
import com.example.skygatehub.dto.dining.TableMapCell;
import com.example.skygatehub.entity.account.NguoiDung;
import com.example.skygatehub.entity.checkin.CheckIn;
import com.example.skygatehub.entity.dining.BanAn;
import com.example.skygatehub.entity.dining.CaAn;
import com.example.skygatehub.entity.dining.DatBan;
import com.example.skygatehub.entity.order.ChiTietDon;
import com.example.skygatehub.entity.order.DonDat;
import com.example.skygatehub.entity.order.QuyenLoiDon;
import com.example.skygatehub.repository.account.NguoiDungRepository;
import com.example.skygatehub.repository.checkin.CheckInRepository;
import com.example.skygatehub.repository.dining.BanAnRepository;
import com.example.skygatehub.repository.dining.CaAnRepository;
import com.example.skygatehub.repository.dining.DatBanRepository;
import com.example.skygatehub.repository.dining.DichVuCaAnRepository;
import com.example.skygatehub.repository.order.ChiTietDonRepository;
import com.example.skygatehub.repository.order.QuyenLoiDonRepository;
import com.example.skygatehub.service.dining.TableAssignmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
    private final QuyenLoiDonRepository quyenLoiDonRepository;
    private final DichVuCaAnRepository dichVuCaAnRepository;
    private final ChiTietDonRepository chiTietDonRepository;

    private static final int GHI_CHU_TOI_DA = 500;

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

        // Tạo map: maBanAn -> DatBan; bàn có nhiều lượt thì chọn lượt theo mức ưu tiên
        Map<String, DatBan> tableToBooking = new HashMap<>();
        for (DatBan db : bookingsOnDate) {
            if (db.getBanAn() != null && db.getTrangThai() != TrangThaiDatBan.DA_HUY) {
                tableToBooking.merge(db.getBanAn().getMaBanAn(), db, TableAssignmentServiceImpl::pickBooking);
            }
        }

        // Hôm nay: khách đang ngồi (kể cả ca khác hoặc lượt quên trả từ hôm trước) vẫn phải hiện để trả bàn
        boolean laHomNay = LocalDate.now().equals(date);
        if (laHomNay) {
            for (DatBan db : datBanRepository.findByTrangThai(TrangThaiDatBan.DA_CHECK_IN)) {
                if (db.getBanAn() != null) {
                    tableToBooking.merge(db.getBanAn().getMaBanAn(), db, TableAssignmentServiceImpl::pickBooking);
                }
            }
        }

        List<TableMapCell> cells = new ArrayList<>();
        for (BanAn b : allTables) {
            TableMapCell cell = TableMapCell.builder()
                    .id(b.getId())
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

                    if (db.getTrangThai() == TrangThaiDatBan.DA_CHECK_IN) {
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
                } else if (laHomNay && b.getTrangThai() == TrangThaiBanAn.DANG_SU_DUNG) {
                    cell.setTrangThaiHienThi("CAN_GIAI_PHONG");
                    cell.setCssBadge("bg-dark");
                    cell.setBadgeText("Cần giải phóng");
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

    private static String maCaAnCua(DatBan db) {
        return (db.getDichVuCaAn() != null && db.getDichVuCaAn().getCaAn() != null)
                ? db.getDichVuCaAn().getCaAn().getMaCaAn() : null;
    }

    // Ưu tiên: DA_CHECK_IN > chờ nhận bàn (ca bắt đầu sớm nhất) > DA_HOAN_THANH
    private static DatBan pickBooking(DatBan a, DatBan b) {
        int rankA = bookingRank(a);
        int rankB = bookingRank(b);
        if (rankA != rankB) {
            return rankA < rankB ? a : b;
        }
        if (rankA == 1) {
            LocalTime timeA = gioBatDau(a);
            LocalTime timeB = gioBatDau(b);
            if (timeA == null) return timeB == null ? a : b;
            if (timeB == null) return a;
            return timeB.isBefore(timeA) ? b : a;
        }
        return a;
    }

    private static int bookingRank(DatBan db) {
        if (db.getTrangThai() == TrangThaiDatBan.DA_CHECK_IN) return 0;
        if (db.getTrangThai() == TrangThaiDatBan.DA_HOAN_THANH) return 2;
        return 1;
    }

    private static LocalTime gioBatDau(DatBan db) {
        return (db.getDichVuCaAn() != null && db.getDichVuCaAn().getCaAn() != null)
                ? db.getDichVuCaAn().getCaAn().getGioBatDau() : null;
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
                .filter(b -> b.getTrangThai() == TrangThaiDatBan.CHO_XAC_NHAN || b.getTrangThai() == TrangThaiDatBan.DA_XAC_NHAN)
                .sorted(Comparator.comparing(DatBan::getMaDatBan))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BanAn> getAvailableTablesForBooking(String maDatBan) {
        DatBan db = datBanRepository.findByMaDatBan(maDatBan)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn đặt bàn: " + maDatBan));

        String caAnCode = maCaAnCua(db);
        if (caAnCode == null) {
            throw new IllegalStateException("Đơn đặt bàn [" + maDatBan + "] chưa có ca ăn, không thể tìm bàn trống.");
        }

        return banAnRepository.findAvailableTables(db.getSoKhach(), db.getNgayAn(), caAnCode);
    }

    @Override
    public void assignTableToBooking(String maDatBan, String maBanAn, String maNhanVien, String ghiChu) {
        DatBan db = datBanRepository.findByMaDatBan(maDatBan)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn đặt bàn: " + maDatBan));

        BanAn ba = banAnRepository.findByMaBanAn(maBanAn)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy bàn ăn: " + maBanAn));

        DonDat don = db.getDonDat();
        if (don.getTrangThaiDon() == TrangThaiDon.DA_HUY) {
            throw new IllegalStateException("Đơn [" + don.getMaDonHang() + "] đã bị hủy, không thể xếp bàn.");
        }

        if (db.getTrangThai() != TrangThaiDatBan.CHO_XAC_NHAN && db.getTrangThai() != TrangThaiDatBan.DA_XAC_NHAN) {
            throw new IllegalStateException("Đơn đặt bàn [" + maDatBan + "] đang ở trạng thái "
                    + db.getTrangThai() + ", không thể xếp bàn.");
        }

        if (ba.getTrangThai() == TrangThaiBanAn.BAO_TRI) {
            throw new IllegalStateException("Bàn [" + ba.getSoBan() + "] đang bảo trì, không thể phân!");
        }

        if (db.getSoKhach() != null && ba.getSucChua() != null && ba.getSucChua() < db.getSoKhach()) {
            throw new IllegalStateException("Bàn " + ba.getSoBan() + " chỉ có " + ba.getSucChua()
                    + " chỗ, lượt đặt có " + db.getSoKhach() + " khách.");
        }

        String maCaAn = maCaAnCua(db);
        if (maCaAn == null) {
            throw new IllegalStateException("Đơn đặt bàn [" + maDatBan + "] chưa có ca ăn, không thể xếp bàn.");
        }
        if (datBanRepository.countTrungBan(ba.getMaBanAn(), db.getNgayAn(), maCaAn, db.getMaDatBan()) > 0) {
            throw new IllegalStateException("Bàn " + ba.getSoBan() + " đã có lượt đặt khác trong ca "
                    + maCaAn + " ngày " + db.getNgayAn() + ".");
        }

        db.setBanAn(ba);
        if (db.getTrangThai() == TrangThaiDatBan.CHO_XAC_NHAN) {
            db.setTrangThai(TrangThaiDatBan.DA_XAC_NHAN);
        }
        if (ghiChu != null && !ghiChu.trim().isEmpty()) {
            String moi = ghiChu.trim();
            String cu = db.getGhiChu();
            String gop = (cu == null || cu.trim().isEmpty()) ? moi : cu + " | " + moi;
            db.setGhiChu(gop.length() > GHI_CHU_TOI_DA ? gop.substring(0, GHI_CHU_TOI_DA) : gop);
        }
        try {
            datBanRepository.saveAndFlush(db);
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalStateException("Bàn " + ba.getSoBan()
                    + " vừa được xếp cho lượt khác, vui lòng chọn bàn khác.", ex);
        }
        log.info("[ASSIGN-TABLE] Phân bàn {} thành công cho đơn đặt bàn {}", ba.getSoBan(), maDatBan);
    }

    @Override
    public void unassignTable(String maDatBan) {
        DatBan db = datBanRepository.findByMaDatBan(maDatBan)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn đặt bàn: " + maDatBan));

        if (db.getTrangThai() != TrangThaiDatBan.CHO_XAC_NHAN && db.getTrangThai() != TrangThaiDatBan.DA_XAC_NHAN) {
            throw new IllegalStateException("Đơn đặt bàn [" + maDatBan + "] đang ở trạng thái "
                    + db.getTrangThai() + ", không thể hủy xếp bàn!");
        }

        if (db.getBanAn() == null) {
            throw new IllegalStateException("Đơn đặt bàn [" + maDatBan + "] chưa được xếp bàn.");
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

        DonDat don = db.getDonDat();
        if (don.getTrangThaiDon() == TrangThaiDon.DA_HUY) {
            throw new IllegalStateException("Đơn [" + don.getMaDonHang() + "] đã bị hủy, không thể Check-in.");
        }
        if (don.getTrangThaiThanhToan() == TrangThaiThanhToanDon.CHUA_THANH_TOAN
                || don.getTrangThaiThanhToan() == TrangThaiThanhToanDon.DA_HOAN_TIEN) {
            throw new IllegalStateException("Đơn [" + don.getMaDonHang() + "] chưa đặt cọc, không thể Check-in.");
        }

        if (db.getBanAn() == null) {
            throw new IllegalStateException("Đơn đặt bàn [" + maDatBan + "] chưa được xếp bàn vật lý. Vui lòng xếp bàn trước khi Check-in!");
        }

        if (db.getTrangThai() == TrangThaiDatBan.DA_CHECK_IN) {
            throw new IllegalStateException("Đơn đặt bàn [" + maDatBan + "] đã hoàn tất Check-in trước đó.");
        }

        if (db.getTrangThai() == TrangThaiDatBan.DA_HUY) {
            throw new IllegalStateException("Đơn đặt bàn [" + maDatBan + "] đã bị hủy, không thể Check-in.");
        }

        if (db.getTrangThai() == TrangThaiDatBan.DA_HOAN_THANH) {
            throw new IllegalStateException("Đơn đặt bàn [" + maDatBan + "] đã hoàn tất, không thể Check-in.");
        }

        if (db.getTrangThai() != TrangThaiDatBan.CHO_XAC_NHAN && db.getTrangThai() != TrangThaiDatBan.DA_XAC_NHAN) {
            throw new IllegalStateException("Đơn đặt bàn [" + maDatBan + "] đang ở trạng thái "
                    + db.getTrangThai() + ", không thể Check-in.");
        }

        if (!LocalDate.now().equals(db.getNgayAn())) {
            throw new IllegalStateException("Lượt đặt bàn dành cho ngày " + db.getNgayAn()
                    + ", không check-in hôm nay được.");
        }

        BanAn ba = db.getBanAn();
        if (ba.getTrangThai() == TrangThaiBanAn.BAO_TRI) {
            throw new IllegalStateException("Bàn [" + ba.getSoBan() + "] đang bảo trì, không thể Check-in.");
        }

        List<DatBan> dangAn = datBanRepository.findByBanAn_MaBanAnAndTrangThaiAndMaDatBanNot(
                ba.getMaBanAn(), TrangThaiDatBan.DA_CHECK_IN, maDatBan);
        if (!dangAn.isEmpty()) {
            throw new IllegalStateException("Bàn " + ba.getSoBan() + " đang có khách của lượt "
                    + dangAn.get(0).getMaDatBan() + ", hãy hoàn tất bàn trước.");
        }

        NguoiDung nv = null;
        if (maNhanVien != null && !maNhanVien.trim().isEmpty()) {
            nv = nguoiDungRepository.findByMaNguoiDung(maNhanVien.trim()).orElse(null);
        }
        if (nv == null) {
            throw new IllegalStateException("Không xác định được nhân viên thực hiện check-in.");
        }

        // Cập nhật trạng thái đặt bàn
        db.setTrangThai(TrangThaiDatBan.DA_CHECK_IN);
        datBanRepository.save(db);

        tieuThuQuyenLoiCombo(db);

        // Đổi trạng thái bàn vật lý sang Đang sử dụng
        ba.setTrangThai(TrangThaiBanAn.DANG_SU_DUNG);
        banAnRepository.save(ba);

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

    /**
     * Đặt bàn từ combo gộp nhiều suất cùng ca: check-in là dùng hết quyền lợi ăn uống của ca đó.
     */
    private void tieuThuQuyenLoiCombo(DatBan db) {
        if (db.getQuyenLoiDon() == null) {
            return;
        }
        String maCaAn = maCaAnCua(db);
        if (maCaAn == null) {
            return;
        }
        List<QuyenLoiDon> quyenLois = quyenLoiDonRepository.findByChiTietDon_DonDat_MaDonDatAndTrangThai(
                db.getDonDat().getMaDonDat(), TrangThaiQuyenLoi.HIEU_LUC);
        for (QuyenLoiDon ql : quyenLois) {
            if (!"AN_UONG".equals(ql.getLoaiThanhPhanChup()) || !Boolean.TRUE.equals(ql.getTheoDoiSuDung())) {
                continue;
            }
            if (ql.getThanhPhanDichVu() == null || ql.getThanhPhanDichVu().getDichVuAnUong() == null) {
                continue;
            }
            String maMon = ql.getThanhPhanDichVu().getDichVuAnUong().getMaDichVu();
            if (dichVuCaAnRepository.findByDichVuAnUong_MaDichVuAndCaAn_MaCaAn(maMon, maCaAn).isEmpty()) {
                continue;
            }
            ql.setSoLuongDaDung(ql.getSoLuongToiDa());
            ql.setTrangThai(TrangThaiQuyenLoi.DA_DUNG_HET);
            quyenLoiDonRepository.save(ql);
        }
    }

    @Override
    public String completeTable(String maDatBan, String maNhanVien) {
        DatBan db = datBanRepository.findByMaDatBan(maDatBan)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn đặt bàn: " + maDatBan));

        if (db.getTrangThai() != TrangThaiDatBan.DA_CHECK_IN) {
            throw new IllegalStateException("Đơn đặt bàn [" + maDatBan + "] chưa Check-in (trạng thái "
                    + db.getTrangThai() + "), không thể hoàn tất bàn.");
        }

        db.setTrangThai(TrangThaiDatBan.DA_HOAN_THANH);
        datBanRepository.save(db);

        BanAn ba = db.getBanAn();
        if (ba != null && ba.getTrangThai() == TrangThaiBanAn.DANG_SU_DUNG) {
            boolean conKhach = !datBanRepository.findByBanAn_MaBanAnAndTrangThaiAndMaDatBanNot(
                    ba.getMaBanAn(), TrangThaiDatBan.DA_CHECK_IN, maDatBan).isEmpty();
            if (!conKhach) {
                ba.setTrangThai(TrangThaiBanAn.SAN_SANG);
                banAnRepository.save(ba);
            }
        }

        log.info("[COMPLETE-TABLE] Trả bàn thành công đơn đặt bàn {}", maDatBan);
        return (ba != null) ? ba.getSoBan() : "N/A";
    }

    @Override
    @Transactional(readOnly = true)
    public List<MealScheduleRow> getMealSchedule(LocalDate targetDate) {
        LocalDate date = (targetDate != null) ? targetDate : LocalDate.now();
        List<CaAn> caAnList = sortedCaAn();
        List<DatBan> allBookings = datBanRepository.findByNgayAn(date);
        Map<String, List<ChiTietDon>> chiTietTheoDon = new HashMap<>();
        Map<String, List<QuyenLoiDon>> quyenLoiTheoDon = new HashMap<>();
        Map<String, Boolean> linkCache = new HashMap<>();

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

            for (DatBan db : forThisShift) {
                List<String> mon = tinhMonCuaDatBan(db, ca.getMaCaAn(), chiTietTheoDon, quyenLoiTheoDon, linkCache);
                if (!mon.isEmpty()) {
                    row.getMonTheoDatBan().put(db.getMaDatBan(), mon);
                }
            }

            schedule.add(row);
        }

        return schedule;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CaAn> getAllCaAn() {
        return sortedCaAn();
    }

    // Ca theo giờ bắt đầu, ca chưa có giờ xuống cuối
    private List<CaAn> sortedCaAn() {
        return caAnRepository.findAll().stream()
                .sorted(Comparator.comparing(CaAn::getGioBatDau, Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(Collectors.toList());
    }

    /**
     * Món của một lượt đặt bàn: dòng ăn uống của đơn và suất ăn trong combo, chỉ lấy món có link tới ca đó.
     * Dữ liệu của đơn được nạp một lần rồi dùng lại cho các lượt cùng đơn.
     */
    private List<String> tinhMonCuaDatBan(DatBan db, String maCaAn,
                                          Map<String, List<ChiTietDon>> chiTietTheoDon,
                                          Map<String, List<QuyenLoiDon>> quyenLoiTheoDon,
                                          Map<String, Boolean> linkCache) {
        List<String> mon = new ArrayList<>();
        if (db.getDonDat() == null || db.getNgayAn() == null) {
            return mon;
        }
        String maDonDat = db.getDonDat().getMaDonDat();

        for (ChiTietDon ct : chiTietTheoDon.computeIfAbsent(maDonDat, chiTietDonRepository::findByDonDat_MaDonDat)) {
            if (ct.getLoaiDichVu() == LoaiDichVuDon.AN_UONG
                    && ct.getTrangThai() == TrangThaiChiTiet.HIEU_LUC
                    && db.getNgayAn().equals(ct.getNgaySuDung())
                    && ct.getDichVuAnUong() != null
                    && coLinkCa(ct.getDichVuAnUong().getMaDichVu(), maCaAn, linkCache)) {
                mon.add(ct.getTenSanPham() + " x " + ct.getSoLuong());
            }
        }

        for (QuyenLoiDon ql : quyenLoiTheoDon.computeIfAbsent(maDonDat, quyenLoiDonRepository::findByChiTietDon_DonDat_MaDonDat)) {
            if ("AN_UONG".equals(ql.getLoaiThanhPhanChup())
                    && ql.getTrangThai() != TrangThaiQuyenLoi.DA_HUY
                    && ql.getThanhPhanDichVu() != null
                    && ql.getThanhPhanDichVu().getDichVuAnUong() != null
                    && ql.getChiTietDon() != null
                    && db.getNgayAn().equals(ql.getChiTietDon().getNgaySuDung())
                    && coLinkCa(ql.getThanhPhanDichVu().getDichVuAnUong().getMaDichVu(), maCaAn, linkCache)) {
                mon.add(ql.getTenThanhPhanChup() + " x " + ql.getSoLuongToiDa());
            }
        }
        return mon;
    }

    private boolean coLinkCa(String maMon, String maCaAn, Map<String, Boolean> linkCache) {
        return linkCache.computeIfAbsent(maMon + "|" + maCaAn,
                k -> dichVuCaAnRepository.findByDichVuAnUong_MaDichVuAndCaAn_MaCaAn(maMon, maCaAn).isPresent());
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
