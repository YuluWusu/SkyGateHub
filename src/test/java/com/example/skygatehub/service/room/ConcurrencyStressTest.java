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
        // Dọn dẹp phân phòng test sau khi hoàn tất để lần test sau luôn độc lập
        List<PhanPhong> assignments = phanPhongRepository.findByPhong_MaPhong(MA_PHONG);
        if (!assignments.isEmpty()) {
            phanPhongRepository.deleteAll(assignments);
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
        AtomicInteger failureCount = new AtomicInteger(0);

        for (int i = 1; i <= NUMBER_OF_THREADS; i++) {
            final String maDatPhong = MA_DAT_PHONG_PREFIX + i;

            executorService.submit(() -> {
                readyLatch.countDown();
                try {
                    // Chờ tất cả 10 luồng sẵn sàng để kích hoạt đồng thời
                    startLatch.await();

                    // Gọi hàm phân phòng có chứa khóa bi quan @Lock(PESSIMISTIC_WRITE)
                    roomOperationService.assignRoomToBooking(maDatPhong, MA_PHONG, MA_NHAN_VIEN);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    // Cắt các luồng thất bại do xung đột khóa bi quan hoặc phòng đã bị chiếm
                    failureCount.incrementAndGet();
                    System.err.println("Thread [" + Thread.currentThread().getName() + "] Bị chặn bởi khóa: " + e.getMessage());
                } finally {
                    finishLatch.countDown();
                }
            });
        }

        readyLatch.await(); // Chờ 10 luồng chuẩn bị xong
        startLatch.countDown(); // Kích hoạt nổ đạn đồng thời 10 luồng
        finishLatch.await(); // Chờ toàn bộ 10 luồng hoàn tất
        executorService.shutdown();

        // Kiểm tra kết quả thực nghiệm
        String report = String.format("""
                
                ================================================================================
                [THỰC NGHIỆM ĐA LUỒNG] KẾT QUẢ TRANH CHẤP PHÂN PHÒNG (PESSIMISTIC LOCK)
                ================================================================================
                • Tổng số luồng gửi yêu cầu đồng thời : %d luồng
                • Mã phòng tranh chấp                 : %s
                • SỐ GIAO DỊCH THÀNH CÔNG             : %d (Đã xếp phòng)
                • SỐ GIAO DỊCH BỊ CHẶN BỞI KHÓA       : %d (Tránh Overbooking thành công)
                ================================================================================
                """, NUMBER_OF_THREADS, MA_PHONG, successCount.get(), failureCount.get());

        System.out.println(report);
        log.info(report);

        // Khẳng định đúng tính chất của Khóa bi quan: Đúng 1 thành công, 9 thất bại
        assertEquals(1, successCount.get(), "Chỉ được phép đúng 1 đơn đặt thành công!");
        assertEquals(9, failureCount.get(), "9 đơn đặt còn lại phải thất bại!");
    }
}
