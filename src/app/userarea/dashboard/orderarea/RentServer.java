package app.userarea.dashboard.orderarea;

import app.App;
import app.userarea.dashboard.UserDash;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class RentServer extends JFrame {
    private JButton logoutButton;
    private JComboBox<Item> serverDropdown;
    private JTextField serverPrice;
    private JComboBox<Integer> serverQuantity;
    private JButton returnButton;
    private JButton submitButton;
    private JLabel errorLabel;
    private JLabel priceLabel;
    private JPanel panel;
    private JTextField serverStock;
    private JLabel dueLabel;
    private Connection connection;
    private double serverPriceFinal;
    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    Date current = new Date();
    private Item serverItem;

    public RentServer(int userID, String fname) {
        Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
        this.setTitle("Computer Shop - Welcome");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setBounds(0, 0, size.width, size.height);
        this.setVisible(true);
        this.add(panel);
        Calendar c = Calendar.getInstance();
        c.setTime(current);
        c.add(Calendar.MONTH, 1);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        Date futureDate = c.getTime();
        dueLabel.setText(String.valueOf(futureDate));
        errorLabel.setVisible(false);
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:identifier.sqlite");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        getParts();
        serverDropdown.addActionListener(e -> {
            serverItem = (Item) serverDropdown.getSelectedItem();
            assert serverItem != null;
            serverPriceFinal = serverItem.getPrice();
            serverPrice.setText(String.valueOf(serverPriceFinal));
            serverStock.setText(String.valueOf(serverItem.getQuantity()));
            updatePrice();
        });
        serverQuantity.addActionListener(e -> updatePrice());

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
            new UserDash(userID, fname);
            dispose();
        });
        submitButton.addActionListener(e -> {
            if (checkBox()) {
                errorLabel.setVisible(false);
                try {
                    PreparedStatement createOrder = connection.prepareStatement("INSERT INTO Orders (orderStatus, userID, serverID, serverAmount, orderType) VALUES (?, ?, ?, ?, ?)");
                    createOrder.setString(1, "Awaiting Shipment");
                    createOrder.setInt(2, userID);
                    createOrder.setInt(3, serverItem.getItemID());
                    createOrder.setInt(4, Integer.parseInt(serverQuantity.getSelectedItem().toString()));
                    createOrder.setString(5, "Rental");
                    int rowsAffectedO = createOrder.executeUpdate();
                    if (rowsAffectedO == 1) {
                        createOrder.close();
                        try {

                            PreparedStatement createPayment = connection.prepareStatement("INSERT INTO Payments (userID, orderID, price, paymentStatus, dueDate, remainingBal) VALUES (?,?,?,?,?,?)");
                            createPayment.setInt(1, userID);
                            PreparedStatement getOrderID = connection.prepareStatement("SELECT orderID FROM Orders WHERE userID = "+userID);
                            ResultSet rs = getOrderID.executeQuery();
                            while (rs.next()) {
                                createPayment.setInt(2, rs.getInt("orderID"));
                            }
                            getOrderID.close();
                            rs.close();
                            createPayment.setDouble(3, Double.parseDouble(priceLabel.getText()));
                            createPayment.setString(4, "Due On Due Date");
                            createPayment.setString(5, dateFormat.format(futureDate));
                            createPayment.setDouble(6, Double.parseDouble(priceLabel.getText()));
                            int rowsAffectedP = createPayment.executeUpdate();
                            if (rowsAffectedP == 1) {
                                createPayment.close();
                                updateStock();
                                JOptionPane.showMessageDialog(null, "Order Created, redirecting you to your orders area");
                                connection.close();
                                new UserDash(userID, fname);
                                dispose();
                            }
                        } catch (SQLException createPayment) {
                            System.out.println(createPayment.getMessage());
                        }
                    }
                } catch (SQLException createOrder) {
                    System.out.println(createOrder.getMessage());
                }
            }
        });
    }

    private void getParts() {
        int quantity;
        ResultSet rs;
        double price;
        String name;
        int itemID;
        try {
            PreparedStatement getCPU = connection.prepareStatement("select * from Parts where partType = ?");
            getCPU.setString(1, "server");
            rs = getCPU.executeQuery();
            while (rs.next()) {
                itemID = rs.getInt("partID");
                name = rs.getString("partName");
                price = rs.getDouble("price");
                quantity = rs.getInt("quantity");
                serverDropdown.addItem(new Item(itemID, name, price, quantity));
            }
            serverDropdown.setSelectedIndex(-1);
            for (int i = 1; i < 11; i++) {
                serverQuantity.addItem(i);
            }
            getCPU.close();
            rs.close();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    private static class Item {
        private final int itemID;
        private final String name;
        private final double price;
        private final int quantity;

        private Item(Integer itemID, String name, Double price, int quantity) {
            this.itemID = itemID;
            this.name = name;
            this.price = price;
            this.quantity = quantity;
        }

        private int getItemID() { return itemID; }

        private String getItemName() {
            return name;
        }

        private double getPrice() { return price; }

        private int getQuantity() { return quantity; }

        @Override
        public String toString() {
            return getItemName();
        }
    }

    private void updatePrice() {
        double totalPrice = (serverPriceFinal * Integer.parseInt(Objects.requireNonNull(serverQuantity.getSelectedItem()).toString()));
        BigDecimal round = new BigDecimal(totalPrice).setScale(2, RoundingMode.HALF_UP);
        double finalPrice = round.doubleValue();
        priceLabel.setText(String.valueOf(finalPrice));
    }

    public boolean checkBox() {
        if (serverDropdown.getSelectedIndex() == -1) {
            errorLabel.setText("Please Select a CPU");
            errorLabel.setVisible(true);
        } else {
            return true;
        }
        return false;
    }

    public void updateStock() {
        try {
            int updatedStock = serverItem.getQuantity() - Integer.parseInt(Objects.requireNonNull(serverQuantity.getSelectedItem()).toString());
            PreparedStatement updateCPUStock = connection.prepareStatement("UPDATE Parts SET quantity = ? WHERE partID = ?");
            updateCPUStock.setInt(1, updatedStock);
            updateCPUStock.setInt(2, serverItem.getItemID());
            updateCPUStock.executeUpdate();
            updateCPUStock.close();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

}
