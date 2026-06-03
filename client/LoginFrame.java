package client;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class LoginFrame extends JFrame {

    private final Color BG_MAIN = new Color(30, 31, 34);
    private final Color CARD_BG = new Color(43, 45, 49);
    private final Color ACCENT = new Color(88, 101, 242);
    private final Color ACCENT_HOVER = new Color(71, 82, 196);
    private final Color TEXT_PRIMARY = new Color(242, 243, 245);
    private final Color TEXT_MUTED = new Color(181, 186, 193);
    private final Color INPUT_BG = new Color(30, 31, 34);

    public LoginFrame() {
        setTitle("MultiChat Connect");
        getContentPane().setBackground(BG_MAIN);
        setLayout(new GridBagLayout());

        JPanel cardPanel = new JPanel();
        cardPanel.setLayout(new BoxLayout(cardPanel, BoxLayout.Y_AXIS));
        cardPanel.setBackground(CARD_BG);
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(30, 31, 34), 1),
                new EmptyBorder(50, 50, 50, 50)
        ));

        JLabel titleLabel = new JLabel("MEMBER LOGIN");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Enter your username to connect");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitleLabel.setForeground(TEXT_MUTED);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel userLabel = new JLabel("USERNAME");
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 10));
        userLabel.setForeground(TEXT_MUTED);
        userLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel labelWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        labelWrapper.setOpaque(false);
        labelWrapper.setMaximumSize(new Dimension(300, 20));
        labelWrapper.add(userLabel);

        JTextField userField = new JTextField(15);
        userField.setMaximumSize(new Dimension(300, 40));
        userField.setPreferredSize(new Dimension(300, 40));
        userField.setBackground(INPUT_BG);
        userField.setForeground(TEXT_PRIMARY);
        userField.setCaretColor(TEXT_PRIMARY);
        userField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        userField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(60, 62, 68), 1),
                BorderFactory.createEmptyBorder(5, 12, 5, 12)));

        JButton loginBtn = new JButton("Connect");
        loginBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginBtn.setMaximumSize(new Dimension(300, 40));
        loginBtn.setBackground(ACCENT);
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFocusPainted(false);
        loginBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        loginBtn.setBorderPainted(false);
        loginBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        loginBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                loginBtn.setBackground(ACCENT_HOVER);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                loginBtn.setBackground(ACCENT);
            }
        });

        loginBtn.addActionListener(e -> {
            String user = userField.getText().trim();
            if (!user.isEmpty()) {
                new ChatFrame(user);
                dispose();
            } else {
                userField.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(250, 119, 124), 2),
                        BorderFactory.createEmptyBorder(5, 12, 5, 12)));
            }
        });

        userField.addActionListener(e -> loginBtn.doClick());

        cardPanel.add(titleLabel);
        cardPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        cardPanel.add(subtitleLabel);
        cardPanel.add(Box.createRigidArea(new Dimension(0, 35)));
        cardPanel.add(labelWrapper);
        cardPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        cardPanel.add(userField);
        cardPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        cardPanel.add(loginBtn);

        add(cardPanel);

        setSize(480, 520);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setVisible(true);
    }

    public static void main(String[] args) {
        System.setProperty("javax.net.ssl.trustStore", "serverkeystore.jks");
        System.setProperty("javax.net.ssl.trustStorePassword", System.getenv("TRUSTSTORE_PASSWORD"));
        SwingUtilities.invokeLater(LoginFrame::new);
    }
}