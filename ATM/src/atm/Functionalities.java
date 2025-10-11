/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package atm;

/**
 *
 * @author hh
 */

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

// Abstract class for ATM behavior
abstract class Functionalities {
    protected Map<String, BankAccount> accounts = new HashMap<>();
    protected BankAccount currentAccount;

    public Functionalities() {
        accounts.put("12345", new BankAccount("12345", 1111, 800.00));
        accounts.put("67890", new BankAccount("67890", 2222, 500.00));
    }

    public abstract void start();
    protected abstract void login(Scanner scanner);
    protected abstract void showMenu(Scanner scanner);
}

// Concrete class implementing ATM functionality
class SimpleATMSystem extends Functionalities {

    @Override
    public void start() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("            === WELCOME TO ATM ===");
        System.out.println(" ===Develped by Mir Ahmad Shah & Wajahat Ali khan===\n");
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
                        System.out.println("Your amount Successfully Withdrawn!");
                        break;
                    case 3:
                        System.out.print("Enter amount to deposit: ");
                        double depositAmount = scanner.nextDouble();
                        scanner.nextLine();
                        currentAccount.deposit(depositAmount);
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

