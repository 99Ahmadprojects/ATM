package atm;

public class BankAccount {
    private String accountNumber;
    private int pin;
    private double balance;

    public BankAccount(String accountNumber, int pin, double balance) {
        this.accountNumber = accountNumber;
        this.pin = pin;
        this.balance = balance;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public int getPin() {
        return pin;
    }

    public boolean validatePIN(int inputPin) {
        return this.pin == inputPin;
    }

    public double getBalance() {
        return balance;
    }

    public void deposit(double amount) throws IllegalArgumentException {
        if (amount <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive!");
        }
        balance += amount;
    }

    public void withdraw(double amount) throws IllegalArgumentException {
        if (amount <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive!");
        }
        if (amount > balance) {
            throw new IllegalArgumentException("Insufficient balance!");
        }
        balance -= amount;
    }
}