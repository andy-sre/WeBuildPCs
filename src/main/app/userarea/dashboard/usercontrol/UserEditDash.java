package main.app.userarea.dashboard.usercontrol;

import main.app.App;
import main.app.userarea.dashboard.UserDash;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class UserEditDash extends JFrame {
    private JButton logoutButton;
    private JButton editMyProfileButton;
    private JButton returnButton;
    private JButton deleteMyProfileButton;
    private JPanel panel;
    private Connection connection;
    private Double balance;

    public UserEditDash(int userID, String fname) {
        Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
        this.setTitle("Computer Shop - Welcome");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setBounds(0, 0, size.width, size.height);
        this.setVisible(true);
        this.add(panel);

        try {
            connection = DriverManager.getConnection("jdbc:sqlite:identifier.sqlite");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        editMyProfileButton.addActionListener(e -> {
            new UserEdit(userID, fname);
            dispose();
        });
        deleteMyProfileButton.addActionListener(e -> delete(userID));
        logoutButton.addActionListener(e -> {
            closeConnection();
            new App();
            dispose();
        });
        returnButton.addActionListener(e -> {
            closeConnection();
            new UserDash(userID, fname);
            dispose();
        });
    }

    private void delete(int userID) {
        int input = JOptionPane.showConfirmDialog(null,
                "Do you want to proceed?", "Deleting Your Account...", JOptionPane.YES_NO_CANCEL_OPTION);
        if (input == 0) {
            try {
                PreparedStatement checkBalances = connection.prepareStatement("SELECT remainingBal FROM Payments WHERE userID = ?");
                checkBalances.setInt(1, userID);
                ResultSet rs = checkBalances.executeQuery();
                while (rs.next()) {
                    if (rs.getDouble("remainingBal") > 0) {
                        balance = rs.getDouble("remainingBal");
                    } else {
                        balance = 0.0;
                    }
                }
                if (balance == 0.0) {
                    rs.close();
                    checkBalances.close();
                    try {
                        PreparedStatement deleteUser = connection.prepareStatement("DELETE FROM Users WHERE userID = ?");
                        deleteUser.setInt(1, userID);
                        int rowsAffectedUsers = deleteUser.executeUpdate();
                        if (rowsAffectedUsers == 1) {
                            try {
                                deleteUser.close();
                                PreparedStatement deletePayments = connection.prepareStatement("DELETE FROM Payments WHERE userID = ?");
                                deletePayments.setInt(1, userID);
                                int rowsAffectedPayments = deletePayments.executeUpdate();
                                if (rowsAffectedPayments == 1) {
                                    deletePayments.close();
                                    try {
                                        PreparedStatement deleteOrder = connection.prepareStatement("DELETE FROM Orders WHERE userID = ?");
                                        deleteOrder.setInt(1, userID);
                                        int rowsAffectedOrder = deleteOrder.executeUpdate();
                                        if (rowsAffectedOrder == 1) {
                                            deleteUser.close();
                                            connection.close();
                                            new App();
                                            dispose();
                                        }
                                    } catch (SQLException deleteOrderError) {
                                        System.out.println(deleteOrderError.getMessage());
                                    }
                                }
                            } catch (SQLException deletePaymentError) {
                                System.out.println(deletePaymentError.getMessage());
                            }
                        }
                    } catch (SQLException deleteUserError) {
                        System.out.println(deleteUserError.getMessage());
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "You can't delete your account.  You have a balance due!");
                }
            } catch (SQLException updateUserErr) {
                System.out.println(updateUserErr.getMessage());
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

