/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/*
 * GUI Implementation of ATM with Event Handling Concepts
 */
package atm;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.DecimalFormat;
import java.util.*;
import java.sql.Connection;
import java.sql.DriverManager;


/**
 *
 * @author hh
 */

/**
 * SimpleATMGUI.java
 * Replaced file-based persistence with MySQL DB using two tables:
 *  - users(account_no VARCHAR PRIMARY KEY, pin INT, balance DOUBLE)
 *  - admins(username VARCHAR PRIMARY KEY, password VARCHAR)
 *
 * Keep UI/UX unchanged from your prior design.
 */
public class SimpleATMGUI {
    private final JFrame frame;
    private final CardLayout cardLayout;
    private final JPanel mainPanel;

    private JTextField accountField;
    private JPasswordField pinField;
    private BankAccount currentAccount;

    private JLabel welcomeLabel;
    private JLabel balanceLabel;

    // Map is retained only as short-term cache (not source of truth).
    private final Map<String, BankAccount> accounts = new HashMap<>();
    private final DecimalFormat df = new DecimalFormat("#,##0.00");

    // DB connection info — change to match your MySQL credentials if different
    private static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/atm_system";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "alice"; // <-- set your password here
    
    public static Connection getConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private Connection conn;

    private DefaultTableModel adminTableModel;

    // Constructor
    public SimpleATMGUI() {
        // Connect to DB
        try {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,
                    "Unable to connect to database.\nPlease check DB_URL, DB_USER and DB_PASS.\n" + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            // If DB unavailable, still continue so user can see UI, but operations will fail.
        }

        frame = new JFrame("ATM System (Event-Driven GUI)");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 420);
        frame.setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        mainPanel.add(createLoginPanel(), "LOGIN");
        mainPanel.add(createUserDashboardPanel(), "USER_DASH");
        mainPanel.add(createAdminDashboardPanel(), "ADMIN_DASH");

        frame.setContentPane(mainPanel);
        frame.setVisible(true);
    }

    // ---------- DATABASE HELPERS ----------
    private void closeQuietly(AutoCloseable c) {
        if (c == null) return;
        try { c.close(); } catch (Exception ignored) {}
    }

    // Load all users from DB into the in-memory map (used for Admin table view)
    private void loadAccountsFromDatabase() {
        accounts.clear();
        if (conn == null) return;

        String sql = "SELECT account_no, pin, balance FROM users";
        try (PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                String accNo = rs.getString("account_no");
                int pin = rs.getInt("pin");
                double bal = rs.getDouble("balance");
                accounts.put(accNo, new BankAccount(accNo, pin, bal));
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(frame, "Error reading users from DB: " + ex.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Fetch one user from DB and return BankAccount object (or null)
    private BankAccount fetchAccountFromDB(String accountNo) {
        if (conn == null) return null;
        String sql = "SELECT account_no, pin, balance FROM users WHERE account_no = ?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, accountNo);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return new BankAccount(rs.getString("account_no"), rs.getInt("pin"), rs.getDouble("balance"));
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(frame, "Error fetching account: " + ex.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }

    // Update balance for account in DB (set absolute balance)
    private boolean updateBalanceInDB(String accountNo, double newBalance) {
        if (conn == null) return false;
        String sql = "UPDATE users SET balance = ? WHERE account_no = ?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setDouble(1, newBalance);
            pst.setString(2, accountNo);
            int updated = pst.executeUpdate();
            return updated > 0;
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(frame, "Error updating balance: " + ex.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    // Add user to DB
    private boolean insertUserToDB(String accountNo, int pin, double initialBalance) {
        if (conn == null) return false;
        String sql = "INSERT INTO users (account_no, pin, balance) VALUES (?, ?, ?)";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, accountNo);
            pst.setInt(2, pin);
            pst.setDouble(3, initialBalance);
            pst.executeUpdate();
            return true;
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(frame, "Error adding user: " + ex.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    // Verify admin credentials from admins table
    private boolean verifyAdmin(String username, String password) {
        if (conn == null) return username.equals("admin") && password.equals("admin123"); // fallback hard-coded
        String sql = "SELECT username FROM admins WHERE username = ? AND password = ?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, username);
            pst.setString(2, password);
            try (ResultSet rs = pst.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(frame, "Error verifying admin: " + ex.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    // Verify user credentials against DB and return BankAccount (or null)
    private BankAccount verifyUserAndFetchAccount(String accountNo, int pin) {
        if (conn == null) {
            // fallback to in-memory map
            BankAccount a = accounts.get(accountNo);
            if (a != null && a.validatePIN(pin)) return a;
            return null;
        }

        String sql = "SELECT account_no, pin, balance FROM users WHERE account_no = ? AND pin = ?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, accountNo);
            pst.setInt(2, pin);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return new BankAccount(rs.getString("account_no"), rs.getInt("pin"), rs.getDouble("balance"));
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(frame, "Error verifying user: " + ex.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }

    // ---------- LOGIN PANEL ----------
    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(245, 247, 250));
        panel.setBorder(new EmptyBorder(25, 25, 25, 25));

        JLabel title = new JLabel("<html><center><h1 style='color:#2C3E50;'>🏦 ATM SYSTEM</h1><p>Developed by Mir Ahmad Shah & Wajahat Ali Khan</p></center></html>");
        title.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(title, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(3, 1, 15, 15));
        form.setBackground(new Color(245, 247, 250));

        accountField = new JTextField();
        styleField(accountField);

        pinField = new JPasswordField();
        styleField(pinField);

        form.add(labeledField("Account / Username:", accountField));
        form.add(labeledField("PIN / Password:", pinField));

        JButton loginBtn = new JButton("Login");
        JButton exitBtn = new JButton("Exit");
        styleButton(loginBtn, new Color(52, 152, 219));
        styleButton(exitBtn, new Color(231, 76, 60));

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        btnPanel.setBackground(new Color(245, 247, 250));
        btnPanel.add(loginBtn);
        btnPanel.add(exitBtn);
        form.add(btnPanel);
        panel.add(form, BorderLayout.CENTER);

        // Event handlers
        loginBtn.addActionListener(new LoginActionHandler()); // inner-class listener
        exitBtn.addActionListener(e -> {
            // close DB connection and exit
            closeQuietly(conn);
            frame.dispose();
        });
        pinField.addActionListener(e -> attemptLogin());

        return panel;
    }

    // Inner class listener for login
    private class LoginActionHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            attemptLogin();
        }
    }

    private JPanel labeledField(String label, JComponent field) {
        JPanel p = new JPanel(new BorderLayout(6, 6));
        p.setBackground(new Color(245, 247, 250));
        JLabel l = new JLabel(label);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        p.add(l, BorderLayout.WEST);
        p.add(field, BorderLayout.CENTER);
        return p;
    }

    private void styleField(JTextField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)
        ));
        field.setBackground(Color.WHITE);
    }

    private void styleButton(JButton button, Color bgColor) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
    }

    // Event Handler logic for login (uses DB)
    private void attemptLogin() {
        String acc = accountField.getText().trim();
        String pinTxt = new String(pinField.getPassword());

        if (acc.isEmpty() || pinTxt.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Please enter both fields.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Try admin login first
        if (verifyAdmin(acc, pinTxt)) {
            refreshAdminTable();
            cardLayout.show(mainPanel, "ADMIN_DASH");
            accountField.setText("");
            pinField.setText("");
            return;
        }

        // Else treat as user login
        try {
            int pin = Integer.parseInt(pinTxt);
            BankAccount a = verifyUserAndFetchAccount(acc, pin);
            if (a != null) {
                currentAccount = a;
                updateBalanceLabelFromDB();
                cardLayout.show(mainPanel, "USER_DASH");
                accountField.setText("");
                pinField.setText("");
            } else {
                JOptionPane.showMessageDialog(frame, "Invalid credentials.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(frame, "PIN must be numeric.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ---------- USER DASHBOARD ----------
    private JPanel createUserDashboardPanel() {
        JPanel p = new JPanel(new BorderLayout(12, 12));
        p.setBorder(new EmptyBorder(12, 12, 12, 12));

        welcomeLabel = new JLabel("Welcome, User");
        welcomeLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        p.add(welcomeLabel, BorderLayout.NORTH);

        JPanel center = new JPanel(new GridLayout(2, 1, 8, 8));
        JPanel balPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        balPanel.add(new JLabel("Available Balance: "));
        balanceLabel = new JLabel("$0.00");
        balanceLabel.setFont(new Font("Monospaced", Font.BOLD, 18));
        balPanel.add(balanceLabel);
        center.add(balPanel);

        JPanel btns = new JPanel(new GridLayout(1, 3, 10, 0));
        JButton checkBtn = new JButton("Check Balance");
        JButton withdrawBtn = new JButton("Withdraw");
        JButton depositBtn = new JButton("Deposit");
        btns.add(checkBtn);
        btns.add(withdrawBtn);
        btns.add(depositBtn);
        center.add(btns);

        p.add(center, BorderLayout.CENTER);

        JButton logoutBtn = new JButton("Logout");
        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        south.add(logoutBtn);
        p.add(south, BorderLayout.SOUTH);

        // Event handlers (anonymous listeners)
        checkBtn.addActionListener(e -> updateBalanceLabelFromDBAndShow());
        withdrawBtn.addActionListener(e -> withdrawMoney());
        depositBtn.addActionListener(e -> depositMoney());
        logoutBtn.addActionListener(e -> {
            currentAccount = null;
            cardLayout.show(mainPanel, "LOGIN");
        });

        return p;
    }

    // Refresh currentAccount from DB and update label
    private void updateBalanceLabelFromDB() {
        if (currentAccount == null) return;
        BankAccount refreshed = fetchAccountFromDB(currentAccount.getAccountNumber());
        if (refreshed != null) {
            currentAccount.setBalance(refreshed.getBalance());
        }
        welcomeLabel.setText("Welcome Back, Account: " + currentAccount.getAccountNumber());
        balanceLabel.setText("$" + df.format(currentAccount.getBalance()));
    }

    // Show balance dialog after fetching fresh balance
    private void updateBalanceLabelFromDBAndShow() {
        updateBalanceLabelFromDB();
        if (currentAccount != null) {
            JOptionPane.showMessageDialog(frame, "Your balance: $" + df.format(currentAccount.getBalance()), "Balance", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void withdrawMoney() {
        if (currentAccount == null) return;
        String amt = JOptionPane.showInputDialog(frame, "Enter amount to withdraw:");
        if (amt == null) return;
        try {
            double a = Double.parseDouble(amt);
            // fetch latest balance
            BankAccount fresh = fetchAccountFromDB(currentAccount.getAccountNumber());
            if (fresh == null) throw new IllegalStateException("Account not found in DB.");
            if (a <= 0) throw new IllegalArgumentException("Amount must be positive.");
            if (a > fresh.getBalance()) throw new IllegalArgumentException("Insufficient balance!");
            double newBalance = fresh.getBalance() - a;
            if (updateBalanceInDB(fresh.getAccountNumber(), newBalance)) {
                currentAccount.setBalance(newBalance);
                updateBalanceLabelFromDB();
                JOptionPane.showMessageDialog(frame, "Withdrawal successful.");
            } else {
                JOptionPane.showMessageDialog(frame, "Withdrawal failed (DB error).", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(frame, "Invalid amount.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException | IllegalStateException ex) {
            JOptionPane.showMessageDialog(frame, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void depositMoney() {
        if (currentAccount == null) return;
        String amt = JOptionPane.showInputDialog(frame, "Enter amount to deposit:");
        if (amt == null) return;
        try {
            double a = Double.parseDouble(amt);
            if (a <= 0) throw new IllegalArgumentException("Amount must be positive.");
            BankAccount fresh = fetchAccountFromDB(currentAccount.getAccountNumber());
            if (fresh == null) throw new IllegalStateException("Account not found in DB.");
            double newBalance = fresh.getBalance() + a;
            if (updateBalanceInDB(fresh.getAccountNumber(), newBalance)) {
                currentAccount.setBalance(newBalance);
                updateBalanceLabelFromDB();
                JOptionPane.showMessageDialog(frame, "Deposit successful.");
            } else {
                JOptionPane.showMessageDialog(frame, "Deposit failed (DB error).", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(frame, "Invalid amount.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException | IllegalStateException ex) {
            JOptionPane.showMessageDialog(frame, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ---------- ADMIN DASHBOARD ----------
    private JPanel createAdminDashboardPanel() {
        JPanel p = new JPanel(new BorderLayout(12, 12));
        p.setBorder(new EmptyBorder(12, 12, 12, 12));

        JLabel title = new JLabel("Admin Dashboard");
        title.setFont(new Font("SansSerif", Font.BOLD, 16));
        p.add(title, BorderLayout.NORTH);

        adminTableModel = new DefaultTableModel(new Object[]{"Account", "PIN", "Balance"}, 0);
        JTable table = new JTable(adminTableModel);
        p.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        JButton addBtn = new JButton("Add User");
        JButton refreshBtn = new JButton("Refresh");
        JButton logoutBtn = new JButton("Logout");
        bottom.add(addBtn);
        bottom.add(refreshBtn);
        bottom.add(logoutBtn);
        p.add(bottom, BorderLayout.SOUTH);

        addBtn.addActionListener(e -> openAddUserDialog());
        refreshBtn.addActionListener(e -> refreshAdminTable());
        logoutBtn.addActionListener(e -> cardLayout.show(mainPanel, "LOGIN"));

        return p;
    }

    private void refreshAdminTable() {
        // reload from database
        loadAccountsFromDatabase();
        adminTableModel.setRowCount(0);
        for (BankAccount a : accounts.values()) {
            adminTableModel.addRow(new Object[]{a.getAccountNumber(), a.getPin(), "$" + df.format(a.getBalance())});
        }
    }

    private void openAddUserDialog() {
        JTextField accField = new JTextField();
        JTextField pinField = new JTextField();
        JTextField balField = new JTextField();

        JPanel form = new JPanel(new GridLayout(3, 2, 8, 8));
        form.add(new JLabel("Account Number:"));
        form.add(accField);
        form.add(new JLabel("PIN:"));
        form.add(pinField);
        form.add(new JLabel("Initial Balance:"));
        form.add(balField);

        int ok = JOptionPane.showConfirmDialog(frame, form, "Add New Account", JOptionPane.OK_CANCEL_OPTION);
        if (ok == JOptionPane.OK_OPTION) {
            try {
                String acc = accField.getText().trim();
                if (acc.isEmpty()) throw new IllegalArgumentException("Account number cannot be empty.");
                int pin = Integer.parseInt(pinField.getText().trim());
                double bal = Double.parseDouble(balField.getText().trim());

                // Check existence
                if (fetchAccountFromDB(acc) != null) {
                    JOptionPane.showMessageDialog(frame, "Account already exists!");
                    return;
                }

                boolean added = insertUserToDB(acc, pin, bal);
                if (added) {
                    refreshAdminTable();
                    JOptionPane.showMessageDialog(frame, "New account added successfully!");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "PIN and Balance must be numeric.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(frame, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}





