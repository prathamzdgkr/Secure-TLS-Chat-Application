package client;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.nio.file.Files;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

public class ChatFrame extends JFrame {

    private JPanel messageContainer;
    private JScrollPane scrollPane;
    private JTextField messageField;
    private ChatClient client;
    private String username;
    
    private DefaultListModel<String> activeUsersModel;
    private JList<String> activeUsersList;

    // Advanced, Modern Color Palette
    private final Color APP_BG = new Color(49, 51, 56);         
    private final Color SIDEBAR_BG = new Color(43, 45, 49);     
    private final Color HEADER_BG = new Color(49, 51, 56);      
    private final Color INPUT_WRAPPER = new Color(49, 51, 56);  
    private final Color INPUT_BG = new Color(56, 58, 64);       
    
    // UPDATED: Made BUBBLE_LEFT distinctly lighter than the background
    private final Color BUBBLE_LEFT = new Color(66, 69, 74);    
    private final Color BUBBLE_RIGHT = new Color(88, 101, 242); 
    
    private final Color TEXT_PRIMARY = new Color(242, 243, 245);
    private final Color TEXT_MUTED = new Color(148, 155, 164); 
    private final Color ACCENT = new Color(88, 101, 242);
    private final Color ACCENT_HOVER = new Color(71, 82, 196); 

    public ChatFrame(String username) {
        this.username = username;
        setTitle("MultiChat Workspace - " + username);
        setLayout(new BorderLayout());
        getContentPane().setBackground(APP_BG);

        // HEADER PANEL
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(HEADER_BG);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(30, 31, 34)), 
            new EmptyBorder(18, 25, 18, 25)
        ));
        
        JLabel headerLabel = new JLabel("Main Chat Room"); 
        headerLabel.setForeground(TEXT_PRIMARY);
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 17));
        
        JLabel settingsLabel = new JLabel("Network Status");
        settingsLabel.setForeground(TEXT_MUTED);
        settingsLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        settingsLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        settingsLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { showSettingsMenu(); }
            public void mouseEntered(MouseEvent e) { settingsLabel.setForeground(TEXT_PRIMARY); }
            public void mouseExited(MouseEvent e) { settingsLabel.setForeground(TEXT_MUTED); }
        });

        headerPanel.add(headerLabel, BorderLayout.WEST);
        headerPanel.add(settingsLabel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // MESSAGE CONTAINER
        messageContainer = new JPanel();
        messageContainer.setLayout(new BoxLayout(messageContainer, BoxLayout.Y_AXIS));
        messageContainer.setBackground(APP_BG);
        messageContainer.setBorder(new EmptyBorder(15, 15, 15, 15)); 

        scrollPane = new JScrollPane(messageContainer);
        scrollPane.setBorder(BorderFactory.createEmptyBorder()); 
        scrollPane.getVerticalScrollBar().setUnitIncrement(20); 
        
        scrollPane.getVerticalScrollBar().setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(30, 31, 34); 
                this.trackColor = APP_BG;
            }
            @Override
            protected JButton createDecreaseButton(int orientation) { return createZeroButton(); }
            @Override
            protected JButton createIncreaseButton(int orientation) { return createZeroButton(); }
            private JButton createZeroButton() {
                JButton jb = new JButton();
                jb.setPreferredSize(new Dimension(0, 0)); 
                return jb;
            }
        });
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(8, 0)); 
        add(scrollPane, BorderLayout.CENTER);

        // ACTIVE USERS SIDEBAR
        JPanel sidebarPanel = new JPanel(new BorderLayout());
        sidebarPanel.setBackground(SIDEBAR_BG);
        sidebarPanel.setPreferredSize(new Dimension(220, 0)); 

        JLabel sidebarTitle = new JLabel("ONLINE USERS");
        sidebarTitle.setForeground(TEXT_MUTED);
        sidebarTitle.setFont(new Font("Segoe UI", Font.BOLD, 11));
        sidebarTitle.setBorder(new EmptyBorder(18, 18, 8, 18)); 

        activeUsersModel = new DefaultListModel<>();
        activeUsersList = new JList<>(activeUsersModel);
        activeUsersList.setBackground(SIDEBAR_BG);
        activeUsersList.setSelectionBackground(new Color(53, 55, 60)); 
        activeUsersList.setSelectionForeground(TEXT_PRIMARY);
        activeUsersList.setCellRenderer(new UserListCellRenderer()); 
        
        sidebarPanel.add(sidebarTitle, BorderLayout.NORTH);
        sidebarPanel.add(activeUsersList, BorderLayout.CENTER);
        add(sidebarPanel, BorderLayout.EAST);

        // INPUT PANEL
        JPanel bottomWrapper = new JPanel(new BorderLayout(15, 0)); 
        bottomWrapper.setBackground(INPUT_WRAPPER);
        bottomWrapper.setBorder(new EmptyBorder(15, 25, 25, 25)); 

        ModernButton attachBtn = new ModernButton("Attach", new Color(64, 66, 73), new Color(80, 82, 90), TEXT_PRIMARY, 18);
        attachBtn.setPreferredSize(new Dimension(90, 45));
        attachBtn.addActionListener(e -> sendFile());

        RoundedPanel inputContainer = new RoundedPanel(new BorderLayout(), 18, INPUT_BG);
        inputContainer.setBorder(new EmptyBorder(0, 15, 0, 15)); 

        messageField = new JTextField("Type a message..."); 
        messageField.setBackground(INPUT_BG);
        messageField.setForeground(TEXT_MUTED);
        messageField.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        messageField.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0)); 
        messageField.setCaretColor(TEXT_PRIMARY);
        messageField.setPreferredSize(new Dimension(0, 45));

        messageField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (messageField.getText().equals("Type a message...")) {
                    messageField.setText("");
                    messageField.setForeground(TEXT_PRIMARY);
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (messageField.getText().isEmpty()) {
                    messageField.setForeground(TEXT_MUTED);
                    messageField.setText("Type a message...");
                }
            }
        });
        
        inputContainer.add(messageField, BorderLayout.CENTER);

        ModernButton sendBtn = new ModernButton("Send", ACCENT, ACCENT_HOVER, Color.WHITE, 18);
        sendBtn.setPreferredSize(new Dimension(90, 45));
        sendBtn.addActionListener(e -> send());
        messageField.addActionListener(e -> send()); 

        bottomWrapper.add(attachBtn, BorderLayout.WEST);
        bottomWrapper.add(inputContainer, BorderLayout.CENTER);
        bottomWrapper.add(sendBtn, BorderLayout.EAST);

        add(bottomWrapper, BorderLayout.SOUTH);

        setSize(1050, 680); 
        setLocationRelativeTo(null); 
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);

        try {
            client = new ChatClient(username, this); 
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Connection error. Ensure Server is running first.");
        }
    }

    private void sendFile() {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try {
                byte[] fileBytes = Files.readAllBytes(selectedFile.toPath());
                String base64String = Base64.getEncoder().encodeToString(fileBytes);
                
                String prefix = "";
                String currentText = messageField.getText().trim();
                if (currentText.startsWith("@")) {
                    int spaceIdx = currentText.indexOf(" ");
                    prefix = (spaceIdx != -1) ? currentText.substring(0, spaceIdx + 1) : currentText + " ";
                }
                
                client.send(prefix + "[FILE]" + selectedFile.getName() + "|" + base64String);
                
                if (prefix.isEmpty()) {
                    appendMessage("Sent a file: " + selectedFile.getName(), true);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error processing file: " + ex.getMessage());
            }
        }
    }

    public void receiveFile(String sender, String fileName, String base64Data) {
        SwingUtilities.invokeLater(() -> {
            int result = JOptionPane.showConfirmDialog(this,
                    sender + " sent a file: " + fileName + "\nDownload it?",
                    "Incoming File", JOptionPane.YES_NO_OPTION);
            
            if (result == JOptionPane.YES_OPTION) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setSelectedFile(new File(fileName)); 
                
                if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                    try {
                        byte[] fileBytes = Base64.getDecoder().decode(base64Data);
                        Files.write(fileChooser.getSelectedFile().toPath(), fileBytes);
                        appendMessage("[System] : Saved '" + fileName + "'", false);
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(this, "Error saving file.");
                    }
                }
            }
        });
    }

    private void showSettingsMenu() {
        JDialog dialog = new JDialog(this, "Network Connection Info", true);
        dialog.setSize(380, 240);
        dialog.setLayout(new BorderLayout());
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(APP_BG);

        String netData = (client != null && client.getSocket() != null) ?
            "Connected to " + client.getSocket().getInetAddress().getHostAddress() + ":" + client.getSocket().getPort() : "Not Connected";

        JLabel infoLabel = new JLabel("<html><center><br><b>Status:</b><br>" + netData + "</center></html>", SwingConstants.CENTER);
        infoLabel.setForeground(TEXT_PRIMARY);
        infoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        dialog.add(infoLabel, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout());
        btnPanel.setBackground(APP_BG);
        
        ModernButton pingBtn = new ModernButton("Ping Server (Test Latency)", ACCENT, ACCENT_HOVER, Color.WHITE, 12);
        pingBtn.setPreferredSize(new Dimension(220, 40));
        pingBtn.addActionListener(e -> { if (client != null) client.sendPing(); dialog.dispose(); });
        
        btnPanel.add(pingBtn);
        dialog.add(btnPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    public void updateActiveUsers(String[] users) {
        SwingUtilities.invokeLater(() -> {
            activeUsersModel.clear();
            for (String u : users) if (!u.isEmpty()) activeUsersModel.addElement(u); 
        });
    }

    private void send() {
        String msg = messageField.getText().trim();
        if (!msg.isEmpty() && !msg.equals("Type a message...")) {
            messageField.setText("");
            if (msg.equalsIgnoreCase("/ping")) { client.sendPing(); return; }
            client.send(msg);
            if (!msg.startsWith("@")) appendMessage(msg, true);
        }
    }

    // UPDATED: Separated the timestamp into a beautiful, muted sub-label
    public void appendMessage(String msg, boolean isSelf) {
        if (msg.startsWith("[Private to ")) isSelf = true; 
        final boolean finalIsSelf = isSelf;

        SwingUtilities.invokeLater(() -> {
            JPanel rowPanel = new JPanel(new FlowLayout(finalIsSelf ? FlowLayout.RIGHT : FlowLayout.LEFT));
            rowPanel.setOpaque(false);
            rowPanel.setBorder(new EmptyBorder(3, 0, 3, 0)); 

            Color bubbleColor = finalIsSelf ? BUBBLE_RIGHT : BUBBLE_LEFT;
            if (msg.startsWith("[Private") || msg.startsWith("[System]")) bubbleColor = new Color(59, 165, 93); 

            // Added a slight vertical gap (4px) between text and time
            RoundedPanel bubble = new RoundedPanel(new BorderLayout(0, 4), 18, bubbleColor);
            bubble.setBorder(new EmptyBorder(10, 16, 8, 16)); 

            // Main Message
            JTextArea textLabel = new JTextArea(msg);
            textLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
            textLabel.setForeground(Color.WHITE);
            textLabel.setOpaque(false);
            textLabel.setEditable(false);
            textLabel.setLineWrap(true);
            textLabel.setWrapStyleWord(true); 
            
            // Dedicated Timestamp Label
            String timeStr = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
            JLabel timeLabel = new JLabel(timeStr);
            timeLabel.setFont(new Font("Segoe UI", Font.BOLD, 10));
            timeLabel.setForeground(new Color(220, 225, 230)); // Sleek silver color
            timeLabel.setHorizontalAlignment(finalIsSelf ? SwingConstants.RIGHT : SwingConstants.LEFT);
            
            textLabel.setMaximumSize(new Dimension(480, Integer.MAX_VALUE));
            
            FontMetrics fm = textLabel.getFontMetrics(textLabel.getFont());
            int maxLineWidth = 0;
            for (String line : msg.split("\n")) maxLineWidth = Math.max(maxLineWidth, fm.stringWidth(line));
            
            FontMetrics timeFm = timeLabel.getFontMetrics(timeLabel.getFont());
            int timeWidth = timeFm.stringWidth(timeStr);

            // Dynamically adjust width to accommodate whichever is wider (text or time)
            int preferredWidth = Math.min(Math.max(maxLineWidth, timeWidth) + 10, 480); 
            textLabel.setPreferredSize(new Dimension(preferredWidth, textLabel.getPreferredSize().height));
            
            bubble.add(textLabel, BorderLayout.CENTER);
            bubble.add(timeLabel, BorderLayout.SOUTH);
            rowPanel.add(bubble);

            messageContainer.add(rowPanel);
            messageContainer.revalidate(); 
            messageContainer.repaint();

            JScrollBar vertical = scrollPane.getVerticalScrollBar();
            vertical.setValue(vertical.getMaximum());
        });
    }

    // --- CUSTOM UI COMPONENTS ---

    class ModernButton extends JButton {
        private Color normalColor;
        private Color hoverColor;
        private int radius;

        public ModernButton(String text, Color normalColor, Color hoverColor, Color textColor, int radius) {
            super(text);
            this.normalColor = normalColor;
            this.hoverColor = hoverColor;
            this.radius = radius;
            
            setForeground(textColor);
            setBackground(normalColor);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setFont(new Font("Segoe UI", Font.BOLD, 13));

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) { setBackground(hoverColor); }
                @Override
                public void mouseExited(MouseEvent e) { setBackground(normalColor); }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    class RoundedPanel extends JPanel {
        private Color bgColor;
        private int radius;
        public RoundedPanel(LayoutManager layout, int radius, Color bgColor) {
            super(layout); this.radius = radius; this.bgColor = bgColor; setOpaque(false); 
        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); 
            g2.setColor(bgColor != null ? bgColor : getBackground());
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
            super.paintComponent(g); 
        }
    }

    class UserListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            label.setText(" " + value.toString()); 
            label.setIcon(new OnlineIcon()); 
            label.setBorder(new EmptyBorder(10, 18, 10, 18)); 
            label.setBackground(isSelected ? new Color(53, 55, 60) : SIDEBAR_BG);
            label.setForeground(isSelected ? Color.WHITE : TEXT_MUTED);
            label.setFont(new Font("Segoe UI", Font.BOLD, 14));
            return label;
        }
    }

    class OnlineIcon implements Icon {
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(35, 165, 89)); 
            g2.fillOval(x, y + ((getIconHeight() - 10) / 2), 10, 10);
            g2.dispose();
        }
        public int getIconWidth() { return 18; }
        public int getIconHeight() { return 18; } 
    }
}