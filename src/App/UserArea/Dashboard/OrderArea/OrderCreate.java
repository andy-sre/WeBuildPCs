package App.UserArea.Dashboard.OrderArea;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.Objects;

public class OrderCreate extends JFrame {
    private JPanel panel;
    private JComboBox<Item> cpuDropdown;
    private JComboBox<Item> gpuDropdown;
    private JComboBox<Item> moboDropdown;
    private JComboBox<Item> ramDropdown;
    private JComboBox<Item> storageDropdown;
    private JComboBox<Item> psuDropdown;
    private JComboBox<Item> caseDropdown;
    private JButton submitButton;
    private JButton returnButton;
    private JButton logoutButton;
    private JTextField cpuPrice;
    private JTextField cpuStock;
    private JTextField gpuPrice;
    private JTextField gpuStock;
    private JTextField moboPrice;
    private JTextField moboStock;
    private JTextField ramStock;
    private JTextField storageStock;
    private JTextField psuStock;
    private JTextField caseStock;
    private JTextField ramPrice;
    private JTextField storagePrice;
    private JTextField psuPrice;
    private JTextField casePrice;
    private JTextField totalPrice;
    private JComboBox<Integer> cpuQuantity;
    private JComboBox<Integer> gpuQuantity;
    private JComboBox<Integer> moboQuantity;
    private JComboBox<Integer> ramQuantity;
    private JComboBox<Integer> storageQuantity;
    private JComboBox<Integer> psuQuantity;
    private JComboBox<Integer> caseQuantity;
    private Connection connection;
    private double cpuPriceFinal;
    private double gpuPriceFinal;
    private double ramPriceFinal;
    private double moboPriceFinal;
    private double storagePriceFinal;
    private double psuPriceFinal;
    private double casePriceFinal;
    private double pcPrice;

    public OrderCreate(int userID) {
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
        getParts();
        ActionListener listener = e -> {

        };
        submitButton.addActionListener(listener);
        returnButton.addActionListener(listener);
        ActionListener listener1 = e -> {

        };
        submitButton.addActionListener(listener1);
        returnButton.addActionListener(listener1);
        logoutButton.addActionListener(e -> {

        });

        cpuDropdown.addActionListener(e -> {
            Item cpuItem = (Item) cpuDropdown.getSelectedItem();
            assert cpuItem != null;
            cpuPriceFinal = cpuItem.getPrice();
            cpuPrice.setText(String.valueOf(cpuPriceFinal));
            cpuStock.setText(String.valueOf(cpuItem.getQuantity()));
            updatePrice();

        });
        gpuDropdown.addActionListener(e -> {
            Item gpuItem = (Item) gpuDropdown.getSelectedItem();
            assert gpuItem != null;
            gpuPriceFinal = gpuItem.getPrice();
            gpuPrice.setText(String.valueOf(gpuPriceFinal));
            gpuStock.setText(String.valueOf(gpuItem.getQuantity()));
            updatePrice();

        });
        moboDropdown.addActionListener(e -> {
            Item moboItem = (Item) moboDropdown.getSelectedItem();
            assert moboItem != null;
            moboPriceFinal = moboItem.getPrice();
            moboPrice.setText(String.valueOf(moboPriceFinal));
            moboStock.setText(String.valueOf(moboItem.getQuantity()));
            updatePrice();

        });
        ramDropdown.addActionListener(e -> {
            Item ramItem = (Item) ramDropdown.getSelectedItem();
            assert ramItem != null;
            ramPriceFinal = ramItem.getPrice();
            ramPrice.setText(String.valueOf(ramPriceFinal));
            ramStock.setText(String.valueOf(ramItem.getQuantity()));
            updatePrice();
        });
        storageDropdown.addActionListener(e -> {
            Item storageItem = (Item) storageDropdown.getSelectedItem();
            assert storageItem != null;
            storagePriceFinal = storageItem.getPrice();
            storagePrice.setText(String.valueOf(storagePriceFinal));
            storageStock.setText(String.valueOf(storageItem.getQuantity()));
            updatePrice();

        });
        psuDropdown.addActionListener(e -> {
            Item psuItem = (Item) psuDropdown.getSelectedItem();
            assert psuItem != null;
            psuPriceFinal = psuItem.getPrice();
            psuPrice.setText(String.valueOf(psuPriceFinal));
            psuStock.setText(String.valueOf(psuItem.getQuantity()));
            pcPrice = cpuPriceFinal + gpuPriceFinal + ramPriceFinal + moboPriceFinal + casePriceFinal + storagePriceFinal + storagePriceFinal;
            totalPrice.setText(String.valueOf(pcPrice));
        });
        caseDropdown.addActionListener(e -> {
            Item caseItem = (Item) caseDropdown.getSelectedItem();
            assert caseItem != null;
            casePriceFinal = caseItem.getPrice();
            casePrice.setText(String.valueOf(casePriceFinal));
            caseStock.setText(String.valueOf(caseItem.getQuantity()));
            updatePrice();

        });
        cpuQuantity.addActionListener(e -> updatePrice());
        gpuQuantity.addActionListener(e -> updatePrice());
        ramQuantity.addActionListener(e -> updatePrice());
        storageQuantity.addActionListener(e -> updatePrice());
    }

    private void getParts() {
        int quantity;
        ResultSet rs;
        double price;
        String name;
        int itemID;
        try {
            PreparedStatement getCPU = connection.prepareStatement("select * from CPU");
            rs = getCPU.executeQuery();
            while (rs.next()) {
                itemID = rs.getInt("cpuID");
                name = rs.getString("name");
                price = rs.getDouble("price");
                quantity = rs.getInt("quantity");
                cpuDropdown.addItem(new Item(itemID, name, price, quantity));
            }
            cpuDropdown.setSelectedIndex(-1);
            for (int i = 1; i < 3; i++) {
                cpuQuantity.addItem(i);
            }
            getCPU.close();
            rs.close();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        try {
            PreparedStatement getGPU = connection.prepareStatement("select * from GPU");
            rs = getGPU.executeQuery();
            while (rs.next()) {
                itemID = rs.getInt("gpuID");
                name = rs.getString("name");
                price = rs.getDouble("price");
                quantity = rs.getInt("quantity");
                gpuDropdown.addItem(new Item(itemID, name, price, quantity));
            }
            gpuDropdown.setSelectedIndex(-1);
            for (int i = 1; i < 5; i++) {
                gpuQuantity.addItem(i);
            }
            getGPU.close();
            rs.close();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        try {
            PreparedStatement getMobo = connection.prepareStatement("select * from Motherboard");
            rs = getMobo.executeQuery();
            while (rs.next()) {
                itemID = rs.getInt("motherboardID");
                name = rs.getString("name");
                price = rs.getDouble("price");
                quantity = rs.getInt("quantity");
                moboDropdown.addItem(new Item(itemID, name, price, quantity));
            }
            moboDropdown.setSelectedIndex(-1);
            moboQuantity.addItem(1);
            getMobo.close();
            rs.close();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        try {
            PreparedStatement getPSU = connection.prepareStatement("select * from PSU");
            rs = getPSU.executeQuery();
            while (rs.next()) {
                itemID = rs.getInt("powerSupplyID");
                name = rs.getString("name");
                price = rs.getDouble("price");
                quantity = rs.getInt("quantity");
                psuDropdown.addItem(new Item(itemID, name, price, quantity));
            }
            psuDropdown.setSelectedIndex(-1);
            psuQuantity.addItem(1);
            getPSU.close();
            rs.close();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        try {
            PreparedStatement getRAM = connection.prepareStatement("select * from RAM");
            rs = getRAM.executeQuery();
            while (rs.next()) {
                itemID = rs.getInt("ramID");
                name = rs.getString("name");
                price = rs.getDouble("price");
                quantity = rs.getInt("quantity");
                ramDropdown.addItem(new Item(itemID, name, price, quantity));
            }
            ramDropdown.setSelectedIndex(-1);
            for (int i = 1; i < 9; i++) {
                ramQuantity.addItem(i);
            }
            getRAM.close();
            rs.close();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        try {
            PreparedStatement getCase = connection.prepareStatement("select * from PCCase");
            rs = getCase.executeQuery();
            while (rs.next()) {
                itemID = rs.getInt("pccaseID");
                name = rs.getString("name");
                price = rs.getDouble("price");
                quantity = rs.getInt("quantity");
                caseDropdown.addItem(new Item(itemID, name, price, quantity));
            }
            caseDropdown.setSelectedIndex(-1);
            caseQuantity.addItem(1);
            getCase.close();
            rs.close();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        try {
            PreparedStatement getStorage = connection.prepareStatement("select * from Storage");
            rs = getStorage.executeQuery();
            while (rs.next()) {
                itemID = rs.getInt("storageID");
                name = rs.getString("name");
                price = rs.getDouble("price");
                quantity = rs.getInt("quantity");
                storageDropdown.addItem(new Item(itemID, name, price, quantity));
            }
            storageDropdown.setSelectedIndex(-1);
            for (int i = 1; i < 11; i++) {
                storageQuantity.addItem(i);
            }
            getStorage.close();
            rs.close();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    private void updatePrice() {
        pcPrice = (cpuPriceFinal * Integer.parseInt(Objects.requireNonNull(cpuQuantity.getSelectedItem()).toString())) +
                (gpuPriceFinal * Integer.parseInt(Objects.requireNonNull(gpuQuantity.getSelectedItem()).toString())) +
                (ramPriceFinal * Integer.parseInt(Objects.requireNonNull(ramQuantity.getSelectedItem()).toString())) +
                (moboPriceFinal * Integer.parseInt(Objects.requireNonNull(moboQuantity.getSelectedItem()).toString())) +
                (casePriceFinal * Integer.parseInt(Objects.requireNonNull(caseQuantity.getSelectedItem()).toString())) +
                (storagePriceFinal * Integer.parseInt(Objects.requireNonNull(storageQuantity.getSelectedItem()).toString())) +
                (storagePriceFinal * Integer.parseInt(storageQuantity.getSelectedItem().toString()));
        totalPrice.setText(String.valueOf(pcPrice));
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
            return "Name: " + getItemName();
        }
    }


}