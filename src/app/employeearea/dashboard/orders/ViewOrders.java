package app.employeearea.dashboard.orders;

import app.App;
import app.employeearea.dashboard.EmployeeDash;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ViewOrders extends JFrame{
    private JButton viewOrderButton;
    private JTable newOrderTable;
    private JTable myOrdersTable;
    private JPanel panel;
    private JButton claimOrderButton;
    private JButton logoutButton;
    private JLabel errorLabel;
    private JButton returnButton;
    private final DefaultTableModel newOrder = new DefaultTableModel(new String[]{"Order ID", "Order Status", "Remaining Balance", "Payment Status", "Overdue"}, 0);
    private final DefaultTableModel myOrder = new DefaultTableModel(new String[]{"Order ID", "Order Status", "Remaining Balance", "Payment Status", "Overdue"}, 0);
    private Connection connection;
    private final int employeeID;
    private String date = "";
    private java.util.Date dueDate;
    private SimpleDateFormat sFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    private java.util.Date current = new Date();
    private Double balance;

    public ViewOrders(int employeeID, String fname) {
        errorLabel.setVisible(false);
        this.employeeID = employeeID;
        Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
        this.setTitle("Computer Shop - Welcome");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setBounds(0,0,size.width, size.height);
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
        returnButton.addActionListener(e -> {
            try {
                connection.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            new EmployeeDash(employeeID,fname);
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
            PreparedStatement getOrders = connection.prepareStatement("SELECT * FROM Orders WHERE employeeID = ?");
            getOrders.setInt(1, employeeID);
            ResultSet rs = getOrders.executeQuery();
            while (rs.next()) {
                int orderID = rs.getInt("orderID");
                myOrder.addRow(new Object[]{orderID, rs.getString("orderStatus")});
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
            PreparedStatement getOrders = connection.prepareStatement("select * FROM Orders O INNER JOIN Payments P on O.orderID = P.orderID WHERE O.employeeID IS NULL AND P.paymentStatus = ? or O.orderStatus = ?");
            getOrders.setString(1, "Payment Received");
            getOrders.setString(2, "Refund Requested");
            ResultSet rs = getOrders.executeQuery();
            while (rs.next()) {
                int orderID = rs.getInt("orderID");
                date = rs.getString("dueDate");
                balance = rs.getDouble("remainingBal");
                System.out.println("Test 0");
                try {
                    dueDate = sFormat.parse(date);
                    if(current.compareTo(dueDate) >= 0) {
                        System.out.println("Test 1");
                        newOrder.addRow(new Object[]{orderID, rs.getString("orderStatus"), balance, rs.getString("paymentStatus"), "TRUE"});
                    } else {
                        System.out.println("Test 2");
                        newOrder.addRow(new Object[]{orderID, rs.getString("orderStatus"), balance, rs.getString("paymentStatus"), "FALSE"});
                    }
                } catch (ParseException e) {
                    System.out.println(e);
                }

            }
            newOrderTable.setModel(newOrder);
            rs.close();
            getOrders.close();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }
}
