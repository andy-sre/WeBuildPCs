package Main.app.employeearea.dashboard.stock;

import Main.app.App;
import Main.app.employeearea.dashboard.EmployeeDash;

import javax.swing.*;
import java.awt.*;

public class StockDash extends JFrame {
    private JPanel panel;
    private JButton logoutButton;
    private JButton CPUButton;
    private JButton GPUButton;
    private JButton RAMButton;
    private JButton MOBOButton;
    private JButton PSUButton;
    private JButton storageButton;
    private JButton caseButton;
    private JButton returnButton;
    private JLabel stockLabel;
    private JButton serverButton;

    public StockDash(int employeeID, String fname) {
        stockLabel.setText("Welcome, " + fname + " to your stock dashboard");
        Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
        this.setTitle("Computer Shop - Welcome");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setBounds(0, 0, size.width, size.height);
        this.setVisible(true);
        this.add(panel);
        CPUButton.addActionListener(e -> {
            new StockController(employeeID, fname, "cpu");
            dispose();
        });
        GPUButton.addActionListener(e -> {
            new StockController(employeeID, fname, "gpu");
            dispose();
        });
        RAMButton.addActionListener(e -> {
            new StockController(employeeID, fname, "ram");
            dispose();
        });
        MOBOButton.addActionListener(e -> {
            new StockController(employeeID, fname, "mobo");
            dispose();
        });
        PSUButton.addActionListener(e -> {
            new StockController(employeeID, fname, "psu");
            dispose();
        });
        storageButton.addActionListener(e -> {
            new StockController(employeeID, fname, "storage");
            dispose();
        });
        caseButton.addActionListener(e -> {
            new StockController(employeeID, fname, "pcCase");
            dispose();
        });
        serverButton.addActionListener(e -> {
            new StockController(employeeID, fname, "server");
            dispose();
        });
        returnButton.addActionListener(e -> {
            new EmployeeDash(employeeID, fname);
            dispose();
        });
        logoutButton.addActionListener(e -> {
            new App();
            dispose();
        });

    }
}
