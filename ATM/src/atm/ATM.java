/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
/*
 * Main class demonstrating Event-Driven Programming
 */
package atm;

import javax.swing.SwingUtilities;

/**
 *
 * @author hh
 */

/**
 * ATM.java
 * Entry point demonstrating Event-Driven Programming in Java.
 *
 * → Procedural vs Event-Driven:
 * In procedural programming, the flow is sequential.
 * In event-driven programming, the flow depends on user actions (events).
 */
public class ATM {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(SimpleATMGUI::new);
    }
}
