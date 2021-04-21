package app.userarea.dashboard;

import app.App;
import app.userarea.dashboard.orderarea.OrderCreate;
import app.userarea.dashboard.orderarea.OrderViewDash;
import app.userarea.dashboard.usercontrol.UserControlDash;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class UserDash extends JFrame{
    private JPanel panel;
    private JButton logoutButton;
    private JButton newOrderButton;
    private JButton viewOrdersButton;
    private JButton editDetailsButton;
    private JLabel welcomeLabel;
    private JLabel paymentAlertMsg;
    private JPanel paymentAlertPanel;
    private Connection connection;

    public UserDash(int userID, String fname) {
        Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
        this.setTitle("Computer Shop - Welcome");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setBounds(0,0,size.width, size.height);
        this.setVisible(true);
        this.add(panel);
        paymentAlertPanel.setVisible(false);
        paymentAlertMsg.setVisible(false);
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:identifier.sqlite");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        try {
            PreparedStatement checkPayment = connection.prepareStatement("SELECT * FROM Payments WHERE userID = ?");
            checkPayment.setInt(1, userID);
            ResultSet rs = checkPayment.executeQuery();
            while (rs.next()) {
                if (rs.getDouble("remainingBal") > 0) {
                    paymentAlertMsg.setText("You have pending payments due on orders.  Please view your orders area!");
                    paymentAlertPanel.setVisible(true);
                    paymentAlertMsg.setVisible(true);
                } else {
                    paymentAlertPanel.setVisible(false);
                    paymentAlertMsg.setVisible(false);
                }
            }
            rs.close();
            checkPayment.close();
            connection.close();
        } catch (SQLException checkOrders) {
            checkOrders.printStackTrace();
        }
        welcomeLabel.setText("Welcome, " + fname + " to your dashboard");
        newOrderButton.addActionListener(e -> {
            new OrderCreate(userID, fname);
            dispose();
        });
        viewOrdersButton.addActionListener(e -> {
            new OrderViewDash(userID, fname);
            dispose();
        });
        editDetailsButton.addActionListener(e -> {
            new UserControlDash(userID, fname);
            dispose();
        });
        logoutButton.addActionListener(e -> {
            new App();
            dispose();
        });
    }
}