package atm;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.HashMap;
import java.util.Map;

public class SimpleATMGUI extends JFrame {
    private Map<String, BankAccount> accounts = new HashMap<>();
    private BankAccount currentAccount;
    
    // GUI Components
    private JPanel mainPanel;
    private JTextField accountField;
    private JPasswordField pinField;
    private JButton loginButton;
    
    public SimpleATMGUI() {
        initializeAccounts();
        setupGUI();
        setApplicationIcon();
    }
    
    private void initializeAccounts() {
        accounts.put("12345", new BankAccount("12345", 1111, 800.00));
        accounts.put("67890", new BankAccount("67890", 2222, 500.00));
    }
    
    private void setupGUI() {
        setTitle("ATM System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null); // Center the window
        setResizable(false);
        
        createLoginPanel();
        
        setVisible(true);
    }
    
    private void setApplicationIcon() {
        try {
            ImageIcon icon = new ImageIcon("C:\\Users\\RiphahIUI\\OneDrive\\Desktop\\atm.png");
            setIconImage(icon.getImage());
        } catch (Exception e) {
            // Use default icon if custom icon fails
            System.out.println("Icon not found, using default.");
        }
    }
    
    // Custom JTextField with rounded corners
    class RoundedTextField extends JTextField {
        public RoundedTextField(int columns) {
            super(columns);
            setOpaque(false);
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Fill rounded rectangle
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
            
            super.paintComponent(g2);
            g2.dispose();
        }
        
        @Override
        protected void paintBorder(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.BLACK);
            g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 15, 15);
            g2.dispose();
        }
    }
    
    // Custom JPasswordField with rounded corners
    class RoundedPasswordField extends JPasswordField {
        public RoundedPasswordField(int columns) {
            super(columns);
            setOpaque(false);
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Fill rounded rectangle
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
            
            super.paintComponent(g2);
            g2.dispose();
        }
        
        @Override
        protected void paintBorder(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.BLACK);
            g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 15, 15);
            g2.dispose();
        }
    }
    
    // Custom JButton with rounded corners
    class RoundedButton extends JButton {
        public RoundedButton(String text) {
            super(text);
            setContentAreaFilled(false);
            setFocusPainted(false);
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            if (getModel().isPressed()) {
                g2.setColor(getBackground().darker());
            } else if (getModel().isRollover()) {
                g2.setColor(getBackground().brighter());
            } else {
                g2.setColor(getBackground());
            }
            
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
            super.paintComponent(g2);
            g2.dispose();
        }
        
        @Override
        protected void paintBorder(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.BLACK);
            g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 15, 15);
            g2.dispose();
        }
    }
    
    private void createLoginPanel() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        
        // Create form panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridLayout(3, 1, 15, 15));
        formPanel.setBackground(Color.WHITE);
        
        // Account Number Field with placeholder
        accountField = new RoundedTextField(20);
        accountField.setText("Enter Account Number");
        accountField.setForeground(Color.BLACK);
        accountField.setFont(new Font("Arial", Font.PLAIN, 14));
        accountField.setPreferredSize(new Dimension(250, 40));
        accountField.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        
        // Add focus listener for placeholder behavior
        accountField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (accountField.getText().equals("Enter Account Number")) {
                    accountField.setText("");
                    accountField.setForeground(Color.BLACK);
                }
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                if (accountField.getText().isEmpty()) {
                    accountField.setForeground(Color.BLACK);
                    accountField.setText("Enter Account Number");
                }
            }
        });
        
        formPanel.add(accountField);
        
        // PIN Field with placeholder
        pinField = new RoundedPasswordField(20);
        pinField.setEchoChar((char) 0); // Show text initially
        pinField.setText("Enter PIN");
        pinField.setForeground(Color.BLACK);
        pinField.setFont(new Font("Arial", Font.PLAIN, 14));
        pinField.setPreferredSize(new Dimension(250, 40));
        pinField.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        
        // Add focus listener for placeholder behavior
        pinField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (String.valueOf(pinField.getPassword()).equals("Enter PIN")) {
                    pinField.setText("");
                    pinField.setForeground(Color.BLACK);
                    pinField.setEchoChar('•'); // Show dots when typing
                }
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                if (pinField.getPassword().length == 0) {
                    pinField.setEchoChar((char) 0); // Show text
                    pinField.setForeground(Color.BLACK);
                    pinField.setText("Enter PIN");
                }
            }
        });
        
        formPanel.add(pinField);
        
        // LOGIN Button with rounded corners
        loginButton = new RoundedButton("LOGIN");
        loginButton.setBackground(new Color(0, 50,200)); // Light blue color
        loginButton.setForeground(Color.WHITE);
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));
        loginButton.setPreferredSize(new Dimension(250, 40));
        
        // Add hover effect
        loginButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                loginButton.setBackground(new Color(50, 50, 50)); // Dark gray on hover
                loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
            
            public void mouseExited(java.awt.event.MouseEvent evt) {
                loginButton.setBackground(Color.blue);
                loginButton.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });
        
        loginButton.addActionListener(new LoginButtonListener());
        formPanel.add(loginButton);
        
        mainPanel.add(formPanel, BorderLayout.CENTER);
        
        add(mainPanel);
    }
    
    private void createMainMenuPanel() {
        mainPanel.removeAll();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        
        // Welcome message
        JLabel welcomeLabel = new JLabel("Welcome! Account: " + currentAccount.getAccountNumber());
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        welcomeLabel.setForeground(Color.BLACK);
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(welcomeLabel, BorderLayout.NORTH);
        
        // Buttons panel
        JPanel buttonPanel = new JPanel(new GridLayout(4, 1, 10, 10));
        buttonPanel.setBackground(Color.WHITE);
        
        JButton balanceButton = createMenuButton("Check Balance");
        balanceButton.addActionListener(new BalanceButtonListener());
        buttonPanel.add(balanceButton);
        
        JButton withdrawButton = createMenuButton("Withdraw Money");
        withdrawButton.addActionListener(new WithdrawButtonListener());
        buttonPanel.add(withdrawButton);
        
        JButton depositButton = createMenuButton("Deposit Money");
        depositButton.addActionListener(new DepositButtonListener());
        buttonPanel.add(depositButton);
        
        JButton logoutButton = createMenuButton("Logout");
        logoutButton.addActionListener(new LogoutButtonListener());
        buttonPanel.add(logoutButton);
        
        mainPanel.add(buttonPanel, BorderLayout.CENTER);
        
        mainPanel.revalidate();
        mainPanel.repaint();
    }
    
    private JButton createMenuButton(String text) {
        RoundedButton button = new RoundedButton(text);
        button.setBackground(Color.orange);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setPreferredSize(new Dimension(250, 35));
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(50, 50, 50));
                button.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
            
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(Color.orange);
                button.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });
        
        return button;
    }
    
    private void switchToLoginView() {
        mainPanel.removeAll();
        createLoginPanel();
        mainPanel.revalidate();
        mainPanel.repaint();
    }
    
    private void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Message", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    // Action Listeners
    private class LoginButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String accountNumber = accountField.getText().trim();
            String pinText = String.valueOf(pinField.getPassword());
            
            // Check if placeholder text is still there
            if (accountNumber.equals("Enter Account Number") || accountNumber.isEmpty()) {
                showError("Please enter account number!");
                return;
            }
            
            if (pinText.equals("Enter PIN") || pinText.isEmpty()) {
                showError("Please enter PIN!");
                return;
            }
            
            try {
                int pin = Integer.parseInt(pinText);
                BankAccount account = accounts.get(accountNumber);
                
                if (account != null && account.validatePIN(pin)) {
                    currentAccount = account;
                    createMainMenuPanel();
                } else {
                    showError("Invalid account number or PIN!");
                }
            } catch (NumberFormatException ex) {
                showError("PIN must be a number!");
            }
        }
    }
    
    private class BalanceButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            double balance = currentAccount.getBalance();
            showMessage(String.format("Your current balance: $%.2f", balance));
        }
    }
    
    private class WithdrawButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String input = JOptionPane.showInputDialog(SimpleATMGUI.this, 
                "Enter amount to withdraw:", "Withdraw", JOptionPane.QUESTION_MESSAGE);
            
            if (input != null && !input.trim().isEmpty()) {
                try {
                    double amount = Double.parseDouble(input.trim());
                    currentAccount.withdraw(amount);
                    showMessage(String.format("Successfully withdrew $%.2f\nNew balance: $%.2f", 
                        amount, currentAccount.getBalance()));
                } catch (NumberFormatException ex) {
                    showError("Please enter a valid number!");
                } catch (IllegalArgumentException ex) {
                    showError("Error: " + ex.getMessage());
                }
            }
        }
    }
    
    private class DepositButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String input = JOptionPane.showInputDialog(SimpleATMGUI.this, 
                "Enter amount to deposit:", "Deposit", JOptionPane.QUESTION_MESSAGE);
            
            if (input != null && !input.trim().isEmpty()) {
                try {
                    double amount = Double.parseDouble(input.trim());
                    currentAccount.deposit(amount);
                    showMessage(String.format("Successfully deposited $%.2f\nNew balance: $%.2f", 
                        amount, currentAccount.getBalance()));
                } catch (NumberFormatException ex) {
                    showError("Please enter a valid number!");
                } catch (IllegalArgumentException ex) {
                    showError("Error: " + ex.getMessage());
                }
            }
        }
    }
    
    private class LogoutButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            currentAccount = null;
            showMessage("Thank you for using our ATM system!");
            switchToLoginView();
        }
    }
}