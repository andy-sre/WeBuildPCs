package Main.app.userarea.dashboard;

import Main.app.App;
import Main.app.userarea.dashboard.orderarea.OrderCreate;
import Main.app.userarea.dashboard.orderarea.OrderViewDash;
import Main.app.userarea.dashboard.orderarea.RentServer;
import Main.app.userarea.dashboard.usercontrol.UserEditDash;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class UserDash extends JFrame {
    private JPanel panel;
    private JButton logoutButton;
    private JButton newOrderButton;
    private JButton viewOrdersButton;
    private JButton editDetailsButton;
    private JButton rentButton;
    private JLabel welcomeLabel;
    private JLabel paymentAlertMsg;
    private JPanel paymentAlertPanel;

    public UserDash(int userID, String fname) {
        Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
        this.setTitle("Computer Shop - Welcome");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setBounds(0, 0, size.width, size.height);
        this.setVisible(true);
        this.add(panel);
        Connection connection = null;
        paymentAlertPanel.setVisible(false);
        paymentAlertMsg.setVisible(false);
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:identifier.sqlite");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        try {
            assert connection != null;
            PreparedStatement checkPayment = connection.prepareStatement("SELECT * FROM Payments WHERE userID = ?");
            checkPayment.setInt(1, userID);
            ResultSet rs = checkPayment.executeQuery();
            while (rs.next()) {
                String date = rs.getString("dueDate");
                double balance = rs.getDouble("remainingBal");
                try {
                    SimpleDateFormat sFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                    Date dueDate = sFormat.parse(date);
                    if (balance > 0) {
                        Date current = new Date();
                        if (current.compareTo(dueDate) >= 0) {
                            paymentAlertMsg.setText("You are overdue on your invoices!");
                            paymentAlertPanel.setVisible(true);
                            paymentAlertMsg.setVisible(true);
                        } else {
                            paymentAlertPanel.setVisible(false);
                            paymentAlertMsg.setVisible(false);
                        }
                    }
                } catch (ParseException e) {
                    System.out.println(e.getMessage());
                }
            }
            rs.close();
            checkPayment.close();
            connection.close();
        } catch (SQLException checkOrders) {
            checkOrders.printStackTrace();
        }
        welcomeLabel.setText("Welcome, " + fname + " to your dashboard");
        rentButton.addActionListener(e -> {
            new RentServer(userID, fname);
            dispose();
        });
        newOrderButton.addActionListener(e -> {
            new OrderCreate(userID, fname);
            dispose();
        });
        viewOrdersButton.addActionListener(e -> {
            new OrderViewDash(userID, fname);
            dispose();
        });
        editDetailsButton.addActionListener(e -> {
            new UserEditDash(userID, fname);
            dispose();
        });
        logoutButton.addActionListener(e -> {
            new App();
            dispose();
        });

    }
}
