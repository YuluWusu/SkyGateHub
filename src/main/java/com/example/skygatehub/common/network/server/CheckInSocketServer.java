package com.example.skygatehub.common.network.server;

import com.example.skygatehub.repository.account.NguoiDungRepository;
import com.example.skygatehub.repository.checkin.CheckInRepository;
import com.example.skygatehub.repository.dining.DatBanRepository;
import com.example.skygatehub.repository.order.DonDatRepository;
import com.example.skygatehub.repository.order.VeRepository;
import com.example.skygatehub.repository.roombooking.DatPhongRepository;
import com.example.skygatehub.service.room.RoomOperationService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Socket Server phục vụ check-in đồng thời đa trạm (Tiêu chí 5.3 - 0,4 điểm).
 * Chạy ngầm trên cổng 9090 bằng ExecutorService, phục vụ tối thiểu 3 Client đồng thời.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CheckInSocketServer {

    @Value("${socket.server.port:9090}")
    private int port;

    private final DonDatRepository donDatRepository;
    private final VeRepository veRepository;
    private final DatPhongRepository datPhongRepository;
    private final DatBanRepository datBanRepository;
    private final CheckInRepository checkInRepository;
    private final NguoiDungRepository nguoiDungRepository;
    private final RoomOperationService roomOperationService;
    private final PlatformTransactionManager transactionManager;

    private ServerSocket serverSocket;
    private ExecutorService threadPool;
    private volatile boolean running = false;

    @PostConstruct
    public void startServer() {
        // Cấp phát ThreadPool phục vụ tối thiểu 3 Client đồng thời (Tiêu chí 5.3)
        this.threadPool = Executors.newFixedThreadPool(10, r -> {
            Thread t = new Thread(r);
            t.setName("SocketWorker-" + t.getId());
            t.setDaemon(true);
            return t;
        });

        // Khởi động server trong luồng nền để không chặn quá trình boot của Spring MVC
        Thread serverThread = new Thread(this::listen, "SkyGate-SocketServer-Main");
        serverThread.setDaemon(true);
        serverThread.start();
    }

    private void listen() {
        try {
            serverSocket = new ServerSocket(port);
            running = true;
            log.info("[THREAD: {}] === CheckInSocketServer đã khởi động thành công trên cổng TCP {} ===",
                    Thread.currentThread().getName(), port);

            while (running) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    log.info("[THREAD: {}] Nhận kết nối mới từ: {}",
                            Thread.currentThread().getName(), clientSocket.getRemoteSocketAddress());

                    CheckInClientHandler handler = new CheckInClientHandler(
                            clientSocket,
                            donDatRepository,
                            veRepository,
                            datPhongRepository,
                            datBanRepository,
                            checkInRepository,
                            nguoiDungRepository,
                            roomOperationService,
                            transactionManager
                    );
                    threadPool.execute(handler);
                } catch (IOException e) {
                    if (!running) {
                        log.info("CheckInSocketServer đang dừng tiếp nhận kết nối.");
                        break;
                    }
                    log.error("Lỗi khi accept client socket: {}", e.getMessage());
                }
            }
        } catch (IOException e) {
            log.error("Không thể mở ServerSocket trên cổng {}: {}", port, e.getMessage());
        }
    }

    @PreDestroy
    public void stopServer() {
        running = false;
        if (serverSocket != null && !serverSocket.isClosed()) {
            try {
                serverSocket.close();
            } catch (IOException ignored) {}
        }
        if (threadPool != null && !threadPool.isShutdown()) {
            threadPool.shutdownNow();
        }
        log.info("CheckInSocketServer đã dừng an toàn.");
    }
}
