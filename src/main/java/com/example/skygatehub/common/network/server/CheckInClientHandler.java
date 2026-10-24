package com.example.skygatehub.common.network.server;

import com.example.skygatehub.common.enums.LoaiCheckIn;
import com.example.skygatehub.common.enums.TrangThaiBanAn;
import com.example.skygatehub.common.enums.TrangThaiCheckIn;
import com.example.skygatehub.common.enums.TrangThaiDatBan;
import com.example.skygatehub.common.enums.TrangThaiVe;
import com.example.skygatehub.entity.account.NguoiDung;
import com.example.skygatehub.entity.checkin.CheckIn;
import com.example.skygatehub.entity.dining.DatBan;
import com.example.skygatehub.entity.order.DonDat;
import com.example.skygatehub.entity.order.Ve;
import com.example.skygatehub.entity.roombooking.DatPhong;
import com.example.skygatehub.repository.account.NguoiDungRepository;
import com.example.skygatehub.repository.checkin.CheckInRepository;
import com.example.skygatehub.repository.dining.DatBanRepository;
import com.example.skygatehub.repository.order.DonDatRepository;
import com.example.skygatehub.repository.order.VeRepository;
import com.example.skygatehub.repository.roombooking.DatPhongRepository;
import com.example.skygatehub.service.room.RoomOperationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Luồng phục vụ từng Client kết nối tới CheckInSocketServer (Tiêu chí 5.1, 5.2, 5.3, 5.4).
 * Chạy trên luồng riêng được cấp từ ExecutorService.
 */
@Slf4j
public class CheckInClientHandler implements Runnable {

    private static final AtomicInteger CLIENT_COUNTER = new AtomicInteger(1);

    private final Socket clientSocket;
    private final DonDatRepository donDatRepository;
    private final VeRepository veRepository;
    private final DatPhongRepository datPhongRepository;
    private final DatBanRepository datBanRepository;
    private final CheckInRepository checkInRepository;
    private final NguoiDungRepository nguoiDungRepository;
    private final RoomOperationService roomOperationService;
    private final PlatformTransactionManager transactionManager;

    private final int clientId;
    private String stationName = "ANONYMOUS";
    private boolean authenticated = false;

    public CheckInClientHandler(
            Socket socket,
            DonDatRepository donDatRepository,
            VeRepository veRepository,
            DatPhongRepository datPhongRepository,
            DatBanRepository datBanRepository,
            CheckInRepository checkInRepository,
            NguoiDungRepository nguoiDungRepository,
            RoomOperationService roomOperationService,
            PlatformTransactionManager transactionManager) {
        this.clientSocket = socket;
        this.donDatRepository = donDatRepository;
        this.veRepository = veRepository;
        this.datPhongRepository = datPhongRepository;
        this.datBanRepository = datBanRepository;
        this.checkInRepository = checkInRepository;
        this.nguoiDungRepository = nguoiDungRepository;
        this.roomOperationService = roomOperationService;
        this.transactionManager = transactionManager;
        this.clientId = CLIENT_COUNTER.getAndIncrement();
    }

    @Override
    public void run() {
        String threadName = Thread.currentThread().getName();
        log.info("[THREAD: {}] [CLIENT #{}] Khởi tạo kết nối từ {}", threadName, clientId, clientSocket.getRemoteSocketAddress());

        try (
            BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8));
            PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true, StandardCharsets.UTF_8)
        ) {
            writer.println("200 WELCOME SkyGateHub Check-In Server v1.0. Ready for commands.");

            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                log.info("[THREAD: {}] [CLIENT #{} - {}] Lệnh nhận: {}", threadName, clientId, stationName, line);

                String[] parts = line.contains("|") ? line.split("\\|") : line.split("\\s+");
                String command = parts[0].toUpperCase();

                if ("QUIT".equals(command) || "EXIT".equals(command)) {
                    writer.println("200 BYE Goodbye!");
                    break;
                }

                String response = handleCommand(command, parts);
                writer.println(response);
            }
        } catch (Exception e) {
            log.error("[THREAD: {}] [CLIENT #{}] Lỗi giao tiếp socket: {}", threadName, clientId, e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (Exception ignored) {}
            log.info("[THREAD: {}] [CLIENT #{}] Ngắt kết nối thành công.", threadName, clientId);
        }
    }

    private String handleCommand(String command, String[] parts) {
        switch (command) {
            case "AUTH":
                if (parts.length < 2) return "400 ERROR Thiếu tên trạm: Cú pháp: AUTH <STATION_NAME>";
                this.stationName = parts[1];
                this.authenticated = true;
                return "200 AUTH_OK Trạm [" + stationName + "] đã xác thực thành công.";

            case "LOOKUP":
                if (parts.length < 2) return "400 ERROR Thiếu mã tra cứu: Cú pháp: LOOKUP <CODE_OR_QR>";
                return processLookup(parts[1]);

            case "CHECKIN_VE":
                if (!authenticated) return "401 UNAUTHORIZED Vui lòng AUTH trước khi check-in.";
                if (parts.length < 2) return "400 ERROR Thiếu mã vé: Cú pháp: CHECKIN_VE <MA_VE_OR_CHUOI>";
                return xuLyCheckinVe(parts[1]);

            case "CHECKIN_PHONG":
                if (!authenticated) return "401 UNAUTHORIZED Vui lòng AUTH trước khi check-in.";
                if (parts.length < 2) return "400 ERROR Thiếu mã đặt phòng: Cú pháp: CHECKIN_PHONG <MA_DAT_PHONG>";
                String maNhanVien = (parts.length >= 3) ? parts[2] : null;
                return processCheckinPhong(parts[1], maNhanVien);

            case "CHECKIN_AN":
                if (!authenticated) return "401 UNAUTHORIZED Vui lòng AUTH trước khi check-in.";
                if (parts.length < 2) return "400 ERROR Thiếu mã đặt bàn: Cú pháp: CHECKIN_AN <MA_DAT_BAN>";
                return processCheckinAn(parts[1]);

            case "STATS":
                long totalCheckins = checkInRepository.count();
                return "200 STATS TongLuotCheckIn=" + totalCheckins + " Tram=" + stationName;

            default:
                return "404 UNKNOWN Lệnh không hợp lệ. Các lệnh hỗ trợ: AUTH, LOOKUP, CHECKIN_VE, CHECKIN_PHONG, CHECKIN_AN, STATS, QUIT";
        }
    }

    private String processLookup(String code) {
        // Tra cứu theo đơn hàng hoặc mã QR
        Optional<DonDat> donOpt = donDatRepository.findByMaDonHang(code);
        if (donOpt.isEmpty()) {
            donOpt = donDatRepository.findByMaQr(code);
        }
        if (donOpt.isEmpty()) {
            donOpt = donDatRepository.findByMaDonDat(code);
        }

        if (donOpt.isPresent()) {
            DonDat d = donOpt.get();
            return String.format("200 OK_DON MaDon=%s Khach=%s SDT=%s TongTien=%s TrangThai=%s QR=%s",
                    d.getMaDonHang(), d.getHoTenKhach(), d.getSdtKhach(), d.getTongTien(), d.getTrangThaiDon(), d.getMaQr());
        }

        // Tra cứu theo vé
        Optional<Ve> veOpt = veRepository.findByMaVe(code);
        if (veOpt.isEmpty()) {
            veOpt = veRepository.findByMaVeChuoi(code);
        }
        if (veOpt.isPresent()) {
            Ve v = veOpt.get();
            return String.format("200 OK_VE MaVe=%s LoaiVe=%s Ngay=%s TrangThai=%s",
                    v.getMaVe(), v.getLoaiVe().getTenLoaiVe(), v.getNgayThamQuan(), v.getTrangThai());
        }

        // Tra cứu theo phòng
        Optional<DatPhong> dpOpt = datPhongRepository.findByMaDatPhong(code);
        if (dpOpt.isPresent()) {
            DatPhong dp = dpOpt.get();
            return String.format("200 OK_PHONG MaDatPhong=%s LoaiPhong=%s NgayNhan=%s NgayTra=%s SoPhong=%d TrangThai=%s",
                    dp.getMaDatPhong(), dp.getLoaiPhong().getTenLoaiPhong(), dp.getNgayNhanPhong(), dp.getNgayTraPhong(),
                    dp.getSoLuongPhong(), dp.getTrangThai());
        }

        // Tra cứu theo bàn ăn
        Optional<DatBan> dbOpt = datBanRepository.findByMaDatBan(code);
        if (dbOpt.isPresent()) {
            DatBan db = dbOpt.get();
            return String.format("200 OK_AN MaDatBan=%s NgayAn=%s SoKhach=%d TrangThai=%s",
                    db.getMaDatBan(), db.getNgayAn(), db.getSoKhach(), db.getTrangThai());
        }

        return "404 NOT_FOUND Không tìm thấy dịch vụ hoặc đơn đặt với mã: " + code;
    }

    /**
     * Check-in vé dùng Pessimistic Lock chống quét trùng (Tiêu chí 5.2).
     */
    private String xuLyCheckinVe(String maVeHoacChuoi) {
        TransactionTemplate mauGiaoDich = new TransactionTemplate(transactionManager);
        return mauGiaoDich.execute(trangThaiGiaoDich -> {
            Optional<Ve> veOptional = veRepository.findByMaVeChuoiWithLock(maVeHoacChuoi);
            if (veOptional.isEmpty()) {
                veOptional = veRepository.findByMaVe(maVeHoacChuoi);
            }
            if (veOptional.isEmpty()) {
                return "404 NOT_FOUND Mã vé không tồn tại: " + maVeHoacChuoi;
            }

            Ve ve = veOptional.get();
            if (ve.getTrangThai() == TrangThaiVe.DA_SU_DUNG) {
                return "409 CONFLICT_ALREADY_USED Vé [" + ve.getMaVe() + "] đã được sử dụng lúc " + ve.getThoiGianSuDung();
            }
            if (ve.getTrangThai() == TrangThaiVe.DA_HUY) {
                return "410 GONE Vé [" + ve.getMaVe() + "] đã bị hủy, không thể check-in.";
            }

            // Cập nhật trạng thái vé
            ve.setTrangThai(TrangThaiVe.DA_SU_DUNG);
            ve.setThoiGianSuDung(LocalDateTime.now());
            veRepository.save(ve);

            // Tạo bản ghi CheckIn
            CheckIn banGhiCheckIn = new CheckIn();
            banGhiCheckIn.setMaCheckIn("CI-VE-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
            banGhiCheckIn.setDonDat(ve.getChiTietDon().getDonDat());
            banGhiCheckIn.setLoaiCheckIn(LoaiCheckIn.VE);
            banGhiCheckIn.setVe(ve);

            // Gán nhân viên mặc định (User đầu tiên hoặc Quản trị)
            NguoiDung nhanVien = nguoiDungRepository.findAll().stream().findFirst().orElse(null);
            banGhiCheckIn.setNhanVien(nhanVien);
            banGhiCheckIn.setSoLuong(1);
            banGhiCheckIn.setThoiGianCheckIn(LocalDateTime.now());
            banGhiCheckIn.setTrangThai(TrangThaiCheckIn.DA_CHECK_IN);
            banGhiCheckIn.setGhiChu("Check-in qua Socket tại trạm: " + stationName);
            checkInRepository.save(banGhiCheckIn);

            log.info("[CHECKIN-VE-SUCCESS] Trạm {} check-in thành công vé: {}", stationName, ve.getMaVe());
            return "200 CHECKIN_SUCCESS MaCheckIn=" + banGhiCheckIn.getMaCheckIn() + " MaVe=" + ve.getMaVe() + " LoaiVe=" + ve.getLoaiVe().getTenLoaiVe();
        });
    }

    /**
     * Tích hợp Trạm 2: Lễ Tân Lưu Trú qua RoomOperationService (Tiêu chí 5.3, 5.4).
     */
    private String processCheckinPhong(String codeOrOrder, String maNhanVien) {
        try {
            String targetMaDatPhong = codeOrOrder;
            Optional<DatPhong> dpOpt = datPhongRepository.findByMaDatPhong(targetMaDatPhong);

            if (dpOpt.isEmpty()) {
                // Thử tra theo mã đơn hàng
                Optional<DonDat> donOpt = donDatRepository.findByMaDonHang(codeOrOrder);
                if (donOpt.isPresent()) {
                    List<DatPhong> dps = datPhongRepository.findByChiTietDon_DonDat_MaDonDat(donOpt.get().getMaDonDat());
                    if (!dps.isEmpty()) {
                        targetMaDatPhong = dps.get(0).getMaDatPhong();
                        dpOpt = Optional.of(dps.get(0));
                    }
                }
            }

            if (dpOpt.isEmpty()) {
                return "404 NOT_FOUND Không tìm thấy đơn đặt phòng hoặc mã đơn: " + codeOrOrder;
            }

            String rooms = roomOperationService.checkInRoom(targetMaDatPhong, maNhanVien);
            log.info("[CHECKIN-PHONG-SUCCESS] Trạm {} check-in thành công đơn {}: Phòng [{}]", stationName, targetMaDatPhong, rooms);
            return "200 CHECKIN_PHONG_OK MaDatPhong=" + targetMaDatPhong + " SoPhong=" + rooms + " LoaiPhong=" + dpOpt.get().getLoaiPhong().getTenLoaiPhong();
        } catch (Exception ex) {
            log.warn("Lỗi check-in phòng qua socket: {}", ex.getMessage());
            return "409 CONFLICT " + ex.getMessage();
        }
    }

    private String processCheckinAn(String maDatBan) {
        TransactionTemplate tx = new TransactionTemplate(transactionManager);
        return tx.execute(status -> {
            Optional<DatBan> dbOpt = datBanRepository.findByMaDatBan(maDatBan);
            if (dbOpt.isEmpty()) return "404 NOT_FOUND Mã đặt bàn không tồn tại: " + maDatBan;

            DatBan db = dbOpt.get();
            if (db.getTrangThai() == TrangThaiDatBan.DA_CHECK_IN) {
                return "409 CONFLICT_ALREADY_SEATED Bàn [" + maDatBan + "] đang được sử dụng.";
            }
            if (db.getTrangThai() == TrangThaiDatBan.DA_HUY) {
                return "410 GONE Đặt bàn [" + maDatBan + "] đã bị hủy.";
            }
            if (db.getTrangThai() == TrangThaiDatBan.DA_HOAN_THANH) {
                return "409 CONFLICT Đặt bàn [" + maDatBan + "] đã hoàn tất.";
            }
            if (db.getBanAn() == null) {
                return "409 CONFLICT Đặt bàn [" + maDatBan + "] chưa được xếp bàn.";
            }
            if (db.getBanAn().getTrangThai() == TrangThaiBanAn.BAO_TRI) {
                return "409 CONFLICT Bàn [" + db.getBanAn().getSoBan() + "] đang bảo trì.";
            }
            if (!LocalDate.now().equals(db.getNgayAn())) {
                return "409 CONFLICT Đặt bàn [" + maDatBan + "] dành cho ngày " + db.getNgayAn() + ".";
            }
            if (!datBanRepository.findByBanAn_MaBanAnAndTrangThaiAndMaDatBanNot(
                    db.getBanAn().getMaBanAn(), TrangThaiDatBan.DA_CHECK_IN, maDatBan).isEmpty()) {
                return "409 CONFLICT Bàn [" + db.getBanAn().getSoBan() + "] đang có khách của lượt khác.";
            }

            db.setTrangThai(TrangThaiDatBan.DA_CHECK_IN);
            db.getBanAn().setTrangThai(TrangThaiBanAn.DANG_SU_DUNG);
            datBanRepository.save(db);

            CheckIn ci = new CheckIn();
            ci.setMaCheckIn("CI-AN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
            ci.setDonDat(db.getDonDat());
            ci.setLoaiCheckIn(LoaiCheckIn.AN_UONG);
            ci.setDatBan(db);

            NguoiDung nv = nguoiDungRepository.findAll().stream().findFirst().orElse(null);
            ci.setNhanVien(nv);
            ci.setSoLuong(db.getSoKhach());
            ci.setThoiGianCheckIn(LocalDateTime.now());
            ci.setTrangThai(TrangThaiCheckIn.DA_CHECK_IN);
            ci.setGhiChu("Check-in bàn ăn qua Socket tại trạm: " + stationName);
            checkInRepository.save(ci);

            log.info("[CHECKIN-AN-SUCCESS] Trạm {} check-in bàn thành công: {}", stationName, maDatBan);
            return "200 CHECKIN_SUCCESS MaCheckIn=" + ci.getMaCheckIn() + " MaDatBan=" + maDatBan + " SoKhach=" + db.getSoKhach();
        });
    }
}
