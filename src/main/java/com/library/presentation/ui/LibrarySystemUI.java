package com.library.presentation.ui;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.geom.RoundRectangle2D;

/**
 * Swing Login UI that visually matches the JavaFX login.fxml + login.css:
 * - Left branding panel with title & welcome text
 * - Right form: username, password, "Remember me" (optional), Login button
 * - Colors & sizes adapted from the CSS
 *
 * Integration: pass your real com.library.application.AuthService to the constructor.
 */
public class LibrarySystemUI extends JFrame {

    // --- Color tokens from login.css ---
    private static final Color PINK_BG_LEFT   = Color.decode("#ffeaf5"); // .login-left
    private static final Color BRAND_PINK     = Color.decode("#C2185B"); // .title / .login-button bg
    private static final Color INPUT_BG       = Color.decode("#f8d2e3"); // .input-field background
    private static final Color INPUT_FG       = Color.decode("#333333");
    private static final Color WELCOME_GRAY   = Color.decode("#888888");

    // Radii to mimic rounded styles
    private static final int INPUT_RADIUS = 12;
    private static final int BUTTON_RADIUS = 30;

    // Services
    public interface AuthService {
        boolean login(String username, String password);
    }

    private final AuthService authService;

    // UI elements
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JLabel feedbackLabel;

    public LibrarySystemUI(AuthService authService) {
        super("Library System — Login");
        this.authService = authService;
        initLookAndFeel();
        initUI();
        pack();
        setLocationRelativeTo(null);
        // Ensure fields can receive input and focus when the frame appears
        SwingUtilities.invokeLater(() -> {
            if (usernameField != null) {
                usernameField.setEditable(true);
                usernameField.setEnabled(true);
                usernameField.requestFocusInWindow();
            }
            if (passwordField != null) {
                passwordField.setEditable(true);
                passwordField.setEnabled(true);
            }
        });
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    private void initLookAndFeel() {
        // Clean, modern defaults without external dependencies
        UIManager.put("Button.font", new Font("Segoe UI", Font.PLAIN, 14));
        UIManager.put("Label.font", new Font("Segoe UI", Font.PLAIN, 14));
        UIManager.put("TextField.font", new Font("Segoe UI", Font.PLAIN, 14));
        UIManager.put("PasswordField.font", new Font("Segoe UI", Font.PLAIN, 14));
        // Frame background
        getContentPane().setBackground(Color.WHITE);
    }

    private void initUI() {
        // Root layout: two columns
        JPanel root = new JPanel(new BorderLayout());
        root.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        root.add(buildLeftPanel(), BorderLayout.WEST);
        root.add(buildRightPanel(), BorderLayout.CENTER);
        setContentPane(root);
    }

    private JComponent buildLeftPanel() {
        JPanel left = new JPanel();
        left.setBackground(PINK_BG_LEFT); // matches .login-left
        left.setPreferredSize(new Dimension(320, 460));
        left.setLayout(new GridBagLayout());

        JLabel title = new JLabel("Welcome to the Library");
        title.setForeground(BRAND_PINK); // matches .title color
        title.setFont(new Font("Segoe UI", Font.BOLD, 28)); // matches .title size ~28px

        JLabel welcome = new JLabel("<html><div style='text-align:center;'>Manage books, users, and loans<br/>all in one place.</div></html>");
        welcome.setForeground(WELCOME_GRAY); // .welcome-label
        welcome.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        JPanel inner = new JPanel();
        inner.setOpaque(false);
        inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        welcome.setAlignmentX(Component.CENTER_ALIGNMENT);

        inner.add(title);
        inner.add(Box.createVerticalStrut(12));
        inner.add(welcome);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.CENTER;
        left.add(inner, gbc);

        return decorateCard(left);
    }

    private JComponent buildRightPanel() {
        JPanel right = new JPanel();
        right.setOpaque(false);
        right.setLayout(new GridBagLayout());

        JLabel signInTitle = new JLabel("Sign in");
        signInTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        signInTitle.setForeground(new Color(30, 30, 30));

        JLabel userLbl = new JLabel("Username");
        usernameField = new JTextField(20);
        styleInput(usernameField); // rounded, pink bg

        JLabel passLbl = new JLabel("Password");
        passwordField = new JPasswordField(20);
        styleInput(passwordField);

        JCheckBox rememberMe = new JCheckBox("Remember me");
        rememberMe.setOpaque(false);

        JButton loginButton = new JButton("Login");
        stylePillButton(loginButton);
        loginButton.addActionListener(this::onLogin);

        feedbackLabel = new JLabel(" ");
        feedbackLabel.setForeground(new Color(180, 0, 0));
        feedbackLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.gridx = 0; c.gridy = 0; c.gridwidth = 2; c.anchor = GridBagConstraints.WEST;
        right.add(signInTitle, c);

        c.gridy++; c.gridwidth = 1;
        right.add(userLbl, c);

        c.gridx = 1;
        right.add(usernameField, c);

        c.gridx = 0; c.gridy++;
        right.add(passLbl, c);

        c.gridx = 1;
        right.add(passwordField, c);

        c.gridx = 0; c.gridy++; c.gridwidth = 2; c.anchor = GridBagConstraints.WEST;
        right.add(rememberMe, c);

        c.gridy++; c.anchor = GridBagConstraints.CENTER;
        right.add(loginButton, c);

        c.gridy++; c.insets = new Insets(2, 6, 6, 6);
        right.add(feedbackLabel, c);

        return decorateCard(right);
    }

    private JComponent decorateCard(JComponent c) {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(c, BorderLayout.CENTER);
        wrapper.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        return wrapper;
    }

    private void styleInput(JComponent field) {
        field.setBackground(INPUT_BG); // .input-field background
        field.setForeground(INPUT_FG);
        // Use rounded outline border but let the component paint its background so text/caret remain visible
        field.setBorder(new RoundedFillBorder(INPUT_BG, new Insets(10, 12, 10, 12), INPUT_RADIUS));
        field.setOpaque(true);
        field.setPreferredSize(new Dimension(280, 40)); // .input-field -fx-pref-width: 280px
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14)); // .input-field font-size 14px
    }

    private void stylePillButton(AbstractButton b) {
        b.setFocusPainted(false);
        b.setForeground(Color.WHITE);               // .login-button text
        b.setBackground(BRAND_PINK);                // .login-button bg color
        b.setFont(new Font("Segoe UI", Font.BOLD, 14)); // .login-button font-size + weight
        // Use empty border so the button contents have padding; keep button opaque so background shows
        b.setBorder(BorderFactory.createEmptyBorder(10, 22, 10, 22));
        b.setOpaque(true);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private void onLogin(ActionEvent e) {
        String user = usernameField.getText().trim();
        String pass = new String(passwordField.getPassword());

        if (user.isEmpty() || pass.isEmpty()) {
            showError("Please enter username & password.");
            return;
        }

        boolean ok;
        try {
            ok = authService != null && authService.login(user, pass);
        } catch (Exception ex) {
            showError("Login failed: " + ex.getMessage());
            return;
        }

        if (ok) {
            showSuccess("Login successful.");
            // TODO: navigate to Admin/Home dashboard frame
            // e.g., new DashboardFrame(...).setVisible(true); dispose();
        } else {
            showError("Invalid credentials. Try again.");
        }
    }

    private void showError(String msg) {
        feedbackLabel.setText(msg);
        feedbackLabel.setForeground(new Color(180, 0, 0));
        // subtle shake animation
        shake(this.getRootPane());
    }

    private void showSuccess(String msg) {
        feedbackLabel.setText(msg);
        feedbackLabel.setForeground(new Color(0, 128, 0));
    }

    private void shake(JComponent comp) {
        final Point p0 = comp.getLocation();
        final int[] dx = {0, 8, -8, 6, -6, 4, -4, 2, -2, 0};
        new Thread(() -> {
            try {
                for (int d : dx) {
                    SwingUtilities.invokeAndWait(() ->
                            comp.setLocation(p0.x + d, p0.y)
                    );
                    Thread.sleep(15);
                }
                SwingUtilities.invokeLater(() -> comp.setLocation(p0));
            } catch (Exception ignored) { }
        }).start();
    }

    /**
     * Helpers for quick programmatic checks/tests.
     */
    public boolean isUsernameEditable() {
        return usernameField != null && usernameField.isEnabled() && usernameField.isEditable();
    }

    public boolean isPasswordEditable() {
        return passwordField != null && passwordField.isEnabled() && passwordField.isEditable();
    }

    public void setUsernameText(String t) {
        if (usernameField != null) usernameField.setText(t);
    }

    public void setPasswordText(String t) {
        if (passwordField != null) passwordField.setText(t);
    }

    // --- Rounded border that also paints the fill (for inputs & button) ---
    private static class RoundedFillBorder extends AbstractBorder {
        private final Color fill;
        private final Insets insets;
        private final int radius;

        RoundedFillBorder(Color fill, Insets insets, int radius) {
            this.fill = fill;
            this.insets = insets;
            this.radius = radius;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Shape rr = new RoundRectangle2D.Float(x, y, width - 1, height - 1, radius, radius);

            // Draw only an outline so the component's own text/caret remain visible
            g2.setColor(new Color(0, 0, 0, 40));
            g2.draw(rr);

            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(insets.top, insets.left, insets.bottom, insets.right);
        }

        @Override
        public Insets getBorderInsets(Component c, Insets insets) {
            insets.top = this.insets.top;
            insets.left = this.insets.left;
            insets.bottom = this.insets.bottom;
            insets.right = this.insets.right;
            return insets;
        }
    }

    // --- Demo main (remove in production) ---
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AuthService fake =
                    (u, p) -> "admin".equals(u) && "admin".equals(p);

            LibrarySystemUI frame = new LibrarySystemUI(fake);
            frame.setVisible(true);
        });
    }

}
