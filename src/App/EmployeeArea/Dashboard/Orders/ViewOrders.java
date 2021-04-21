package App.EmployeeArea.Dashboard.Orders;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class ViewOrders extends JFrame{
    private JButton viewOrderButton;
    private JTable newOrderTable;
    private JTable myOrdersTable;
    private JPanel panel;
    private JButton claimOrderButton;
    private JButton logoutButton;
    private JLabel errorLabel;
    private final DefaultTableModel newOrder = new DefaultTableModel(new String[]{"Order ID", "Order Status"}, 0);
    private final DefaultTableModel myOrder = new DefaultTableModel(new String[]{"Order ID", "Order Status"}, 0);
    private Connection connection;
    private final int employeeID;

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
            PreparedStatement getOrders = connection.prepareStatement("SELECT * FROM Orders WHERE employeeID IS NULL AND orderStatus = ? OR orderStatus =?");
            getOrders.setString(1, "Partial Payment");
            getOrders.setString(2, "Payment In Full");
            ResultSet rs = getOrders.executeQuery();
            while (rs.next()) {
                int orderID = rs.getInt("orderID");
                newOrder.addRow(new Object[]{orderID, rs.getString("orderStatus")});
            }
            newOrderTable.setModel(newOrder);
            rs.close();
            getOrders.close();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }
}
