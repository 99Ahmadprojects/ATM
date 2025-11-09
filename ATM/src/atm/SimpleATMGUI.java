/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package atm;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 *
 * @author hh
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

    private final Map<String, BankAccount> accounts = new HashMap<>();
    private static final String FILE_PATH = "accounts.txt";
    private final DecimalFormat df = new DecimalFormat("#,##0.00");

    private final String ADMIN_USER = "admin";
    private final String ADMIN_PASS = "admin123";

    private DefaultTableModel adminTableModel;

    public SimpleATMGUI() {
        loadAccountsFromFile();

        frame = new JFrame("ATM System (GUI)");
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

    // ---------- File Handling ----------
    private void loadAccountsFromFile() {
        accounts.clear();
        File f = new File(FILE_PATH);
        if (!f.exists()) {
            try (PrintWriter pw = new PrintWriter(f)) {
                pw.println("10001,1234,1000.00");
                pw.println("20002,4321,250.00");
            } catch (FileNotFoundException ignored) {}
        }

        try (Scanner sc = new Scanner(f)) {
            while (sc.hasNextLine()) {
                String line = sc.nextLine().trim();
                if (line.isEmpty()) continue;
                String[] p = line.split(",");
                if (p.length == 3) {
                    accounts.put(p[0], new BankAccount(p[0], Integer.parseInt(p[1]), Double.parseDouble(p[2])));
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error reading accounts.txt", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveAccountsToFile() {
        try (PrintWriter pw = new PrintWriter(FILE_PATH)) {
            for (BankAccount a : accounts.values()) {
                pw.println(a.getAccountNumber() + "," + a.getPin() + "," + a.getBalance());
            }
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(frame, "Unable to save file.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ---------- LOGIN PANEL ----------
    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(245, 247, 250));
        panel.setBorder(new EmptyBorder(25, 25, 25, 25));

        JLabel title = new JLabel("<html><center><h1 style='color:#2C3E50;'>🏦 ATM SYSTEM</h1><p>Developed by Mir Ahmad Shah & Wajahat Ali Khan</p></center></html>");
        title.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(title, BorderLayout.NORTH);

        // Center panel
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

        loginBtn.addActionListener(e -> attemptLogin());
        exitBtn.addActionListener(e -> frame.dispose());
        pinField.addActionListener(e -> attemptLogin());

        return panel;
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

    private void attemptLogin() {
        String acc = accountField.getText().trim();
        String pinTxt = new String(pinField.getPassword());

        if (acc.isEmpty() || pinTxt.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Please enter both fields.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (acc.equals(ADMIN_USER) && pinTxt.equals(ADMIN_PASS)) {
            refreshAdminTable();
            cardLayout.show(mainPanel, "ADMIN_DASH");
            accountField.setText("");
            pinField.setText("");
            return;
        }

        BankAccount a = accounts.get(acc);
        try {
            int pin = Integer.parseInt(pinTxt);
            if (a != null && a.validatePIN(pin)) {
                currentAccount = a;
                updateBalanceLabel();
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

        checkBtn.addActionListener(e -> showBalance());
        withdrawBtn.addActionListener(e -> withdrawMoney());
        depositBtn.addActionListener(e -> depositMoney());
        logoutBtn.addActionListener(e -> {
            currentAccount = null;
            cardLayout.show(mainPanel, "LOGIN");
        });

        return p;
    }

    private void updateBalanceLabel() {
        if (currentAccount != null) {
            welcomeLabel.setText("Welcome Back, Account: " + currentAccount.getAccountNumber());
            balanceLabel.setText("$" + df.format(currentAccount.getBalance()));
        }
    }

    private void showBalance() {
        JOptionPane.showMessageDialog(frame, "Your balance: $" + df.format(currentAccount.getBalance()), "Balance", JOptionPane.INFORMATION_MESSAGE);
    }

    private void withdrawMoney() {
        String amt = JOptionPane.showInputDialog(frame, "Enter amount to withdraw:");
        if (amt == null) return;
        try {
            double a = Double.parseDouble(amt);
            currentAccount.withdraw(a);
            saveAccountsToFile();
            updateBalanceLabel();
            JOptionPane.showMessageDialog(frame, "Withdrawal successful.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void depositMoney() {
        String amt = JOptionPane.showInputDialog(frame, "Enter amount to deposit:");
        if (amt == null) return;
        try {
            double a = Double.parseDouble(amt);
            currentAccount.deposit(a);
            saveAccountsToFile();
            updateBalanceLabel();
            JOptionPane.showMessageDialog(frame, "Deposit successful.");
        } catch (Exception ex) {
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
        loadAccountsFromFile();
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
                int pin = Integer.parseInt(pinField.getText().trim());
                double bal = Double.parseDouble(balField.getText().trim());

                if (accounts.containsKey(acc)) {
                    JOptionPane.showMessageDialog(frame, "Account already exists!");
                    return;
                }

                BankAccount newAcc = new BankAccount(acc, pin, bal);
                accounts.put(acc, newAcc);
                saveAccountsToFile();
                refreshAdminTable();
                JOptionPane.showMessageDialog(frame, "New account added successfully!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Invalid input: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}



