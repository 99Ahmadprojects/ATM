# 🏧 ATM Management System (Java Swing + MySQL)

A fully functional **ATM Management System** built using **Java Swing**, **Event-Driven Programming**, and **MySQL Database Integration**.  
This project demonstrates clean UI design, structured OOP principles, and real-world ATM functionalities such as withdrawals, deposits, balance checking, and admin account management.

---

## Output Screenshots

<p align="center">
  <img src="1.png" alt="ATM Screenshot" width="400">
  <img src="2.png" alt="ATM Screenshot" width="400">
  <img src="3.png" alt="ATM Screenshot" width="400">
</p>

---

## 🚀 Features

### 👤 **User Features**
- Login using **Account Number + PIN**
- Check current balance
- Withdraw funds (with validation)
- Deposit funds
- Real-time balance updates

### 🔐 **Admin Features**
- Login using **Admin username + password**
- Add new user accounts
- View all users in a live table
- Refresh account data
- Secure logout

### 🧱 **Backend / System Features**
- Event-Driven GUI using Java Swing
- JDBC-based MySQL integration
- Two database tables: `users` and `admin`
- Secure PIN/password handling
- Modular and clean Java code structure
- Automatic exception handling & validation

---

## 🛠️ Technologies Used

| Layer | Technology |
|------|-------------|
| Programming Language | **Java (JDK 8+)** |
| GUI Framework | **Java Swing (Event-Driven)** |
| Database | **MySQL / MariaDB** |
| JDBC Driver | **MySQL Connector/J** |
| IDE (optional) | Apache NetBeans / IntelliJ / Eclipse |

---

## 📁 Project Structure

```

ATM-System/
│
├── src/
│ ├── atm/
│ │ ├── ATM.java # Main class / entry point
│ │ ├── BankAccount.java # Data model (OOP)
│ │ ├── SimpleATMGUI.java # Full GUI + Event handling
│
├── README.md
└── /lib
└── mysql-connector-j-8.x.x.jar
│
└── README.md # Project documentation

```

---

## 🔌 JDBC Installation (Required)

Download MySQL Connector/J:
 https://dev.mysql.com/downloads/connector/j/

Choose Platform Independent (or any OS – JAR works everywhere)

Download the ZIP Archive

Extract the ZIP

Add the file mysql-connector-j-8.x.x.jar to your project:

In NetBeans:

```
Right-click Project → Properties → Libraries → Add JAR/Folder
```

## ▶️ Running the Application

Import the project into NetBeans

Set up your MySQL database (run the SQL above)

Update the DB credentials in SimpleATMGUI.java:
```
String url = "jdbc:mysql://localhost:3306/atm_system";
String user = "root";
String pass = "";
```

Run the ATM.java file


## 📚 Concepts Demonstrated in This Project

This ATM system implements key lessons from:

### Java Database Programming

- JDBC connections

- Prepared statements

- ResultSets & exception handling

### Database Fundamentals

- Relational structure (tables, rows, keys)

- Domain & integrity constraints

- Keys and key constraints

- Separation of admin and user tables

- Data validation and constraints

### Event-Driven Programming

- Delegation event model

- ActionEvent listeners

- Inner classes & anonymous listeners

- Swing UI workflow

## 🧑‍💻 Developers

**Mir Ahmad Shah & Wajahat Ali Khan**
