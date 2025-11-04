package atm;

// ========================
// Main Class
// ========================
public class Atm {
    public static void main(String[] args) {
        // You can choose between console or GUI version
        // SimpleATMSystem atm = new SimpleATMSystem();
        // atm.start();
        
        // Launch GUI version
        SimpleATMGUI atmGUI = new SimpleATMGUI();
        atmGUI.setVisible(true);
    }
}