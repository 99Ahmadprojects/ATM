package atm;

import java.util.Scanner;

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
        boolean isLoggedIn = false;

        do {
            System.out.print("Enter Account Number: ");
            String accNumber = scanner.nextLine();

            System.out.print("Enter PIN: ");
            int pin = scanner.nextInt();
            scanner.nextLine(); // consume newline

            BankAccount account = accounts.get(accNumber);

            if (account != null && account.validatePIN(pin)) {
                currentAccount = account;
                System.out.println("Login Successful!");
                isLoggedIn = true;
                showMenu(scanner);
            } else {
                System.out.println("Invalid account number or PIN! Please try again.\n");
            }

        } while (!isLoggedIn);
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
                        System.out.printf("Amount successfully deposited $%.2f%n", depositAmount);
                        System.out.printf("Updated balance: $%.2f%n", currentAccount.getBalance());
                        System.out.println("=== Transaction Complete ===");
                        break;
                    case 4:
                        System.out.println("Thanks for using this ATM ");
                        break;
                    default:
                        System.out.println("Invalid option. Please try again.");
                }
            } catch (IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage());
            }
            finally{
                System.out.print("Ended");               
            }
        } while (choice != 4);
    }
}