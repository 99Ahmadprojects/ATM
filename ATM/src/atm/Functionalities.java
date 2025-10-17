/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package atm;

/**
 *
 * @author hh
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

// Abstract class for ATM behavior
abstract class Functionalities {
    protected Map<String, BankAccount> accounts = new HashMap<>();
    protected BankAccount currentAccount;

    public abstract void start();
    protected abstract void login(Scanner scanner);
    protected abstract void showMenu(Scanner scanner);
}

// Concrete class implementing ATM functionality
class SimpleATMSystem extends Functionalities {

    // Path to the data file
    private static final String FILE_PATH = "accounts.txt";

    // Constructor loads accounts from the file
    public SimpleATMSystem() {
        loadAccountsFromFile();
    }

    // Method load account data from text file
    private void loadAccountsFromFile() {
        try (Scanner fileScanner = new Scanner(new File(FILE_PATH))) {
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    String accNum = parts[0];
                    int pin = Integer.parseInt(parts[1]);
                    double balance = Double.parseDouble(parts[2]);
                    accounts.put(accNum, new BankAccount(accNum, pin, balance));
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("Error: Account data file not found! Please create 'accounts.txt'.");
        } catch (NumberFormatException e) {
            System.err.println("Error: Invalid number format in account data file.");
        }
    }

    // Method write the updated account data back to the file
    private void updateAccountsFile() {
        try (PrintWriter writer = new PrintWriter(new File(FILE_PATH))) {
            for (BankAccount account : accounts.values()) {
                writer.println(account.getAccountNumber() + "," + account.getPin() + "," + account.getBalance());
            }
        } catch (FileNotFoundException e) {
            System.err.println("Error: Could not save account data.");
        }
    }

    @Override
    public void start() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("            === WELCOME TO ATM ===");
        System.out.println(" === Develped by Mir Ahmad Shah & Wajahat Ali khan ===\n");
        login(scanner);
    }

    @Override
    protected void login(Scanner scanner) {
        System.out.print("Enter Account Number: ");
        String accNumber = scanner.nextLine();

        System.out.print("Enter PIN: ");
        int pin = scanner.nextInt();
        scanner.nextLine(); // consume newline

        BankAccount account = accounts.get(accNumber);

        if (account != null && account.validatePIN(pin)) {
            currentAccount = account;
            System.out.println("Login Successful!");
            showMenu(scanner);
        } else {
            System.out.println("Invalid account number or PIN! please try again");
        }
    }

    @Override
    protected void showMenu(Scanner scanner) {
        int choice;
        do {
            System.out.println("\n1. Check Balance");
            System.out.println("2. Withdraw Money");
            System.out.println("3. Deposit Money");
            System.out.println("4. Exit");
            System.out.print("Choose: ");

            choice = scanner.nextInt();
            scanner.nextLine(); // consume newline

            try {
                switch (choice) {
                    case 1:
                        System.out.printf("Your balance: $%.2f%n", currentAccount.getBalance());
                        break;
                    case 2:
                        System.out.print("Enter amount to withdraw: ");
                        double withdrawAmount = scanner.nextDouble();
                        scanner.nextLine();
                        currentAccount.withdraw(withdrawAmount);
                        updateAccountsFile(); // Save changes to file
                        System.out.println("Your amount Successfully Withdrawn!");
                        break;
                    case 3:
                        System.out.print("Enter amount to deposit: ");
                        double depositAmount = scanner.nextDouble();
                        scanner.nextLine();
                        currentAccount.deposit(depositAmount);
                        updateAccountsFile(); // Save changes to file
                        System.out.println("Your amount Successfully Deposited!");
                        break;
                    case 4:
                        System.out.println("Thank you for using ATM. Goodbye!");
                        break;
                    default:
                        System.out.println("Invalid option. Please try again.");
                }
            } catch (IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage());
            }
        } while (choice != 4);
    }
}

