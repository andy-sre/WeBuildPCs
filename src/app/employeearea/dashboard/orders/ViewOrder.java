package app.employeearea.dashboard.orders;

import app.App;

import javax.swing.*;
import java.awt.*;

public class ViewOrder extends JFrame{
    private JPanel panel;
    private JButton logoutButton;

    public ViewOrder(int employeeID, int orderID, String fname) {
        Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
        this.setTitle("Computer Shop - Welcome");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setBounds(0,0,size.width, size.height);
        this.setVisible(true);
        this.add(panel);
        logoutButton.addActionListener(e -> {
            new App();
            dispose();
        });
    }
}
