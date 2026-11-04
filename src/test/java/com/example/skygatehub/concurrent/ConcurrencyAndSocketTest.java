package com.example.skygatehub.concurrent;

import com.example.skygatehub.common.enums.LoaiVeType;
import com.example.skygatehub.common.enums.TrangThaiDon;
import com.example.skygatehub.common.enums.TrangThaiVe;
import com.example.skygatehub.common.network.server.CheckInSocketServer;
import com.example.skygatehub.common.util.OrderCodeGenerator;
import com.example.skygatehub.entity.account.NguoiDung;
import com.example.skygatehub.entity.checkin.CheckIn;
import com.example.skygatehub.entity.order.ChiTietDon;
import com.example.skygatehub.entity.order.DonDat;
import com.example.skygatehub.entity.order.Ve;
import com.example.skygatehub.entity.ticket.LoaiVe;
import com.example.skygatehub.repository.account.NguoiDungRepository;
import com.example.skygatehub.repository.checkin.CheckInRepository;
import com.example.skygatehub.repository.dining.DatBanRepository;
import com.example.skygatehub.repository.order.DonDatRepository;
import com.example.skygatehub.repository.order.VeRepository;
import com.example.skygatehub.repository.roombooking.DatPhongRepository;
import com.example.skygatehub.service.checkin.impl.CheckInServiceImpl;
import com.example.skygatehub.service.room.RoomOperationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.SimpleTransactionStatus;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Chương trình kiểm thử xử lý đồng thời (đa luồng) và lập trình mạng Socket (Tiêu chí 5.2, 5.3, 5.4).
 * Dùng làm minh chứng thực nghiệm cho Chương 4 của Báo cáo đồ án.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Kiểm thử Đa luồng & Đồng thời Socket Server (Tiêu chí 5.2, 5.3, 5.4)")
class ConcurrencyAndSocketTest {

    private Ve createSampleTicket(String maVe, String maVeChuoi) {
        DonDat donDat = new DonDat();
        donDat.setMaDonDat("DD_TEST_01");
        donDat.setMaDonHang("CTDG-261104-9999");
        donDat.setHoTenKhach("Nguyễn Văn Thực Nghiệm");
        donDat.setSdtKhach("0905123456");
        donDat.setTongTien(new BigDecimal("700000"));
        donDat.setTrangThaiDon(TrangThaiDon.DA_XAC_NHAN);

        LoaiVe loaiVe = new LoaiVe();
        loaiVe.setMaLoaiVe("VE01");
        loaiVe.setTenLoaiVe("Vé Tham Quan Người Lớn");
        loaiVe.setLoaiVe(LoaiVeType.NGUOI_LON);

        ChiTietDon chiTietDon = new ChiTietDon();
        chiTietDon.setMaChiTiet("CTD_TEST_01");
        chiTietDon.setDonDat(donDat);
        chiTietDon.setLoaiVe(loaiVe);

        Ve ve = new Ve();
        ve.setMaVe(maVe);
        ve.setMaVeChuoi(maVeChuoi);
        ve.setChiTietDon(chiTietDon);
        ve.setLoaiVe(loaiVe);
        ve.setNgayThamQuan(LocalDate.now());
        ve.setTrangThai(TrangThaiVe.CHUA_SU_DUNG);
        return ve;
    }

    @Test
    @Order(1)
    @DisplayName("Kịch bản 1 (Tiêu chí 5.2): 10 luồng đồng thời quét CÙNG 1 mã vé - Chỉ đúng 1 luồng thành công, 9 luồng bị chặn")
    void testTenConcurrentThreadsCheckInSameTicket() throws InterruptedException {
        Ve sharedTicket = createSampleTicket("VE_CONC_001", "CTDG-261104-9999-VE01-1");

        VeRepository veRepository = mock(VeRepository.class);
        DonDatRepository donDatRepository = mock(DonDatRepository.class);
        CheckInRepository checkInRepository = mock(CheckInRepository.class);
        NguoiDungRepository nguoiDungRepository = mock(NguoiDungRepository.class);

        NguoiDung staff = new NguoiDung();
        staff.setMaNguoiDung("ND003");
        staff.setHoTen("Lê Văn Soát Vé");

        when(veRepository.findByMaVeWithLock("VE_CONC_001")).thenReturn(Optional.of(sharedTicket));
        when(veRepository.findByMaVe("VE_CONC_001")).thenReturn(Optional.of(sharedTicket));
        when(veRepository.save(any(Ve.class))).thenAnswer(inv -> inv.getArgument(0));
        when(nguoiDungRepository.findByMaNguoiDung("ND003")).thenReturn(Optional.of(staff));
        when(checkInRepository.save(any(CheckIn.class))).thenAnswer(inv -> inv.getArgument(0));

        CheckInServiceImpl checkInService = new CheckInServiceImpl(
                veRepository, donDatRepository, checkInRepository, nguoiDungRepository);

        int threadCount = 10;
        AtomicInteger threadSeq = new AtomicInteger(1);
        ExecutorService executor = Executors.newFixedThreadPool(threadCount, r -> {
            Thread t = new Thread(r);
            t.setName("GateScanner-Thread-" + threadSeq.getAndIncrement());
            return t;
        });

        CountDownLatch readyLatch = new CountDownLatch(threadCount);
        CountDownLatch startGun = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(threadCount);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger conflictCount = new AtomicInteger(0);
        List<String> auditLogs = Collections.synchronizedList(new ArrayList<>());

        System.out.println("\n================================================================================");
        System.out.println(" [BẮT ĐẦU KIỂM THỬ 5.2] 10 LUỒNG CÙNG QUÉT 1 MÃ VÉ (VE_CONC_001) TẠI CÙNG THỜI ĐIỂM");
        System.out.println("================================================================================");

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                String tName = Thread.currentThread().getName();
                readyLatch.countDown();
                try {
                    startGun.await(); // Chờ hiệu lệnh xuất phát đồng loạt
                    CheckIn result = checkInService.checkInSingleTicket("VE_CONC_001", "ND003", "Quét đồng thời");
                    successCount.incrementAndGet();
                    auditLogs.add(String.format("  [THÀNH CÔNG] %-22s -> Mã CheckIn: %s | Vé chuyển sang: %s",
                            tName, result.getMaCheckIn(), result.getVe().getTrangThai()));
                } catch (IllegalStateException ex) {
                    conflictCount.incrementAndGet();
                    auditLogs.add(String.format("  [BỊ CHẶN]    %-22s -> %s", tName, ex.getMessage()));
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        readyLatch.await(5, TimeUnit.SECONDS);
        startGun.countDown(); // Bắn pháo hiệu cho 10 luồng chạy cùng lúc!
        assertTrue(doneLatch.await(10, TimeUnit.SECONDS), "Các luồng kiểm thử phải hoàn tất trong 10 giây");
        executor.shutdownNow();

        auditLogs.forEach(System.out::println);
        System.out.println("--------------------------------------------------------------------------------");
        System.out.printf(" TỔNG KẾT 5.2: Tổng số luồng = %d | Thành công = %d | Bị chặn quét trùng = %d%n",
                threadCount, successCount.get(), conflictCount.get());
        System.out.println("================================================================================\n");

        assertEquals(1, successCount.get(), "Chỉ duy nhất 1 luồng được phép check-in thành công");
        assertEquals(9, conflictCount.get(), "9 luồng còn lại phải bị chặn bởi cơ chế đồng bộ");
        assertEquals(TrangThaiVe.DA_SU_DUNG, sharedTicket.getTrangThai(), "Trạng thái vé sau cùng phải là DA_SU_DUNG");
        verify(checkInRepository, times(1)).save(any(CheckIn.class));
    }

    @Test
    @Order(2)
    @DisplayName("Kịch bản 2 (Tiêu chí 4.5 & 5.2): 10 luồng sinh đồng thời 1.000 mã vé/đơn hàng không trùng lặp")
    void testConcurrentUniqueCodeGeneration() throws InterruptedException {
        int threads = 10;
        int codesPerThread = 100;
        int totalExpected = threads * codesPerThread;

        ExecutorService pool = Executors.newFixedThreadPool(threads);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(threads);
        Set<String> generatedCodes = ConcurrentHashMap.newKeySet();

        for (int i = 0; i < threads; i++) {
            pool.submit(() -> {
                try {
                    startLatch.await();
                    for (int j = 0; j < codesPerThread; j++) {
                        generatedCodes.add(OrderCodeGenerator.generateMaVe());
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        startLatch.countDown();
        assertTrue(doneLatch.await(10, TimeUnit.SECONDS));
        pool.shutdownNow();

        System.out.printf("[KIỂM THỬ SINH MÃ ĐA LUỒNG] Đã sinh %d/%d mã vé duy nhất tuyệt đối (0 xung đột).%n",
                generatedCodes.size(), totalExpected);
        assertEquals(totalExpected, generatedCodes.size(), "Không được phép trùng bất kỳ mã vé nào khi chạy đa luồng");
    }

    @Test
    @Order(3)
    @DisplayName("Kịch bản 3 (Tiêu chí 5.3 & 5.4): 5 Socket Client kết nối TCP đồng thời tới CheckInSocketServer")
    void testFiveConcurrentSocketClients() throws Exception {
        Ve socketTicket = createSampleTicket("VE_SOCKET_999", "CTDG-261104-9999-VE01-9");

        DonDatRepository donDatRepository = mock(DonDatRepository.class);
        VeRepository veRepository = mock(VeRepository.class);
        DatPhongRepository datPhongRepository = mock(DatPhongRepository.class);
        DatBanRepository datBanRepository = mock(DatBanRepository.class);
        CheckInRepository checkInRepository = mock(CheckInRepository.class);
        NguoiDungRepository nguoiDungRepository = mock(NguoiDungRepository.class);
        RoomOperationService roomOperationService = mock(RoomOperationService.class);
        PlatformTransactionManager txManager = mock(PlatformTransactionManager.class);

        TransactionStatus txStatus = new SimpleTransactionStatus();
        when(txManager.getTransaction(any())).thenReturn(txStatus);

        NguoiDung staff = new NguoiDung();
        staff.setMaNguoiDung("ND003");
        staff.setHoTen("Lê Văn Soát Vé");

        AtomicInteger savedCheckInCounter = new AtomicInteger(0);
        when(veRepository.findByMaVe("VE_SOCKET_999")).thenReturn(Optional.of(socketTicket));
        when(veRepository.findByMaVeWithLock("VE_SOCKET_999")).thenReturn(Optional.of(socketTicket));
        when(veRepository.findByMaVeChuoiWithLock("VE_SOCKET_999")).thenReturn(Optional.of(socketTicket));
        when(veRepository.save(any(Ve.class))).thenAnswer(inv -> inv.getArgument(0));
        when(nguoiDungRepository.findAll()).thenReturn(List.of(staff));
        when(checkInRepository.save(any(CheckIn.class))).thenAnswer(inv -> {
            savedCheckInCounter.incrementAndGet();
            return inv.getArgument(0);
        });
        when(checkInRepository.count()).thenAnswer(inv -> (long) savedCheckInCounter.get());

        // Khởi động CheckInSocketServer trên cổng động (port = 0)
        CheckInSocketServer server = new CheckInSocketServer(
                donDatRepository,
                veRepository,
                datPhongRepository,
                datBanRepository,
                checkInRepository,
                nguoiDungRepository,
                roomOperationService,
                txManager
        );
        server.setPort(0);
        server.startServer();

        // Đợi ServerSocket bind cổng thành công
        int attempts = 0;
        while (!server.isRunning() && attempts++ < 50) {
            Thread.sleep(20);
        }
        assertTrue(server.isRunning(), "CheckInSocketServer phải đang hoạt động");
        int boundPort = server.getBoundPort();
        assertTrue(boundPort > 0, "Cổng TCP của SocketServer phải hợp lệ");

        int clientCount = 5; // Phục vụ đồng thời 5 Client (vượt mức tối thiểu 3 Client của Tiêu chí 5.4)
        ExecutorService clientPool = Executors.newFixedThreadPool(clientCount);
        CountDownLatch connectedBarrier = new CountDownLatch(clientCount);
        CountDownLatch scanBarrier = new CountDownLatch(clientCount);
        CountDownLatch finishedLatch = new CountDownLatch(clientCount);

        AtomicInteger socketCheckInSuccess = new AtomicInteger(0);
        AtomicInteger socketCheckInConflict = new AtomicInteger(0);
        Set<String> workerThreadsUsed = ConcurrentHashMap.newKeySet();
        List<String> clientTranscripts = Collections.synchronizedList(new ArrayList<>());

        System.out.println("\n================================================================================");
        System.out.printf(" [BẮT ĐẦU KIỂM THỬ 5.3 & 5.4] %d SOCKET CLIENT KẾT NỐI ĐỒNG THỜI TỚI CỔNG TCP %d%n",
                clientCount, boundPort);
        System.out.println("================================================================================");

        for (int c = 1; c <= clientCount; c++) {
            final int clientIndex = c;
            clientPool.submit(() -> {
                String station = "TRAM_CONG_SO_" + clientIndex;
                try (
                    Socket socket = new Socket("127.0.0.1", boundPort);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
                    PrintWriter writer = new PrintWriter(socket.getOutputStream(), true, StandardCharsets.UTF_8)
                ) {
                    socket.setSoTimeout(5000);
                    String welcome = reader.readLine();
                    connectedBarrier.countDown();
                    connectedBarrier.await(5, TimeUnit.SECONDS); // Đảm bảo cả 5 Client đang mở kết nối cùng lúc!

                    // 1. Gửi PING để kiểm tra luồng SocketWorker phục vụ
                    writer.println("PING");
                    String pongResp = reader.readLine();
                    if (pongResp != null && pongResp.contains("ServerThread=")) {
                        String worker = pongResp.split("ServerThread=")[1].split("\\s+")[0];
                        workerThreadsUsed.add(worker);
                    }

                    // 2. Xác thực trạm AUTH
                    writer.println("AUTH|" + station);
                    String authResp = reader.readLine();

                    // 3. Tra cứu vé LOOKUP
                    writer.println("LOOKUP|VE_SOCKET_999");
                    String lookupResp = reader.readLine();

                    // 4. Đồng loạt cả 5 Client gửi lệnh CHECKIN_VE cùng lúc
                    scanBarrier.countDown();
                    scanBarrier.await(5, TimeUnit.SECONDS);

                    writer.println("CHECKIN_VE|VE_SOCKET_999");
                    String checkInResp = reader.readLine();
                    if (checkInResp != null && checkInResp.startsWith("200 CHECKIN_SUCCESS")) {
                        socketCheckInSuccess.incrementAndGet();
                    } else if (checkInResp != null && checkInResp.startsWith("409 CONFLICT")) {
                        socketCheckInConflict.incrementAndGet();
                    }

                    // 5. Lấy thống kê và đóng kết nối đúng chuẩn
                    writer.println("STATS");
                    String statsResp = reader.readLine();

                    writer.println("QUIT");
                    String byeResp = reader.readLine();

                    clientTranscripts.add(String.format(
                            "  [Client #%d - %s]%n    ├─ WELCOME : %s%n    ├─ PING    : %s%n    ├─ AUTH    : %s%n    ├─ LOOKUP  : %s%n    ├─ CHECKIN : %s%n    ├─ STATS   : %s%n    └─ QUIT    : %s",
                            clientIndex, station, welcome, pongResp, authResp, lookupResp, checkInResp, statsResp, byeResp));
                } catch (Exception e) {
                    clientTranscripts.add("  [Client #" + clientIndex + " LỖI]: " + e.getMessage());
                } finally {
                    finishedLatch.countDown();
                }
            });
        }

        assertTrue(finishedLatch.await(15, TimeUnit.SECONDS), "5 Socket Client phải hoàn tất giao tiếp trong 15 giây");
        clientPool.shutdownNow();
        server.stopServer();

        clientTranscripts.forEach(System.out::println);
        System.out.println("--------------------------------------------------------------------------------");
        System.out.printf(" TỔNG KẾT 5.4: Số Client đồng thời = %d | Các luồng Server tham gia = %s%n",
                clientCount, workerThreadsUsed);
        System.out.printf(" KẾT QUẢ QUÉT VÉ QUA SOCKET: Thành công = %d | Bị chặn trùng (409) = %d%n",
                socketCheckInSuccess.get(), socketCheckInConflict.get());
        System.out.println("================================================================================\n");

        assertTrue(workerThreadsUsed.size() >= 3, "Server phải phân bổ tối thiểu 3 luồng SocketWorker phục vụ đồng thời");
        assertEquals(1, socketCheckInSuccess.get(), "Chỉ đúng 1 Socket Client được phép check-in thành công");
        assertEquals(4, socketCheckInConflict.get(), "4 Socket Client còn lại phải nhận mã 409 CONFLICT_ALREADY_USED");
    }
}
