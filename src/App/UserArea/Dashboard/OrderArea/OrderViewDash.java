package App.UserArea.Dashboard.OrderArea;

import App.App;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class OrderViewDash extends JFrame{
    private JButton logoutButton;
    private JButton viewOrderButton;
    private JTable table1;
    private JPanel panel;
    private JLabel errorLabel;
    private JButton returnButton;
    private final DefaultTableModel model = new DefaultTableModel(new String[]{"Order ID", "Order Status", "Price", "Remaining Balance"}, 0);
    private Connection connection;
    private final int userID;

    public OrderViewDash(int userID, String fname) {
        errorLabel.setVisible(false);
        viewOrderButton.setVisible(true);
        this.userID = userID;
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
        getTable();
        viewOrderButton.addActionListener(e -> {
            if (table1.getSelectionModel().isSelectionEmpty()) {
                errorLabel.setText("Please select a row first!");
                errorLabel.setVisible(true);
            } else {
                int selectedID = (Integer.parseInt(model.getValueAt(table1.getSelectedRow(), 0).toString()));
                errorLabel.setVisible(false);
                new ViewOrder(userID, selectedID, fname);
            }
        });
        logoutButton.addActionListener(e -> {
            new App();
            dispose();
        });
    }
    public void getTable() {
        model.setRowCount(0);
        try {
            PreparedStatement getOrders = connection.prepareStatement("select O.orderID, O.orderStatus, P.price, P.remainingBal FROM Orders O INNER JOIN Payments P on O.orderID = P.orderID WHERE O.userID AND P.userID = ?");
            getOrders.setInt(1, userID);
            ResultSet rs = getOrders.executeQuery();
            while (rs.next()) {
                int orderID = rs.getInt("orderID");
                model.addRow(new Object[]{orderID, rs.getString("orderStatus"), rs.getDouble("price"), rs.getDouble("remainingBal")});
            }
            table1.setModel(model);
            rs.close();
            getOrders.close();
            connection.close();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }
}