package atm;

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