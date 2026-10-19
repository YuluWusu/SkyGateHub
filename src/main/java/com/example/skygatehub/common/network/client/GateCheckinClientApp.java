package com.example.skygatehub.common.network.client;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Ứng dụng Client Socket đồ họa Java Swing (Tiêu chí 5.4 - 0,4 điểm).
 * Dùng tại các quầy / cổng soát vé KDL Cổng Trời Đông Giang.
 * Có nút mở đồng thời 3 trạm Check-in độc lập để kiểm chứng khả năng phục vụ đồng thời của Server.
 */
public class GateCheckinClientApp extends JFrame {

    private final String stationId;
    private final String defaultStationName;

    private JTextField hostField;
    private JTextField portField;
    private JComboBox<String> stationCombo;
    private JButton connectBtn;
    private JButton disconnectBtn;

    private JTextField codeField;
    private JButton lookupBtn;
    private JButton checkinVeBtn;
    private JButton checkinPhongBtn;
    private JButton checkinAnBtn;
    private JButton statsBtn;

    private JTextArea logArea;

    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;
    private Thread listenerThread;
    private volatile boolean connected = false;

    public GateCheckinClientApp(String stationId, String defaultStationName) {
        this.stationId = stationId;
        this.defaultStationName = defaultStationName;
        initUI();
    }

    private void initUI() {
        setTitle("SkyGateHub - Trạm Check-In Điểm Soát Vé [" + defaultStationName + "]");
        setSize(800, 600);
        setMinimumSize(new Dimension(700, 500));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(12, 12, 12, 12));

        // Top Panel: Cấu hình kết nối & trạm
        JPanel topPanel = new JPanel(new GridLayout(2, 1, 6, 6));

        JPanel connPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        connPanel.add(new JLabel("Host:"));
        hostField = new JTextField("localhost", 10);
        connPanel.add(hostField);

        connPanel.add(new JLabel("Port:"));
        portField = new JTextField("9090", 5);
        connPanel.add(portField);

        connPanel.add(new JLabel("Tên Trạm:"));
        stationCombo = new JComboBox<>(new String[]{
                defaultStationName,
                "TRAM_01_CONG_VE_VAO",
                "TRAM_02_QUAY_LE_TAN_PHONG",
                "TRAM_03_NHA_HANG_AM_THUC"
        });
        stationCombo.setEditable(true);
        connPanel.add(stationCombo);

        connectBtn = new JButton("Kết Nối");
        connectBtn.setBackground(new Color(40, 167, 69));
        connectBtn.setForeground(Color.WHITE);
        connectBtn.addActionListener(e -> connectToServer());
        connPanel.add(connectBtn);

        disconnectBtn = new JButton("Ngắt Kết Nối");
        disconnectBtn.setBackground(new Color(220, 53, 69));
        disconnectBtn.setForeground(Color.WHITE);
        disconnectBtn.setEnabled(false);
        disconnectBtn.addActionListener(e -> disconnectFromServer());
        connPanel.add(disconnectBtn);

        // Nút mở đồng thời 3 trạm
        JButton multiStationBtn = new JButton("🚀 Mở Đồng Thời 3 Trạm Check-in");
        multiStationBtn.setBackground(new Color(0, 123, 255));
        multiStationBtn.setForeground(Color.WHITE);
        multiStationBtn.addActionListener(e -> launchThreeStations());
        connPanel.add(multiStationBtn);

        topPanel.add(connPanel);

        // Panel Lệnh Thao Tác
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        actionPanel.add(new JLabel("Mã Barcode / QR / Mã đơn:"));
        codeField = new JTextField(18);
        codeField.addActionListener(e -> sendCommand("LOOKUP " + codeField.getText().trim()));
        actionPanel.add(codeField);

        lookupBtn = new JButton("🔍 Tra Cứu (LOOKUP)");
        lookupBtn.addActionListener(e -> sendCommand("LOOKUP " + codeField.getText().trim()));
        actionPanel.add(lookupBtn);

        checkinVeBtn = new JButton("🎟️ Check-in Vé");
        checkinVeBtn.setBackground(new Color(23, 162, 184));
        checkinVeBtn.setForeground(Color.WHITE);
        checkinVeBtn.addActionListener(e -> sendCommand("CHECKIN_VE " + codeField.getText().trim()));
        actionPanel.add(checkinVeBtn);

        checkinPhongBtn = new JButton("🏨 Check-in Phòng");
        checkinPhongBtn.setBackground(new Color(108, 117, 125));
        checkinPhongBtn.setForeground(Color.WHITE);
        checkinPhongBtn.addActionListener(e -> sendCommand("CHECKIN_PHONG " + codeField.getText().trim()));
        actionPanel.add(checkinPhongBtn);

        checkinAnBtn = new JButton("🍽️ Check-in Bàn Ăn");
        checkinAnBtn.setBackground(new Color(255, 193, 7));
        checkinAnBtn.addActionListener(e -> sendCommand("CHECKIN_AN " + codeField.getText().trim()));
        actionPanel.add(checkinAnBtn);

        statsBtn = new JButton("📊 Thống Kê (STATS)");
        statsBtn.addActionListener(e -> sendCommand("STATS"));
        actionPanel.add(statsBtn);

        topPanel.add(actionPanel);
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Center Panel: Nhật ký bản tin Socket (Log Area)
        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font("Consolas", Font.PLAIN, 13));
        logArea.setBackground(new Color(245, 245, 245));
        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Nhật ký bản tin giao thức Socket TCP (RFC Text Protocol)"));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Bottom Panel: Clear log & status bar
        JPanel bottomPanel = new JPanel(new BorderLayout());
        JLabel statusLbl = new JLabel("Trạng thái: Sẵn sàng kết nối tới CheckInSocketServer (Port 9090)");
        bottomPanel.add(statusLbl, BorderLayout.WEST);

        JButton clearBtn = new JButton("Xóa Nhật Ký");
        clearBtn.addActionListener(e -> logArea.setText(""));
        bottomPanel.add(clearBtn, BorderLayout.EAST);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);

        appendLog("=== SkyGateHub Check-In Desktop Client v1.0 ===");
        appendLog("Sẵn sàng phục vụ. Bấm 'Kết Nối' để kết nối tới cổng 9090.");
    }

    private void connectToServer() {
        String host = hostField.getText().trim();
        int port;
        try {
            port = Integer.parseInt(portField.getText().trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Port phải là số nguyên!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String stationName = (String) stationCombo.getSelectedItem();
        if (stationName == null || stationName.isBlank()) stationName = "TRAM_DEFAULT";

        final String finalStation = stationName;

        new Thread(() -> {
            try {
                appendLog("[SOCKET] Đang kết nối tới " + host + ":" + port + "...");
                socket = new Socket(host, port);
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
                writer = new PrintWriter(socket.getOutputStream(), true, StandardCharsets.UTF_8);
                connected = true;

                SwingUtilities.invokeLater(() -> {
                    connectBtn.setEnabled(false);
                    disconnectBtn.setEnabled(true);
                    hostField.setEnabled(false);
                    portField.setEnabled(false);
                    stationCombo.setEnabled(false);
                });

                // Luồng lắng nghe dữ liệu từ Server
                listenerThread = new Thread(() -> {
                    try {
                        String response;
                        while (connected && (response = reader.readLine()) != null) {
                            appendLog("<-- [SERVER] " + response);
                        }
                    } catch (Exception e) {
                        if (connected) {
                            appendLog("[LỖI ĐỌC] Mất kết nối tới máy chủ: " + e.getMessage());
                        }
                    } finally {
                        disconnectFromServer();
                    }
                }, "SocketListener-" + stationId);
                listenerThread.start();

                // Gửi lệnh AUTH tự động sau khi kết nối
                Thread.sleep(150);
                sendCommand("AUTH " + finalStation);

            } catch (Exception e) {
                appendLog("[LỖI KẾT NỐI] Không thể kết nối tới Server: " + e.getMessage());
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this,
                        "Không thể kết nối tới Socket Server tại " + host + ":" + port + "\nHãy đảm bảo Spring Boot backend đang chạy!",
                        "Lỗi Kết Nối", JOptionPane.ERROR_MESSAGE));
            }
        }).start();
    }

    private void disconnectFromServer() {
        connected = false;
        try {
            if (writer != null) {
                writer.println("QUIT");
            }
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> {
            connectBtn.setEnabled(true);
            disconnectBtn.setEnabled(false);
            hostField.setEnabled(true);
            portField.setEnabled(true);
            stationCombo.setEnabled(true);
            appendLog("[SOCKET] Đã ngắt kết nối an toàn.");
        });
    }

    private void sendCommand(String cmd) {
        if (!connected || writer == null) {
            JOptionPane.showMessageDialog(this, "Chưa kết nối tới máy chủ! Vui lòng bấm 'Kết Nối' trước.", "Chưa Kết Nối", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (cmd == null || cmd.isBlank()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập mã tra cứu / mã vé vào ô nhập liệu!", "Thiếu Dữ Liệu", JOptionPane.WARNING_MESSAGE);
            return;
        }

        appendLog("--> [CLIENT] " + cmd);
        writer.println(cmd);
    }

    private void appendLog(String msg) {
        String time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        SwingUtilities.invokeLater(() -> {
            logArea.append("[" + time + "] " + msg + "\n");
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }

    /**
     * Mở đồng thời 3 cửa sổ trạm check-in độc lập trên 3 luồng riêng (Tiêu chí 5.3 & 5.4).
     */
    public static void launchThreeStations() {
        SwingUtilities.invokeLater(() -> {
            GateCheckinClientApp station1 = new GateCheckinClientApp("STATION_1", "TRAM_01_CONG_VE_VAO");
            station1.setTitle("Trạm 1: Cổng Soát Vé Vào Khu Du Lịch (TCP:9090)");
            station1.setLocation(50, 100);
            station1.setVisible(true);

            GateCheckinClientApp station2 = new GateCheckinClientApp("STATION_2", "TRAM_02_QUAY_LE_TAN_PHONG");
            station2.setTitle("Trạm 2: Quầy Lễ Tân Lưu Trú / Check-in Phòng (TCP:9090)");
            station2.setLocation(450, 150);
            station2.setVisible(true);

            GateCheckinClientApp station3 = new GateCheckinClientApp("STATION_3", "TRAM_03_NHA_HANG_AM_THUC");
            station3.setTitle("Trạm 3: Cổng Nhà Hàng / Check-in Ẩm Thực (TCP:9090)");
            station3.setLocation(850, 200);
            station3.setVisible(true);
        });
    }

    public static void main(String[] args) {
        // Cấu hình giao diện Look and Feel hệ thống
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> {
            GateCheckinClientApp app = new GateCheckinClientApp("DEFAULT", "TRAM_01_CONG_VE_VAO");
            app.setVisible(true);
        });
    }
}
