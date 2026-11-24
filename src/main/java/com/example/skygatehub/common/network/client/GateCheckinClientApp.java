package com.example.skygatehub.common.network.client;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Ứng dụng Client Socket đồ họa Java Swing (Tiêu chí 5.3, 5.4).
 * Hỗ trợ 3 trạm soát vé/lễ tân/nhà hàng độc lập và Tab Stress Test đa luồng
 * để demo trực quan xử lý xung đột (Pessimistic Lock + synchronized) qua TCP Socket.
 */
public class GateCheckinClientApp extends JFrame {

    private final String defaultStationName;

    private JTextField hostField;
    private JTextField portField;
    private JComboBox<String> stationCombo;
    private JButton connectBtn;
    private JButton disconnectBtn;
    private JButton pingBtn;
    private JButton statsBtn;
    private JLabel connectionStatusLabel;
    private JTabbedPane tabbedPane;

    // Tab 1: Cổng Vé
    private JTextField ticketCodeField;
    private DefaultTableModel ticketTableModel;
    private JLabel ticketCountLabel;
    private final AtomicInteger successTicketCount = new AtomicInteger(0);

    // Tab 2: Quầy Lưu Trú
    private JTextField roomCodeField;
    private DefaultTableModel roomTableModel;
    private JLabel roomCountLabel;
    private final AtomicInteger successRoomCount = new AtomicInteger(0);

    // Tab 3: Nhà Hàng
    private JTextField diningCodeField;
    private DefaultTableModel diningTableModel;
    private JLabel diningCountLabel;
    private final AtomicInteger successDiningCount = new AtomicInteger(0);

    // Tab 4: Demo Đa Luồng & Xung Đột
    private JComboBox<String> stressCommandCombo;
    private JTextField stressCodeField;
    private JSpinner stressThreadSpinner;
    private JButton runStressBtn;
    private JLabel stressSummaryLabel;
    private DefaultTableModel stressTableModel;

    private JTextPane logPane;

    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;
    private Thread listenerThread;
    private volatile boolean connected = false;
    private volatile int lastCommandTab = 0;

    public GateCheckinClientApp(String stationId, String defaultStationName) {
        this.defaultStationName = defaultStationName;
        initUI();
    }

    private void initUI() {
        setTitle("SkyGateHub - Hệ Thống Soát Vé & Demo Socket Đa Luồng [" + defaultStationName + "]");
        setSize(1020, 760);
        setMinimumSize(new Dimension(900, 620));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(12, 12, 12, 12));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        topPanel.setBorder(BorderFactory.createTitledBorder("Cấu hình kết nối TCP Socket (Cổng 9090)"));

        topPanel.add(new JLabel("Máy chủ:"));
        hostField = new JTextField("localhost", 9);
        topPanel.add(hostField);

        topPanel.add(new JLabel("Cổng:"));
        portField = new JTextField("9090", 4);
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

        pingBtn = new JButton("PING");
        pingBtn.setEnabled(false);
        pingBtn.addActionListener(e -> sendCommand("PING"));
        topPanel.add(pingBtn);

        statsBtn = new JButton("STATS");
        statsBtn.setEnabled(false);
        statsBtn.addActionListener(e -> sendCommand("STATS"));
        topPanel.add(statsBtn);

        JButton openAllStationsBtn = new JButton("⚡ Mở 3 Trạm Đồng Thời");
        openAllStationsBtn.setBackground(new Color(13, 110, 253));
        openAllStationsBtn.setForeground(Color.WHITE);
        openAllStationsBtn.addActionListener(e -> openThreeIndependentStations());
        topPanel.add(openAllStationsBtn);

        connectionStatusLabel = new JLabel("⚪ Chưa kết nối");
        connectionStatusLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        topPanel.add(connectionStatusLabel);

        mainPanel.add(topPanel, BorderLayout.NORTH);

        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("🎟️ Trạm 1: Cổng Vé", createTicketStationTab());
        tabbedPane.addTab("🏨 Trạm 2: Quầy Lưu Trú", createRoomStationTab());
        tabbedPane.addTab("🍽️ Trạm 3: Nhà Hàng", createDiningStationTab());
        tabbedPane.addTab("⚡ Demo Xung Đột & Đa Luồng", createConcurrencyDemoTab());

        JPanel centerPanel = new JPanel(new BorderLayout(6, 6));
        centerPanel.add(tabbedPane, BorderLayout.CENTER);

        logPane = new JTextPane();
        logPane.setEditable(false);
        logPane.setFont(new Font("Consolas", Font.PLAIN, 12));
        logPane.setBackground(new Color(248, 249, 250));
        JScrollPane scrollLog = new JScrollPane(logPane);
        scrollLog.setPreferredSize(new Dimension(960, 185));
        scrollLog.setBorder(BorderFactory.createTitledBorder("Nhật ký truyền nhận gói tin TCP (Socket Stream)"));
        centerPanel.add(scrollLog, BorderLayout.SOUTH);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        JLabel footerLabel = new JLabel("SkyGateHub Ecosystem — Phân hệ Soát vé Socket Client (RFC Text Protocol)");
        bottomPanel.add(footerLabel, BorderLayout.WEST);

        JButton clearBtn = new JButton("Xóa Nhật Ký");
        clearBtn.addActionListener(e -> logPane.setText(""));
        bottomPanel.add(clearBtn, BorderLayout.EAST);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);
        appendLog("[SYSTEM] Khởi động Swing Client. Sẵn sàng kết nối tới CheckInSocketServer.");
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

        JButton checkinTicketBtn = new JButton("🎟️ Soát Vé (CHECKIN_VE)");
        checkinTicketBtn.setBackground(new Color(13, 110, 253));
        checkinTicketBtn.setForeground(Color.WHITE);
        checkinTicketBtn.setFont(new Font("SansSerif", Font.BOLD, 13));
        checkinTicketBtn.addActionListener(e -> processTicketCheckin());
        controlPanel.add(checkinTicketBtn);

        JButton lookupTicketBtn = new JButton("🔍 Tra Cứu Vé");
        lookupTicketBtn.addActionListener(e -> {
            String code = ticketCodeField.getText().trim();
            if (code.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập mã vé cần tra cứu!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            lastCommandTab = 0;
            sendCommand("LOOKUP " + code);
        });
        controlPanel.add(lookupTicketBtn);

        ticketCountLabel = new JLabel("Số vé đã soát thành công trong ca: 0");
        ticketCountLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        ticketCountLabel.setForeground(new Color(25, 135, 84));
        controlPanel.add(ticketCountLabel);

        panel.add(controlPanel, BorderLayout.NORTH);

        String[] columns = {"Thời gian", "Mã Vé / Chuỗi", "Loại Vé", "Mã Check-in", "Kết Quả"};
        ticketTableModel = createReadOnlyModel(columns);
        JTable ticketTable = createStyledTable(ticketTableModel, 4);
        JScrollPane tableScroll = new JScrollPane(ticketTable);
        tableScroll.setBorder(BorderFactory.createTitledBorder("Danh sách vé vừa soát tại Trạm 1: Cổng Vé"));
        panel.add(tableScroll, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createRoomStationTab() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        controlPanel.add(new JLabel("Mã đặt phòng / Mã đơn hàng:"));
        roomCodeField = new JTextField(20);
        roomCodeField.setFont(new Font("Consolas", Font.BOLD, 14));
        roomCodeField.addActionListener(e -> processRoomCheckin());
        controlPanel.add(roomCodeField);

        JButton checkinRoomBtn = new JButton("🏨 Check-in Nhận Phòng");
        checkinRoomBtn.setBackground(new Color(25, 135, 84));
        checkinRoomBtn.setForeground(Color.WHITE);
        checkinRoomBtn.setFont(new Font("SansSerif", Font.BOLD, 13));
        checkinRoomBtn.addActionListener(e -> processRoomCheckin());
        controlPanel.add(checkinRoomBtn);

        JButton lookupRoomBtn = new JButton("🔍 Tra Cứu Phòng");
        lookupRoomBtn.addActionListener(e -> {
            String code = roomCodeField.getText().trim();
            if (code.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập mã đặt phòng cần tra cứu!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            lastCommandTab = 1;
            sendCommand("LOOKUP " + code);
        });
        controlPanel.add(lookupRoomBtn);

        roomCountLabel = new JLabel("Số lượt nhận phòng thành công: 0");
        roomCountLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        roomCountLabel.setForeground(new Color(25, 135, 84));
        controlPanel.add(roomCountLabel);

        panel.add(controlPanel, BorderLayout.NORTH);

        String[] columns = {"Thời gian", "Mã Đặt Phòng", "Loại Phòng", "Số Phòng Nhận", "Kết Quả"};
        roomTableModel = createReadOnlyModel(columns);
        JTable roomTable = createStyledTable(roomTableModel, 4);
        JScrollPane tableScroll = new JScrollPane(roomTable);
        tableScroll.setBorder(BorderFactory.createTitledBorder("Lịch sử nhận phòng Trạm 2: Quầy Lưu Trú"));
        panel.add(tableScroll, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createDiningStationTab() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        controlPanel.add(new JLabel("Mã đặt bàn / Mã đơn hàng:"));
        diningCodeField = new JTextField(20);
        diningCodeField.setFont(new Font("Consolas", Font.BOLD, 14));
        diningCodeField.addActionListener(e -> processDiningCheckin());
        controlPanel.add(diningCodeField);

        JButton checkinDiningBtn = new JButton("🍽️ Check-in Vào Bàn Ăn");
        checkinDiningBtn.setBackground(new Color(255, 193, 7));
        checkinDiningBtn.setFont(new Font("SansSerif", Font.BOLD, 13));
        checkinDiningBtn.addActionListener(e -> processDiningCheckin());
        controlPanel.add(checkinDiningBtn);

        JButton lookupDiningBtn = new JButton("🔍 Tra Cứu Bàn");
        lookupDiningBtn.addActionListener(e -> {
            String code = diningCodeField.getText().trim();
            if (code.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập mã đặt bàn cần tra cứu!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            lastCommandTab = 2;
            sendCommand("LOOKUP " + code);
        });
        controlPanel.add(lookupDiningBtn);

        diningCountLabel = new JLabel("Số lượt nhận bàn thành công: 0");
        diningCountLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        diningCountLabel.setForeground(new Color(25, 135, 84));
        controlPanel.add(diningCountLabel);

        panel.add(controlPanel, BorderLayout.NORTH);

        String[] columns = {"Thời gian", "Mã Đặt Bàn", "Số Khách", "Số Bàn", "Kết Quả"};
        diningTableModel = createReadOnlyModel(columns);
        JTable diningTable = createStyledTable(diningTableModel, 4);
        JScrollPane tableScroll = new JScrollPane(diningTable);
        tableScroll.setBorder(BorderFactory.createTitledBorder("Lịch sử check-in Trạm 3: Nhà Hàng Ẩm Thực"));
        panel.add(tableScroll, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Tab 4: Demo trực quan khả năng xử lý đồng thời, TCP SocketWorker và chống xung đột (Race Condition).
     */
    private JPanel createConcurrencyDemoTab() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel topBox = new JPanel(new BorderLayout(6, 6));
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));

        controlPanel.add(new JLabel("Loại lệnh:"));
        stressCommandCombo = new JComboBox<>(new String[]{"CHECKIN_VE", "CHECKIN_PHONG", "CHECKIN_AN"});
        controlPanel.add(stressCommandCombo);

        controlPanel.add(new JLabel("Mã mục tiêu:"));
        stressCodeField = new JTextField("TK-2026-0003", 16);
        stressCodeField.setFont(new Font("Consolas", Font.BOLD, 14));
        controlPanel.add(stressCodeField);

        controlPanel.add(new JLabel("Số luồng bắn đồng thời:"));
        stressThreadSpinner = new JSpinner(new SpinnerNumberModel(10, 2, 50, 1));
        controlPanel.add(stressThreadSpinner);

        runStressBtn = new JButton("🚀 Bắn Đồng Thời (Demo Xung Đột)");
        runStressBtn.setBackground(new Color(111, 66, 193));
        runStressBtn.setForeground(Color.WHITE);
        runStressBtn.setFont(new Font("SansSerif", Font.BOLD, 13));
        runStressBtn.addActionListener(e -> runConcurrencyStressTest());
        controlPanel.add(runStressBtn);

        topBox.add(controlPanel, BorderLayout.NORTH);

        stressSummaryLabel = new JLabel("Sẵn sàng: Nhập mã hợp lệ và bấm 'Bắn Đồng Thời' để mở N kết nối Socket cùng lúc (chỉ 1 luồng thắng, các luồng còn lại bị 409 CONFLICT chặn).");
        stressSummaryLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        stressSummaryLabel.setForeground(new Color(73, 80, 87));
        stressSummaryLabel.setBorder(new EmptyBorder(2, 10, 4, 10));
        topBox.add(stressSummaryLabel, BorderLayout.SOUTH);

        panel.add(topBox, BorderLayout.NORTH);

        String[] columns = {"Client Thread", "Server Thread (SocketWorker)", "Thời gian (ms)", "Trạng Thái", "Phản hồi từ Server"};
        stressTableModel = createReadOnlyModel(columns);
        JTable stressTable = createStyledTable(stressTableModel, 3);
        stressTable.getColumnModel().getColumn(0).setPreferredWidth(110);
        stressTable.getColumnModel().getColumn(1).setPreferredWidth(170);
        stressTable.getColumnModel().getColumn(2).setPreferredWidth(95);
        stressTable.getColumnModel().getColumn(3).setPreferredWidth(140);
        stressTable.getColumnModel().getColumn(4).setPreferredWidth(420);

        JScrollPane scroll = new JScrollPane(stressTable);
        scroll.setBorder(BorderFactory.createTitledBorder("Kết quả đua tranh đa luồng qua TCP Socket"));
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    private DefaultTableModel createReadOnlyModel(String[] columns) {
        return new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    private JTable createStyledTable(DefaultTableModel model, int statusColumnIndex) {
        JTable table = new JTable(model);
        table.setRowHeight(25);
        table.setFont(new Font("SansSerif", Font.PLAIN, 12));
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tbl, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(tbl, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    Object statusVal = tbl.getValueAt(row, statusColumnIndex);
                    String status = statusVal != null ? statusVal.toString() : "";
                    if (status.contains("THÀNH CÔNG") || status.startsWith("200")) {
                        c.setBackground(new Color(232, 248, 238));
                        c.setForeground(new Color(20, 108, 67));
                    } else if (status.contains("XUNG ĐỘT") || status.contains("ĐÃ SỬ DỤNG") || status.contains("409") || status.contains("⚠️")) {
                        c.setBackground(new Color(255, 243, 224));
                        c.setForeground(new Color(180, 83, 9));
                    } else if (status.contains("HỦY") || status.contains("KHÔNG TỒN TẠI") || status.contains("❌") || status.contains("❓")) {
                        c.setBackground(new Color(253, 237, 237));
                        c.setForeground(new Color(176, 42, 55));
                    } else {
                        c.setBackground(Color.WHITE);
                        c.setForeground(Color.DARK_GRAY);
                    }
                }
                return c;
            }
        });
        return table;
    }

    private void processTicketCheckin() {
        String code = ticketCodeField.getText().trim();
        if (code.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập hoặc quét mã vé!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        lastCommandTab = 0;
        sendCommand("CHECKIN_VE " + code);
        ticketCodeField.setText("");
        ticketCodeField.requestFocus();
    }

    private void processRoomCheckin() {
        String code = roomCodeField.getText().trim();
        if (code.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập mã đặt phòng (ví dụ DP003) hoặc mã đơn hàng!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        lastCommandTab = 1;
        sendCommand("CHECKIN_PHONG|" + code + "|ND004");
        roomCodeField.setText("");
        roomCodeField.requestFocus();
    }

    private void processDiningCheckin() {
        String code = diningCodeField.getText().trim();
        if (code.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập mã đặt bàn (ví dụ DB001) hoặc mã đơn hàng!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        lastCommandTab = 2;
        sendCommand("CHECKIN_AN|" + code);
        diningCodeField.setText("");
        diningCodeField.requestFocus();
    }

    private void runConcurrencyStressTest() {
        String cmdType = (String) stressCommandCombo.getSelectedItem();
        String targetCode = stressCodeField.getText().trim();
        if (targetCode.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập mã mục tiêu để chạy stress test!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String host = hostField.getText().trim();
        int port;
        try {
            port = Integer.parseInt(portField.getText().trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Cổng Port không hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int threadCount = (Integer) stressThreadSpinner.getValue();
        runStressBtn.setEnabled(false);
        stressTableModel.setRowCount(0);
        stressSummaryLabel.setText("⏳ Đang khởi tạo " + threadCount + " kết nối TCP Socket song song tới " + host + ":" + port + "...");
        appendLog("[STRESS] Bắt đầu bắn " + threadCount + " luồng đồng thời: " + cmdType + " -> " + targetCode);

        new Thread(() -> {
            CountDownLatch readyLatch = new CountDownLatch(threadCount);
            CountDownLatch startLatch = new CountDownLatch(1);
            CountDownLatch doneLatch = new CountDownLatch(threadCount);

            AtomicInteger okCount = new AtomicInteger(0);
            AtomicInteger conflictCount = new AtomicInteger(0);
            AtomicInteger otherCount = new AtomicInteger(0);

            for (int i = 1; i <= threadCount; i++) {
                final int idx = i;
                new Thread(() -> {
                    String clientName = "ClientThread-" + idx;
                    String serverWorker = "-";
                    String statusLabel = "LỖI KẾT NỐI";
                    String detail = "";
                    long elapsedMs = 0;

                    try (Socket s = new Socket(host, port);
                         BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream(), StandardCharsets.UTF_8));
                         PrintWriter pw = new PrintWriter(s.getOutputStream(), true, StandardCharsets.UTF_8)) {

                        br.readLine(); // Banner 200 WELCOME
                        pw.println("AUTH|STRESS_TRAM_" + idx);
                        br.readLine(); // 200 AUTH_OK
                        pw.println("PING");
                        String pong = br.readLine();
                        serverWorker = extractParam(pong != null ? pong : "", "ServerThread=");

                        readyLatch.countDown();
                        startLatch.await();

                        long t0 = System.nanoTime();
                        pw.println(cmdType + "|" + targetCode);
                        String resp = br.readLine();
                        elapsedMs = (System.nanoTime() - t0) / 1_000_000;
                        detail = resp != null ? resp : "NULL";

                        if (detail.startsWith("200")) {
                            okCount.incrementAndGet();
                            statusLabel = "200 THÀNH CÔNG ✅";
                        } else if (detail.startsWith("409")) {
                            conflictCount.incrementAndGet();
                            statusLabel = "409 XUNG ĐỘT ⚠️";
                        } else {
                            otherCount.incrementAndGet();
                            statusLabel = detail.split("\\s+")[0] + " TỪ CHỐI ❌";
                        }

                        pw.println("QUIT");
                    } catch (Exception ex) {
                        otherCount.incrementAndGet();
                        detail = ex.getMessage();
                        readyLatch.countDown();
                    } finally {
                        final String rowWorker = serverWorker;
                        final long rowMs = elapsedMs;
                        final String rowStatus = statusLabel;
                        final String rowDetail = detail;
                        SwingUtilities.invokeLater(() -> stressTableModel.addRow(
                                new Object[]{clientName, rowWorker, rowMs + " ms", rowStatus, rowDetail}));
                        doneLatch.countDown();
                    }
                }, "StressClient-" + idx).start();
            }

            try {
                readyLatch.await();
                startLatch.countDown();
                doneLatch.await();
            } catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
            }

            SwingUtilities.invokeLater(() -> {
                runStressBtn.setEnabled(true);
                String summary = String.format(
                        "Hoàn tất %d luồng đồng thời — ✅ Thành công: %d | ⚠️ Bị chặn xung đột (409): %d | ❌ Khác: %d",
                        threadCount, okCount.get(), conflictCount.get(), otherCount.get());
                stressSummaryLabel.setText(summary);
                appendLog("[STRESS] " + summary);
            });
        }, "StressCoordinator").start();
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
                    pingBtn.setEnabled(true);
                    statsBtn.setEnabled(true);
                    hostField.setEnabled(false);
                    portField.setEnabled(false);
                    stationCombo.setEnabled(false);
                    connectionStatusLabel.setText("🟢 Đã kết nối (" + finalStation + ")");
                });

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
            pingBtn.setEnabled(false);
            statsBtn.setEnabled(false);
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

        if (response.contains("CHECKIN_SUCCESS") && response.contains("MaVe=")) {
            int count = successTicketCount.incrementAndGet();
            ticketCountLabel.setText("Số vé đã soát thành công trong ca: " + count);
            ticketTableModel.insertRow(0, new Object[]{
                    now,
                    extractParam(response, "MaVe="),
                    extractParam(response, "LoaiVe="),
                    extractParam(response, "MaCheckIn="),
                    "✅ THÀNH CÔNG"
            });
        } else if (response.contains("CHECKIN_PHONG_OK")) {
            int count = successRoomCount.incrementAndGet();
            roomCountLabel.setText("Số lượt nhận phòng thành công: " + count);
            roomTableModel.insertRow(0, new Object[]{
                    now,
                    extractParam(response, "MaDatPhong="),
                    extractParam(response, "LoaiPhong="),
                    extractParam(response, "SoPhong="),
                    "✅ CHECK-IN THÀNH CÔNG"
            });
        } else if (response.contains("CHECKIN_AN_OK") || (response.contains("CHECKIN_SUCCESS") && response.contains("MaDatBan="))) {
            int count = successDiningCount.incrementAndGet();
            diningCountLabel.setText("Số lượt nhận bàn thành công: " + count);
            diningTableModel.insertRow(0, new Object[]{
                    now,
                    extractParam(response, "MaDatBan="),
                    extractParam(response, "SoKhach="),
                    extractParam(response, "SoBan="),
                    "✅ CHECK-IN THÀNH CÔNG"
            });
        } else if (response.contains("CONFLICT_ALREADY_USED")) {
            ticketTableModel.insertRow(0, new Object[]{now, "-", "-", "-", "⚠️ VÉ ĐÃ SỬ DỤNG"});
        } else if (response.contains("CONFLICT") || response.contains("GONE") || response.contains("NOT_FOUND")) {
            String statusText = response.contains("GONE")
                    ? "❌ ĐÃ BỊ HỦY"
                    : (response.contains("NOT_FOUND") ? "❓ KHÔNG TỒN TẠI" : "⚠️ " + response);
            DefaultTableModel target = getTargetTableModel();
            target.insertRow(0, new Object[]{now, "-", "-", "-", statusText});
        }
    }

    private DefaultTableModel getTargetTableModel() {
        if (lastCommandTab == 1) return roomTableModel;
        if (lastCommandTab == 2) return diningTableModel;
        return ticketTableModel;
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
        String line = "[" + time + "] " + message + "\n";
        SwingUtilities.invokeLater(() -> {
            StyledDocument doc = logPane.getStyledDocument();
            SimpleAttributeSet attrs = new SimpleAttributeSet();
            if (message.startsWith("[CLIENT -> SERVER]")) {
                StyleConstants.setForeground(attrs, new Color(13, 110, 253));
            } else if (message.contains("409 CONFLICT") || message.contains("⚠️")) {
                StyleConstants.setForeground(attrs, new Color(194, 65, 12));
                StyleConstants.setBold(attrs, true);
            } else if (message.contains("404 ") || message.contains("410 ") || message.contains("ERROR")) {
                StyleConstants.setForeground(attrs, new Color(220, 53, 69));
            } else if (message.contains("200 ")) {
                StyleConstants.setForeground(attrs, new Color(25, 135, 84));
            } else if (message.startsWith("[STRESS]")) {
                StyleConstants.setForeground(attrs, new Color(111, 66, 193));
                StyleConstants.setBold(attrs, true);
            } else {
                StyleConstants.setForeground(attrs, new Color(52, 58, 64));
            }
            try {
                doc.insertString(doc.getLength(), line, attrs);
                logPane.setCaretPosition(doc.getLength());
            } catch (Exception ignored) {}
        });
    }

    private void openThreeIndependentStations() {
        SwingUtilities.invokeLater(() -> {
            GateCheckinClientApp app1 = new GateCheckinClientApp("TRAM_01", "TRAM_01_CONG_VE_VAO");
            app1.setLocation(40, 40);
            app1.setVisible(true);

            GateCheckinClientApp app2 = new GateCheckinClientApp("TRAM_02", "TRAM_02_QUAY_LE_TAN_PHONG");
            app2.setLocation(220, 90);
            app2.setVisible(true);

            GateCheckinClientApp app3 = new GateCheckinClientApp("TRAM_03", "TRAM_03_NHA_HANG_AM_THUC");
            app3.setLocation(400, 140);
            app3.setVisible(true);
        });
    }

    /**
     * Mở ứng dụng kiểm thử trạm soát vé & lễ tân check-in.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GateCheckinClientApp app = new GateCheckinClientApp("TRAM_01", "TRAM_01_CONG_VE_VAO");
            app.setVisible(true);
        });
    }
}
