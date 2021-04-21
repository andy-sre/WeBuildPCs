package App.EmployeeArea.Dashboard;

import App.App;
import App.EmployeeArea.Dashboard.Orders.ViewOrders;
import App.EmployeeArea.Dashboard.ProfileDash.ProfileDash;
import App.EmployeeArea.Dashboard.Stock.StockDash;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class EmployeeDash extends JFrame{
    private JPanel panel;
    private JButton logoutButton;
    private JButton stockButton;
    private JButton viewOrdersButton;
    private JButton editDetailsButton;
    private JLabel welcomeLabel;

    public EmployeeDash(int employeeID, String fname) {
        welcomeLabel.setText("Welcome, " + fname + " to your dashboard");
        Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
        this.setTitle("Computer Shop - Welcome");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setBounds(0,0,size.width, size.height);
        this.setVisible(true);
        this.add(panel);
        stockButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new StockDash(employeeID, fname);
                dispose();
            }
        });
        editDetailsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new ProfileDash(employeeID, fname);
                dispose();
            }
        });
        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new App();
                dispose();
            }
        });
        viewOrdersButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new ViewOrders(employeeID, fname);
                dispose();
            }
        });
    }
}
