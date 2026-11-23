package com.example.skygatehub.service.room;

import com.example.skygatehub.common.enums.LoaiDichVuDon;
import com.example.skygatehub.common.enums.TrangThaiChiTiet;
import com.example.skygatehub.common.enums.TrangThaiDatPhong;
import com.example.skygatehub.common.enums.TrangThaiDon;
import com.example.skygatehub.common.enums.TrangThaiPhong;
import com.example.skygatehub.common.enums.TrangThaiThanhToanDon;
import com.example.skygatehub.entity.account.NguoiDung;
import com.example.skygatehub.entity.order.ChiTietDon;
import com.example.skygatehub.entity.order.DonDat;
import com.example.skygatehub.entity.room.KhuLuuTru;
import com.example.skygatehub.entity.room.LoaiPhong;
import com.example.skygatehub.entity.room.PhanPhong;
import com.example.skygatehub.entity.room.Phong;
import com.example.skygatehub.entity.roombooking.DatPhong;
import com.example.skygatehub.repository.account.NguoiDungRepository;
import com.example.skygatehub.repository.order.ChiTietDonRepository;
import com.example.skygatehub.repository.order.DonDatRepository;
import com.example.skygatehub.repository.room.KhuLuuTruRepository;
import com.example.skygatehub.repository.room.LoaiPhongRepository;
import com.example.skygatehub.repository.room.PhanPhongRepository;
import com.example.skygatehub.repository.room.PhongRepository;
import com.example.skygatehub.repository.roombooking.DatPhongRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@TestPropertySource(properties = {
        "spring.jpa.show-sql=false",
        "logging.level.org.hibernate.SQL=WARN",
        "logging.level.org.hibernate.orm.jdbc.bind=WARN"
})
@Slf4j
public class ConcurrencyStressTest {

    @Autowired
    private RoomOperationService roomOperationService;

    @Autowired
    private PhongRepository phongRepository;

    @Autowired
    private PhanPhongRepository phanPhongRepository;

    @Autowired
    private DatPhongRepository datPhongRepository;

    @Autowired
    private ChiTietDonRepository chiTietDonRepository;

    @Autowired
    private DonDatRepository donDatRepository;

    @Autowired
    private LoaiPhongRepository loaiPhongRepository;

    @Autowired
    private KhuLuuTruRepository khuLuuTruRepository;

    @Autowired
    private NguoiDungRepository nguoiDungRepository;

    private static final int NUMBER_OF_THREADS = 10;
    private static final String MA_PHONG = "P101";
    private static final String MA_DAT_PHONG_PREFIX = "DP_TEST_";
    private static final String MA_NHAN_VIEN = "NV001";

    @BeforeEach
    void prepareData() {
        // 1. Dọn dẹp sạch mọi lịch phân phòng cũ của P101 để mỗi lần chạy test đều xuất phát từ trạng thái trống
        List<PhanPhong> oldAssignments = phanPhongRepository.findByPhong_MaPhong(MA_PHONG);
        if (!oldAssignments.isEmpty()) {
            phanPhongRepository.deleteAll(oldAssignments);
        }

        // 2. Đảm bảo nhân viên NV001 tồn tại
        if (nguoiDungRepository.findByMaNguoiDung(MA_NHAN_VIEN).isEmpty()) {
            NguoiDung nd = new NguoiDung();
            nd.setMaNguoiDung(MA_NHAN_VIEN);
            nd.setTenDangNhap("nv_test");
            nd.setMatKhauHash("123456");
            nd.setHoTen("Nhân viên Test");
            nd.setTrangThai(true);
            nguoiDungRepository.save(nd);
        }

        // 3. Đảm bảo Loại phòng DELUXE & Phòng P101 tồn tại và ở trạng thái SAN_SANG
        LoaiPhong loaiPhong = loaiPhongRepository.findByMaLoaiPhong("DELUXE").orElse(null);
        if (loaiPhong == null) {
            KhuLuuTru khu = khuLuuTruRepository.findAll().stream().findFirst().orElseGet(() -> {
                KhuLuuTru newKhu = new KhuLuuTru();
                newKhu.setTenKhu("FIVITEL Sông Ngân");
                newKhu.setHangSao(5);
                return khuLuuTruRepository.save(newKhu);
            });

            loaiPhong = new LoaiPhong();
            loaiPhong.setMaLoaiPhong("DELUXE");
            loaiPhong.setTenLoaiPhong("Phòng Deluxe");
            loaiPhong.setKhuLuuTru(khu);
            loaiPhong.setSucChuaNguoiLon(2);
            loaiPhong.setSucChuaTreEm(1);
            loaiPhong.setGiaCoBan(BigDecimal.valueOf(1000000));
            loaiPhong.setTrangThai(true);
            loaiPhong = loaiPhongRepository.save(loaiPhong);
        }

        final LoaiPhong finalLoaiPhong = loaiPhong;
        Phong phong = phongRepository.findByMaPhong(MA_PHONG).orElse(null);
        if (phong == null) {
            phong = new Phong();
            phong.setMaPhong(MA_PHONG);
            phong.setSoPhong(MA_PHONG);
            phong.setLoaiPhong(finalLoaiPhong);
            phong.setTrangThai(TrangThaiPhong.SAN_SANG);
            phongRepository.save(phong);
        } else if (phong.getTrangThai() != TrangThaiPhong.SAN_SANG) {
            phong.setTrangThai(TrangThaiPhong.SAN_SANG);
            phongRepository.save(phong);
        }

        // 4. Đảm bảo 10 đơn đặt phòng DP_TEST_1 -> DP_TEST_10 tồn tại và sẵn sàng tranh chấp
        LocalDate checkIn = LocalDate.now().plusDays(1);
        LocalDate checkOut = LocalDate.now().plusDays(2);

        for (int i = 1; i <= NUMBER_OF_THREADS; i++) {
            String maDp = MA_DAT_PHONG_PREFIX + i;
            String maDon = "DD_TEST_" + i;
            String maCt = "CT_TEST_" + i;

            if (datPhongRepository.findByMaDatPhong(maDp).isEmpty()) {
                DonDat donDat = donDatRepository.findByMaDonDat(maDon).orElseGet(() -> {
                    DonDat dd = new DonDat();
                    dd.setMaDonDat(maDon);
                    dd.setMaDonHang("DH_" + maDon);
                    dd.setHoTenKhach("Khách Test " + maDon);
                    dd.setSdtKhach("0905123456");
                    dd.setThoiGianDat(LocalDateTime.now());
                    dd.setTrangThaiDon(TrangThaiDon.DA_XAC_NHAN);
                    dd.setTrangThaiThanhToan(TrangThaiThanhToanDon.DA_THANH_TOAN);
                    dd.setTongTien(BigDecimal.valueOf(1000000));
                    return donDatRepository.save(dd);
                });

                ChiTietDon ctd = chiTietDonRepository.findByMaChiTiet(maCt).orElseGet(() -> {
                    ChiTietDon c = new ChiTietDon();
                    c.setMaChiTiet(maCt);
                    c.setDonDat(donDat);
                    c.setMaDonDat(donDat.getMaDonDat());
                    c.setLoaiDichVu(LoaiDichVuDon.PHONG);
                    c.setLoaiPhong(finalLoaiPhong);
                    c.setTenSanPham(finalLoaiPhong.getTenLoaiPhong());
                    c.setSoLuong(1);
                    c.setDonGia(finalLoaiPhong.getGiaCoBan());
                    c.setNgaySuDung(checkIn);
                    c.setTrangThai(TrangThaiChiTiet.HIEU_LUC);
                    return chiTietDonRepository.save(c);
                });

                DatPhong dp = new DatPhong();
                dp.setMaDatPhong(maDp);
                dp.setChiTietDon(ctd);
                dp.setLoaiPhong(finalLoaiPhong);
                dp.setNgayNhanPhong(checkIn);
                dp.setNgayTraPhong(checkOut);
                dp.setSoLuongPhong(1);
                dp.setSoKhach(2);
                dp.setTrangThai(TrangThaiDatPhong.DA_XAC_NHAN);
                datPhongRepository.save(dp);
            }
        }
    }

    @AfterEach
    void tearDown() {
//        // Dọn dẹp phân phòng test sau khi hoàn tất để lần test sau luôn độc lập
//        List<PhanPhong> assignments = phanPhongRepository.findByPhong_MaPhong(MA_PHONG);
//        if (!assignments.isEmpty()) {
//            phanPhongRepository.deleteAll(assignments);
//        }
        List<PhanPhong> assignments = phanPhongRepository.findByPhong_MaPhong(MA_PHONG);
        if (!assignments.isEmpty()) {
            phanPhongRepository.deleteAll(assignments);
        }

        for (int i = 1; i <= NUMBER_OF_THREADS; i++) {
            datPhongRepository.findByMaDatPhong(MA_DAT_PHONG_PREFIX + i).ifPresent(datPhongRepository::delete);
            chiTietDonRepository.findByMaChiTiet("CT_TEST_" + i).ifPresent(chiTietDonRepository::delete);
            donDatRepository.findByMaDonDat("DD_TEST_" + i).ifPresent(donDatRepository::delete);
        }
    }

    @Test
    @DisplayName("Kiểm thử 10 luồng đồng thời tranh chấp 1 phòng trống duy nhất")
    void testConcurrentRoomAssignmentWithPessimisticLock() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
        CountDownLatch readyLatch = new CountDownLatch(NUMBER_OF_THREADS);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch finishLatch = new CountDownLatch(NUMBER_OF_THREADS);


        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger businessBlockedCount = new AtomicInteger(0); // Bị chặn hợp lệ do phòng đã bị xếp
        AtomicInteger systemErrorCount = new AtomicInteger(0);     // Lỗi hệ thống/SQL (không mong muốn)

        for (int i = 1; i <= NUMBER_OF_THREADS; i++) {
            final String maDatPhong = MA_DAT_PHONG_PREFIX + i;

            executorService.submit(() -> {
                readyLatch.countDown();
                try {
                    startLatch.await();

                    roomOperationService.assignRoomToBooking(maDatPhong, MA_PHONG, MA_NHAN_VIEN);
                    successCount.incrementAndGet();
                } catch (IllegalStateException e) {
                    // ĐÚNG: Luồng sau lấy được khóa, kiểm tra thấy phòng P101 đã có lịch trùng nên từ chối
                    businessBlockedCount.incrementAndGet();
                    System.out.println("Thread [" + Thread.currentThread().getName() + "] Bi chan hop le (Phong da co lich trung)");
                } catch (Exception e) {
                    // SAI: Lỗi kỹ thuật (trùng khóa chính, deadlock, timeout...)
                    systemErrorCount.incrementAndGet();
                    System.err.println("Thread [" + Thread.currentThread().getName() + "] LOI HE THONG: " + e.getMessage());
                } finally {
                    finishLatch.countDown();
                }
            });
        }

        readyLatch.await();
        startLatch.countDown();
        finishLatch.await();
        executorService.shutdown();

        String report = String.format("""
                
                ================================================================================
                [THUC NGHIEM DA LUONG] KET QUA TRANH CHAP PHAN PHONG (PESSIMISTIC LOCK)
                ================================================================================
                - Tong so luong gui yeu cau dong thoi : %d luong
                - Ma phong tranh chap                 : %s
                - SO GIAO DICH THANH CONG             : %d (Da xep phong)
                - SO GIAO DICH BI CHAN HOP LE         : %d (Phong da co lich trung)
                - SO LOI HE THONG / SQL               : %d
                ================================================================================
                """, NUMBER_OF_THREADS, MA_PHONG, successCount.get(), businessBlockedCount.get(), systemErrorCount.get());

        System.out.println(report);
        log.info(report);

        // Khẳng định kết quả
        assertEquals(0, systemErrorCount.get(), "Không được phép xảy ra lỗi kỹ thuật/SQL!");
        assertEquals(1, successCount.get(), "Chỉ được phép đúng 1 đơn đặt thành công!");
        assertEquals(9, businessBlockedCount.get(), "9 đơn đặt còn lại phải bị từ chối do trùng lịch!");

        // Kiểm tra thực tế trong Database chỉ có duy nhất 1 bản ghi phân phòng cho P101
        List<PhanPhong> actualAssignments = phanPhongRepository.findByPhong_MaPhong(MA_PHONG);
        assertEquals(1, actualAssignments.size(), "Database chỉ được tồn tại duy nhất 1 bản ghi phân phòng cho P101!");

    }
}
