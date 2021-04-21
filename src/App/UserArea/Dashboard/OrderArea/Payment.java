package App.UserArea.Dashboard.OrderArea;

import App.App;

import javax.swing.*;
import javax.swing.text.View;
import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.*;

public class Payment extends JFrame{
    private JButton logoutButton;
    private JTextField total;
    private JButton submitButton;
    private JButton returnButton;
    private JLabel pcPriceLabel;
    private JLabel remainingBalLabel;
    private JLabel afterPaymentLabel;
    private JPanel panel;
    private Connection connection;

    public Payment(int userID, int orderID, String fname) {
        Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
        this.setTitle("Computer Shop - Welcome");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setBounds(0,0,size.width, size.height);
        this.setVisible(true);
        this.add(panel);
        afterPaymentLabel.setText("Please Enter Amount To Pay Below and Hit Enter On Keyboard");
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:identifier.sqlite");
        } catch (SQLException connectionStart) {
            System.out.println(connectionStart.getMessage());
        }
        try {
            PreparedStatement getPrice = connection.prepareStatement("select * FROM Payments where orderID = ?");
            getPrice.setInt(1, orderID);
            ResultSet rs = getPrice.executeQuery();
            while(rs.next()) {
                pcPriceLabel.setText(String.valueOf(rs.getDouble("price")));
                remainingBalLabel.setText(String.valueOf(rs.getDouble("remainingBal")));
            }
            rs.close();
            getPrice.close();
        } catch (SQLException getOrder) {
            System.out.println(getOrder.getMessage());
        }
        total.addActionListener(e -> {
            double totalD = Double.parseDouble(total.getText());
            if (totalD > Double.parseDouble(remainingBalLabel.getText())) {
                JOptionPane.showMessageDialog(null, "Please enter a number less than or equal to remaining balance");
            } else {
                double afterPayment = Double.parseDouble(remainingBalLabel.getText()) - totalD;
                BigDecimal round = new BigDecimal(afterPayment).setScale(2, RoundingMode.HALF_UP);
                double afterPaymentRound = round.doubleValue();
                afterPaymentLabel.setText(String.valueOf(afterPaymentRound));
            }
        });
        logoutButton.addActionListener(e -> {
            try {
                connection.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            new App();
            dispose();
        });
        submitButton.addActionListener(e -> {
            double remaining = Double.parseDouble(remainingBalLabel.getText()) - Double.parseDouble(total.getText());
            BigDecimal round = new BigDecimal(remaining).setScale(2, RoundingMode.HALF_UP);
            double remaingingRound = round.doubleValue();
            if (Double.parseDouble(total.getText()) > Double.parseDouble(remainingBalLabel.getText())) {
                JOptionPane.showMessageDialog(null, "Please enter a number less than or equal to remaining balance");
            } else {
                try {
                    PreparedStatement getPrice = connection.prepareStatement("UPDATE Payments SET remainingBal = ? WHERE orderID = ?");
                    getPrice.setDouble(1, remaingingRound);
                    getPrice.setInt(2, orderID);
                    int rowsAffectedP = getPrice.executeUpdate();
                    if (rowsAffectedP == 1) {
                        getPrice.close();
                        try {
                            PreparedStatement updatePayment = connection.prepareStatement("UPDATE Payments SET paymentStatus = ? WHERE orderID = ?");
                            if (remaining == 0) {
                                updatePayment.setString(1, "Payment In Full");
                            } else {
                                updatePayment.setString(1, "Partial Payment");
                            }
                            updatePayment.setInt(2, orderID);
                            int rowsAffectedPayment = updatePayment.executeUpdate();
                            if (rowsAffectedPayment == 1 ) {
                                JOptionPane.showMessageDialog(null, "Payment Received! Thank you!");
                                updatePayment.close();
                                connection.close();
                                new ViewOrder(userID, orderID, fname);
                                dispose();
                            }
                        } catch (SQLException getOrder) {
                            System.out.println(getOrder.getMessage());
                        }
                    }
                } catch (SQLException getOrder) {
                    System.out.println(getOrder.getMessage());
                }
            }
        });
        returnButton.addActionListener(e -> {
            try {
                connection.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            new ViewOrder(userID, orderID, fname);
            dispose();
        });
    }
}
