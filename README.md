# 💳 ATM (Automated Teller Machine)

A simple **ATM** simulation program written in **Java**, designed to demonstrate key programming concepts such as **OOP (Object-Oriented Programming)**, **encapsulation**, **Programming Fundamentals**, and **user interaction** through a **console-based interface**.

---

## Output Screenshots

<p align="center">
  <img src="screenshot.png" alt="ATM Screenshot" width="400">
  <img src="screenshot2.png" alt="ATM Screenshot" width="400">
</p>

---

## 🧠 Project Overview

The **ATM** project allows users to perform essential banking operations, including:

- 🔑 User authentication using account numbers and PINs  
- 💰 Balance inquiry  
- 💵 Cash withdrawal  
- 💳 Cash deposit  
- 🔁 Account management through a secure and interactive console menu  

The system uses **Java Collections (HashMap & Map)** to store and manage multiple user accounts efficiently.

---

## 🧩 Key Concepts Demonstrated

| Concept | Description |
|----------|-------------|
| **Classes & Objects** | Encapsulate account data and ATM logic into structured classes. |
| **Encapsulation** | Sensitive account data (PINs, balances) is private and accessed via methods. |
| **HashMap & Map Interface** | Store and retrieve bank accounts efficiently using account numbers as keys. |
| **Control Flow** | Manage user input and ATM menu operations with conditionals and loops. |
| **Exception Handling** | Handle invalid inputs gracefully using try-catch blocks. |

---

## 🗂️ Project Structure

```

ATM/
│
├── src/
│ ├── BankAccount.java # Represents individual bank accounts
│ ├── ATM.java # Entry point to start the program
│ └── Functionalities.java # Main class handling user interface and operations
│
└── README.md # Project documentation (this file)

```

---

## ⚙️ How It Works

1. The program starts and displays a login prompt.
2. The user enters their **account number** and **PIN**.
3. After successful login, a menu appears with available operations:
   - Check Balance
   - Deposit Money
   - Withdraw Money
   - Exit
4. The program validates transactions (e.g., sufficient funds for withdrawals).
5. All changes are updated in real time for that session.

---


## 🧱 Technologies Used

- **Java SE 8+**
- **HashMap & Map Collections Framework**
- **OOP Principles**
- **Scanner (for console input)**

---

