package main.app.employeearea.dashboard.orders;

import main.app.App;
import main.app.employeearea.dashboard.EmployeeDash;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ViewOrders extends JFrame {
    private final DefaultTableModel newOrder = new DefaultTableModel(new String[]{"Order ID", "Order Status", "Order Type", "Remaining Balance", "Payment Status", "Overdue"}, 0);
    private final DefaultTableModel myOrder = new DefaultTableModel(new String[]{"Order ID", "Order Status", "Order Type", "Remaining Balance", "Payment Status", "Overdue"}, 0);
    private final int employeeID;
    private final SimpleDateFormat sFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    private final Date current = new Date();
    private JButton viewOrderButton;
    private JTable newOrderTable;
    private JTable myOrdersTable;
    private JPanel panel;
    private JButton claimOrderButton;
    private JButton logoutButton;
    private JLabel errorLabel;
    private JButton returnButton;
    private Connection connection;
    private String date = "";
    private Date dueDate;
    private Double balance;

    public ViewOrders(int employeeID, String fname) {
        errorLabel.setVisible(false);
        this.employeeID = employeeID;
        Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
        this.setTitle("Computer Shop - Welcome");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setBounds(0, 0, size.width, size.height);
        this.setVisible(true);
        this.add(panel);
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:identifier.sqlite");
        } catch (SQLException connectionStart) {
            System.out.println(connectionStart.getMessage());
        }
        getMyOrders();
        getNewOrders();
        claimOrderButton.addActionListener(e -> {
            if (newOrderTable.getSelectionModel().isSelectionEmpty()) {
                errorLabel.setText("Please select a row first!");
                errorLabel.setVisible(true);
            } else {
                errorLabel.setVisible(false);
                int selectedID = (Integer.parseInt(newOrder.getValueAt(newOrderTable.getSelectedRow(), 0).toString()));
                if (newOrder.getValueAt(newOrderTable.getSelectedRow(), 2).toString().equals("Rental")) {
                    int checkOrder = JOptionPane.showConfirmDialog(null, "This is a rental order.  Do you agree to ship & complete this order?");
                    if (checkOrder == 0) {
                        try {
                            PreparedStatement setOrder = connection.prepareStatement("UPDATE Orders SET employeeID = ?, orderStatus = ? WHERE orderID = ?");
                            setOrder.setInt(1, employeeID);
                            setOrder.setString(2, "Complete & Shipped");
                            setOrder.setInt(3, selectedID);
                            int rowsAffected = setOrder.executeUpdate();
                            if (rowsAffected == 1) {
                                JOptionPane.showMessageDialog(null, "You assigned yourself this order!");
                                setOrder.close();
                                connection.close();
                                new ViewOrders(employeeID, fname);
                                dispose();
                            }
                        } catch (SQLException error) {
                            System.err.println(error.getMessage());
                        }
                    }
                } else {
                    if (newOrder.getValueAt(newOrderTable.getSelectedRow(), 1).toString().equals("Refund Requested")) {
                        try {
                            PreparedStatement setOrder = connection.prepareStatement("UPDATE Orders SET employeeID = ? WHERE orderID = ?");
                            setOrder.setInt(1, employeeID);
                            setOrder.setInt(2, selectedID);
                            int rowsAffected = setOrder.executeUpdate();
                            if (rowsAffected == 1) {
                                JOptionPane.showMessageDialog(null, "You assigned yourself this order!");
                                setOrder.close();
                                connection.close();
                                new ViewOrders(employeeID, fname);
                                dispose();
                            }
                        } catch (SQLException error) {
                            System.err.println(error.getMessage());
                        }
                    } else {
                        try {
                            PreparedStatement setOrder = connection.prepareStatement("UPDATE Orders SET employeeID = ?, orderStatus = ? WHERE orderID = ?");
                            setOrder.setInt(1, employeeID);
                            setOrder.setString(2, "Builder Assigned");
                            setOrder.setInt(3, selectedID);
                            int rowsAffected = setOrder.executeUpdate();
                            if (rowsAffected == 1) {
                                JOptionPane.showMessageDialog(null, "You assigned yourself this order!");
                                setOrder.close();
                                connection.close();
                                new ViewOrders(employeeID, fname);
                                dispose();
                            }
                        } catch (SQLException error) {
                            System.err.println(error.getMessage());
                        }
                    }
                }


            }
        });
        logoutButton.addActionListener(e -> {
            closeConnection();
            new App();
            dispose();
        });
        returnButton.addActionListener(e -> {
            closeConnection();
            new EmployeeDash(employeeID, fname);
            dispose();
        });
        viewOrderButton.addActionListener(e -> {
            if (myOrdersTable.getSelectionModel().isSelectionEmpty()) {
                errorLabel.setText("Please select a row first!");
                errorLabel.setVisible(true);
            } else {
                int selectedID = (Integer.parseInt(myOrder.getValueAt(myOrdersTable.getSelectedRow(), 0).toString()));
                new ViewOrderEmployee(employeeID, selectedID, fname);
                dispose();
            }
        });
    }

    private void getMyOrders() {
        myOrder.setRowCount(0);
        try {
            PreparedStatement getOrders = connection.prepareStatement("SELECT O.orderID, orderStatus, orderType, paymentStatus, dueDate, remainingBal FROM Orders O INNER JOIN Payments P on O.orderID = P.orderID WHERE employeeID = ?");
            getOrders.setInt(1, employeeID);
            ResultSet rs = getOrders.executeQuery();
            while (rs.next()) {
                int orderID = rs.getInt("orderID");
                date = rs.getString("dueDate");
                balance = rs.getDouble("remainingBal");
                try {
                    dueDate = sFormat.parse(date);
                    if (current.compareTo(dueDate) >= 0) {
                        if (balance == 0.0) {
                            myOrder.addRow(new Object[]{orderID, rs.getString("orderStatus"), rs.getString("orderType"), balance, rs.getString("paymentStatus"), "False"});
                        } else {
                            myOrder.addRow(new Object[]{orderID, rs.getString("orderStatus"), rs.getString("orderType"), balance, rs.getString("paymentStatus"), "True"});
                        }
                    } else {
                        myOrder.addRow(new Object[]{orderID, rs.getString("orderStatus"), rs.getString("orderType"), balance, rs.getString("paymentStatus"), "False"});
                    }
                } catch (ParseException e) {
                    System.out.println(e.getMessage());
                }
            }
            myOrdersTable.setModel(myOrder);
            rs.close();
            getOrders.close();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    private void getNewOrders() {
        newOrder.setRowCount(0);
        try {
            PreparedStatement getOrders = connection.prepareStatement("select O.orderID, dueDate, remainingBal, orderStatus, orderType, paymentStatus FROM Orders O INNER JOIN Payments P on O.orderID = P.orderID WHERE O.employeeID IS NULL AND P.paymentStatus = ? or O.orderStatus = ? or O.orderStatus = ?");
            getOrders.setString(1, "Payment Received");
            getOrders.setString(2, "Refund Requested");
            getOrders.setString(3, "Awaiting Shipment");
            ResultSet rs = getOrders.executeQuery();
            while (rs.next()) {
                int orderID = rs.getInt("orderID");
                date = rs.getString("dueDate");
                balance = rs.getDouble("remainingBal");
                try {
                    dueDate = sFormat.parse(date);
                    if (current.compareTo(dueDate) >= 0) {
                        if (balance == 0.0) {
                            myOrder.addRow(new Object[]{orderID, rs.getString("orderStatus"), rs.getString("orderType"), balance, rs.getString("paymentStatus"), "False"});
                        } else {
                            myOrder.addRow(new Object[]{orderID, rs.getString("orderStatus"), rs.getString("orderType"), balance, rs.getString("paymentStatus"), "True"});
                        }
                    } else {
                        newOrder.addRow(new Object[]{orderID, rs.getString("orderStatus"), rs.getString("orderType"), balance, rs.getString("paymentStatus"), "FALSE"});
                    }
                } catch (ParseException e) {
                    System.out.println(e.getMessage());
                }
            }
            newOrderTable.setModel(newOrder);
            rs.close();
            getOrders.close();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
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
