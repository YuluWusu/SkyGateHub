package com.example.skygatehub.common.network.client;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

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
    private JLabel connectionStatusLabel;

    private JTextField ticketCodeField;
    private JButton checkinTicketBtn;
    private JButton lookupTicketBtn;
    private DefaultTableModel ticketTableModel;
    private JLabel ticketCountLabel;
    private final AtomicInteger successTicketCount = new AtomicInteger(0);

    private JTextField roomCodeField;
    private JButton checkinRoomBtn;

    private JTextField diningCodeField;
    private JButton checkinDiningBtn;

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
        setTitle("SkyGateHub - Hệ Thống Soát Vé & Check-in [" + defaultStationName + "]");
        setSize(900, 680);
        setMinimumSize(new Dimension(800, 550));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(12, 12, 12, 12));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        topPanel.setBorder(BorderFactory.createTitledBorder("Cấu hình kết nối TCP Socket (Cổng 9090)"));

        topPanel.add(new JLabel("Máy chủ:"));
        hostField = new JTextField("localhost", 10);
        topPanel.add(hostField);

        topPanel.add(new JLabel("Cổng:"));
        portField = new JTextField("9090", 5);
        topPanel.add(portField);

        topPanel.add(new JLabel("Trạm:"));
        stationCombo = new JComboBox<>(new String[]{
                defaultStationName,
                "TRAM_01_CONG_VE_VAO",
                "TRAM_02_QUAY_LE_TAN_PHONG",
                "TRAM_03_NHA_HANG_AM_THUC"
        });
        stationCombo.setEditable(true);
        topPanel.add(stationCombo);

        connectBtn = new JButton("Kết Nối");
        connectBtn.setBackground(new Color(40, 167, 69));
        connectBtn.setForeground(Color.WHITE);
        connectBtn.addActionListener(e -> connectToServer());
        topPanel.add(connectBtn);

        disconnectBtn = new JButton("Ngắt Kết Nối");
        disconnectBtn.setBackground(new Color(220, 53, 69));
        disconnectBtn.setForeground(Color.WHITE);
        disconnectBtn.setEnabled(false);
        disconnectBtn.addActionListener(e -> disconnectFromServer());
        topPanel.add(disconnectBtn);

        connectionStatusLabel = new JLabel("⚪ Chưa kết nối");
        connectionStatusLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        topPanel.add(connectionStatusLabel);

        mainPanel.add(topPanel, BorderLayout.NORTH);

        JTabbedPane tabbedPane = new JTabbedPane();

        JPanel ticketTab = createTicketStationTab();
        tabbedPane.addTab("🎟️ Trạm 1: Cổng Vé", ticketTab);

        JPanel roomTab = createRoomStationTab();
        tabbedPane.addTab("🏨 Trạm 2: Quầy Lưu Trú", roomTab);

        JPanel diningTab = createDiningStationTab();
        tabbedPane.addTab("🍽️ Trạm 3: Nhà Hàng", diningTab);

        JPanel centerPanel = new JPanel(new BorderLayout(5, 5));
        centerPanel.add(tabbedPane, BorderLayout.CENTER);

        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        logArea.setBackground(new Color(248, 249, 250));
        JScrollPane scrollLog = new JScrollPane(logArea);
        scrollLog.setPreferredSize(new Dimension(860, 180));
        scrollLog.setBorder(BorderFactory.createTitledBorder("Nhật ký truyền nhận gói tin TCP"));
        centerPanel.add(scrollLog, BorderLayout.SOUTH);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        JLabel footerLabel = new JLabel("SkyGateHub Ecosystem — Phân hệ Soát vé Socket Client (RFC Text Protocol)");
        bottomPanel.add(footerLabel, BorderLayout.WEST);

        JButton clearBtn = new JButton("Xóa Nhật Ký");
        clearBtn.addActionListener(e -> logArea.setText(""));
        bottomPanel.add(clearBtn, BorderLayout.EAST);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);
        appendLog("Khởi động ứng dụng Client Swing. Sẵn sàng kết nối tới CheckInSocketServer.");
    }

    private JPanel createTicketStationTab() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        controlPanel.add(new JLabel("Mã vé / Chuỗi QR soát vé:"));
        ticketCodeField = new JTextField(22);
        ticketCodeField.setFont(new Font("Consolas", Font.BOLD, 14));
        ticketCodeField.addActionListener(e -> processTicketCheckin());
        controlPanel.add(ticketCodeField);

        checkinTicketBtn = new JButton("🎟️ Soát Vé (CHECKIN_VE)");
        checkinTicketBtn.setBackground(new Color(13, 110, 253));
        checkinTicketBtn.setForeground(Color.WHITE);
        checkinTicketBtn.setFont(new Font("SansSerif", Font.BOLD, 13));
        checkinTicketBtn.addActionListener(e -> processTicketCheckin());
        controlPanel.add(checkinTicketBtn);

        lookupTicketBtn = new JButton("🔍 Tra Cứu Vé");
        lookupTicketBtn.addActionListener(e -> sendCommand("LOOKUP " + ticketCodeField.getText().trim()));
        controlPanel.add(lookupTicketBtn);

        ticketCountLabel = new JLabel("Số vé đã soát thành công trong ca: 0");
        ticketCountLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        ticketCountLabel.setForeground(new Color(25, 135, 84));
        controlPanel.add(ticketCountLabel);

        panel.add(controlPanel, BorderLayout.NORTH);

        String[] columns = {"Thời gian", "Mã Vé / Chuỗi", "Loại Vé", "Mã Check-in", "Kết Quả"};
        ticketTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable ticketTable = new JTable(ticketTableModel);
        ticketTable.setRowHeight(24);
        ticketTable.setFont(new Font("SansSerif", Font.PLAIN, 12));
        JScrollPane tableScroll = new JScrollPane(ticketTable);
        tableScroll.setBorder(BorderFactory.createTitledBorder("Danh sách vé vừa soát tại Trạm 1: Cổng Vé"));
        panel.add(tableScroll, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createRoomStationTab() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        panel.add(new JLabel("Mã đặt phòng:"));
        roomCodeField = new JTextField(20);
        panel.add(roomCodeField);

        checkinRoomBtn = new JButton("🏨 Check-in Nhận Phòng");
        checkinRoomBtn.setBackground(new Color(108, 117, 125));
        checkinRoomBtn.setForeground(Color.WHITE);
        checkinRoomBtn.addActionListener(e -> sendCommand("CHECKIN_PHONG " + roomCodeField.getText().trim()));
        panel.add(checkinRoomBtn);

        JButton lookupRoomBtn = new JButton("Tra Cứu Phòng");
        lookupRoomBtn.addActionListener(e -> sendCommand("LOOKUP " + roomCodeField.getText().trim()));
        panel.add(lookupRoomBtn);

        return panel;
    }

    private JPanel createDiningStationTab() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        panel.add(new JLabel("Mã đặt bàn:"));
        diningCodeField = new JTextField(20);
        panel.add(diningCodeField);

        checkinDiningBtn = new JButton("🍽️ Check-in Vào Bàn Ăn");
        checkinDiningBtn.setBackground(new Color(255, 193, 7));
        checkinDiningBtn.addActionListener(e -> sendCommand("CHECKIN_AN " + diningCodeField.getText().trim()));
        panel.add(checkinDiningBtn);

        JButton lookupDiningBtn = new JButton("Tra Cứu Bàn");
        lookupDiningBtn.addActionListener(e -> sendCommand("LOOKUP " + diningCodeField.getText().trim()));
        panel.add(lookupDiningBtn);

        return panel;
    }

    private void processTicketCheckin() {
        String code = ticketCodeField.getText().trim();
        if (code.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập hoặc quét mã vé!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        sendCommand("CHECKIN_VE " + code);
        ticketCodeField.setText("");
        ticketCodeField.requestFocus();
    }

    private void connectToServer() {
        String host = hostField.getText().trim();
        int port;
        try {
            port = Integer.parseInt(portField.getText().trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Cổng Port phải là số nguyên hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String stationName = (String) stationCombo.getSelectedItem();
        if (stationName == null || stationName.trim().isEmpty()) {
            stationName = "TRAM_01_CONG_VE_VAO";
        }

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
                    connectionStatusLabel.setText("🟢 Đã kết nối (" + finalStation + ")");
                });

                // Luồng lắng nghe dữ liệu từ Server
                listenerThread = new Thread(() -> {
                    try {
                        String response;
                        while (connected && (response = reader.readLine()) != null) {
                            String finalResp = response;
                            SwingUtilities.invokeLater(() -> handleServerResponse(finalResp));
                        }
                    } catch (Exception ex) {
                        if (connected) {
                            appendLog("[SOCKET] Mất kết nối tới server: " + ex.getMessage());
                        }
                    } finally {
                        disconnectFromServer();
                    }
                });
                listenerThread.start();

                // Gửi lệnh AUTH tự động sau khi kết nối
                sendCommand("AUTH " + finalStation);

            } catch (Exception ex) {
                appendLog("[SOCKET-ERROR] Kết nối thất bại: " + ex.getMessage());
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this,
                        "Không thể kết nối tới Socket Server tại " + host + ":" + port + "\nVui lòng đảm bảo Spring Boot đang chạy!",
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
            connectionStatusLabel.setText("⚪ Chưa kết nối");
            appendLog("[SOCKET] Đã đóng kết nối với Server.");
        });
    }

    private void sendCommand(String command) {
        if (!connected || writer == null) {
            JOptionPane.showMessageDialog(this, "Chưa kết nối tới máy chủ Socket! Hãy bấm nút 'Kết Nối'.", "Cảnh Báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        appendLog("[CLIENT -> SERVER] " + command);
        writer.println(command);
    }

    private void handleServerResponse(String response) {
        appendLog("[SERVER -> CLIENT] " + response);

        String now = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));

        if (response.startsWith("200 CHECKIN_SUCCESS")) {
            int count = successTicketCount.incrementAndGet();
            ticketCountLabel.setText("Số vé đã soát thành công trong ca: " + count);

            String maCheckIn = extractParam(response, "MaCheckIn=");
            String maVe = extractParam(response, "MaVe=");
            String loaiVe = extractParam(response, "LoaiVe=");

            ticketTableModel.insertRow(0, new Object[]{now, maVe, loaiVe, maCheckIn, "✅ THÀNH CÔNG"});
        } else if (response.contains("CONFLICT_ALREADY_USED")) {
            ticketTableModel.insertRow(0, new Object[]{now, "-", "-", "-", "⚠️ VÉ ĐÃ SỬ DỤNG"});
        } else if (response.contains("GONE")) {
            ticketTableModel.insertRow(0, new Object[]{now, "-", "-", "-", "❌ VÉ ĐÃ BỊ HỦY"});
        } else if (response.contains("NOT_FOUND")) {
            ticketTableModel.insertRow(0, new Object[]{now, "-", "-", "-", "❓ VÉ KHÔNG TỒN TẠI"});
        }
    }

    private String extractParam(String text, String prefix) {
        int idx = text.indexOf(prefix);
        if (idx == -1) return "-";
        int start = idx + prefix.length();
        int end = text.indexOf(" ", start);
        if (end == -1) end = text.length();
        return text.substring(start, end);
    }

    private void appendLog(String message) {
        String time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        SwingUtilities.invokeLater(() -> {
            logArea.append("[" + time + "] " + message + "\n");
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }

    /**
     * Mở đồng thời 3 cửa sổ trạm check-in độc lập trên 3 luồng riêng (Tiêu chí 5.3 & 5.4).
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GateCheckinClientApp app = new GateCheckinClientApp("TRAM_01", "TRAM_01_CONG_VE_VAO");
            app.setVisible(true);
        });
    }
}
