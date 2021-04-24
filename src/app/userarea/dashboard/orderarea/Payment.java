package app.userarea.dashboard.orderarea;

import app.App;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Payment extends JFrame {
    private JButton logoutButton;
    private JTextField total;
    private JButton submitButton;
    private JButton returnButton;
    private JLabel pcPriceLabel;
    private JLabel remainingBalLabel;
    private JLabel afterPaymentLabel;
    private JPanel panel;
    private Connection connection;
    private String orderType;
    private String dueDateString;
    private Date due;

    public Payment(int userID, int orderID, String fname) {
        Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
        this.setTitle("Computer Shop - Welcome");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setBounds(0, 0, size.width, size.height);
        this.setVisible(true);
        this.add(panel);
        afterPaymentLabel.setText("Please Enter Amount To Pay Below and Hit Enter On Keyboard");
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:identifier.sqlite");
        } catch (SQLException connectionStart) {
            System.out.println(connectionStart.getMessage());
        }
        try {
            PreparedStatement getPrice = connection.prepareStatement("select * FROM Payments P INNER JOIN Orders O on O.orderID = P.orderID where P.orderID = ?");
            getPrice.setInt(1, orderID);
            ResultSet rs = getPrice.executeQuery();
            while (rs.next()) {
                pcPriceLabel.setText(String.valueOf(rs.getDouble("price")));
                remainingBalLabel.setText(String.valueOf(rs.getDouble("remainingBal")));
                dueDateString = rs.getString("dueDate");
                orderType = rs.getString("orderType");
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
                BigDecimal round = new BigDecimal(String.valueOf(afterPayment)).setScale(2, RoundingMode.HALF_UP);
                double afterPaymentRound = round.doubleValue();
                afterPaymentLabel.setText(String.valueOf(afterPaymentRound));
            }
        });
        logoutButton.addActionListener(e -> {
            closeConnection();
            new App();
            dispose();
        });
        submitButton.addActionListener(e -> {
            submit(userID, orderID,fname);
        });
        returnButton.addActionListener(e -> {
            closeConnection();
            new ViewOrder(userID, orderID, fname);
            dispose();
        });
    }

    private void submit(int userID, int orderID, String fname){
        double remaining = Double.parseDouble(remainingBalLabel.getText()) - Double.parseDouble(total.getText());
        BigDecimal round = new BigDecimal(String.valueOf(remaining)).setScale(2, RoundingMode.HALF_UP);
        double remaingingRound = round.doubleValue();
        if (Double.parseDouble(total.getText()) > Double.parseDouble(remainingBalLabel.getText())) {
            JOptionPane.showMessageDialog(null, "Please enter a number less than or equal to remaining balance");
        } else {
            if (orderType.equals("PC")) {
                try {
                    PreparedStatement getPrice = connection.prepareStatement("UPDATE Payments SET remainingBal = ? WHERE orderID = ?");
                    getPrice.setDouble(1, remaingingRound);
                    getPrice.setInt(2, orderID);
                    int rowsAffectedPrice = getPrice.executeUpdate();
                    if (rowsAffectedPrice == 1) {
                        getPrice.close();
                        try {
                            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                            Date current = new Date();
                            Calendar c = Calendar.getInstance();
                            c.setTime(current);
                            c.add(Calendar.MONTH, 1);
                            c.set(Calendar.HOUR_OF_DAY, 0);
                            c.set(Calendar.MINUTE, 0);
                            c.set(Calendar.SECOND, 0);
                            c.set(Calendar.MILLISECOND, 0);
                            Date futureDate = c.getTime();
                            PreparedStatement updatePayment = connection.prepareStatement("UPDATE Payments SET paymentStatus = ?, dueDate = ? WHERE orderID = ?");
                            updatePayment.setString(1, "Payment Received");
                            updatePayment.setString(2, dateFormat.format(futureDate));
                            updatePayment.setInt(3, orderID);
                            int rowsAffectedPayment = updatePayment.executeUpdate();
                            if (rowsAffectedPayment == 1) {
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
            } else {
                try {
                    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                    try {
                        due = dateFormat.parse(dueDateString);
                    } catch (ParseException parseErr) {
                        System.out.println(parseErr.getMessage());
                    }
                    Calendar c = Calendar.getInstance();
                    c.setTime(due);
                    c.add(Calendar.MONTH, 1);
                    c.set(Calendar.HOUR_OF_DAY, 0);
                    c.set(Calendar.MINUTE, 0);
                    c.set(Calendar.SECOND, 0);
                    c.set(Calendar.MILLISECOND, 0);
                    Date futureDate = c.getTime();
                    PreparedStatement updatePayment = connection.prepareStatement("UPDATE Payments SET paymentStatus = ?, dueDate = ? WHERE orderID = ?");
                    updatePayment.setString(1, "Check next due date");
                    updatePayment.setString(2, dateFormat.format(futureDate));
                    updatePayment.setInt(3, orderID);
                    int rowsAffectedPayment = updatePayment.executeUpdate();
                    if (rowsAffectedPayment == 1) {
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

        }
    }

    private void closeConnection() {
        try {
            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
