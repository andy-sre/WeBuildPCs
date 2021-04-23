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
    private JLabel errorLabel;
    private Connection connection;
    private double cpuPriceFinal;
    private double gpuPriceFinal;
    private double ramPriceFinal;
    private double moboPriceFinal;
    private double storagePriceFinal;
    private double psuPriceFinal;
    private double casePriceFinal;
    private double pcPrice;
    private Item cpuItem;
    private Item gpuItem;
    private Item ramItem;
    private Item moboItem;
    private Item storageItem;
    private Item psuItem;
    private Item caseItem;
    private boolean stockIssue = false;

    public OrderCreate(int userID, String fname) {
        errorLabel.setVisible(false);
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

        submitButton.addActionListener(e -> {
            if (checkBox()) {
                errorLabel.setVisible(false);
                try {
                    cpuItem = (Item) cpuDropdown.getSelectedItem();
                    gpuItem = (Item) gpuDropdown.getSelectedItem();
                    ramItem = (Item) ramDropdown.getSelectedItem();
                    moboItem = (Item) moboDropdown.getSelectedItem();
                    storageItem = (Item) storageDropdown.getSelectedItem();
                    psuItem = (Item) psuDropdown.getSelectedItem();
                    caseItem = (Item) caseDropdown.getSelectedItem();

                    if (cpuItem.getQuantity()<cpuQuantity.getSelectedIndex()){
                        errorLabel.setText("We do not have enough cpus in stock");
                        errorLabel.setVisible(true);
                    }else if (gpuItem.getQuantity()<cpuQuantity.getSelectedIndex()){
                        errorLabel.setText("We do not have enough gpus in stock");
                        errorLabel.setVisible(true);
                    }else if (ramItem.getQuantity()<ramQuantity.getSelectedIndex()){
                        errorLabel.setText("We do not have enough ram in stock");
                        errorLabel.setVisible(true);
                        stockIssue = true;
                    }else if (moboItem.getQuantity()<moboQuantity.getSelectedIndex()){
                        errorLabel.setText("We do not have enough motherboards in stock");
                        errorLabel.setVisible(true);
                        stockIssue = true;
                    }else if (storageItem.getQuantity()<storageQuantity.getSelectedIndex()){
                        errorLabel.setText("We do not have enough storage in stock");
                        errorLabel.setVisible(true);
                        stockIssue = true;
                    }else if (psuItem.getQuantity()<psuQuantity.getSelectedIndex()){
                        errorLabel.setText("We do not have enough psu in stock");
                        errorLabel.setVisible(true);
                        stockIssue = true;
                    }else if (caseItem.getQuantity()<caseQuantity.getSelectedIndex()){
                        errorLabel.setText("We do not have enough cases in stock");
                        errorLabel.setVisible(true);
                        stockIssue = true;
                    }else{
                        stockIssue = false;
                    }

                    if (stockIssue == false){
                        PreparedStatement createOrder = connection.prepareStatement("INSERT INTO Orders (orderStatus, " +
                                "userID, cpuID, cpuAmount, gpuID, gpuAmount, ramID, ramAmount, motherBoardID, " +
                                "motherBoardAmount, pcCaseID, pcCaseAmount, psuID, psuAmount, storageAmount, storageID, orderType) " +
                                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                        createOrder.setString(1, "Order Created");
                        createOrder.setInt(2, userID);
                        assert cpuItem != null;
                        createOrder.setInt(3, cpuItem.getItemID());
                        createOrder.setInt(4, Integer.parseInt(Objects.requireNonNull(cpuQuantity.getSelectedItem()).toString()));
                        assert gpuItem != null;
                        createOrder.setInt(5, gpuItem.getItemID());
                        createOrder.setInt(6, Integer.parseInt(Objects.requireNonNull(gpuQuantity.getSelectedItem()).toString()));
                        assert ramItem != null;
                        createOrder.setInt(7, ramItem.getItemID());
                        createOrder.setInt(8, Integer.parseInt(Objects.requireNonNull(ramQuantity.getSelectedItem()).toString()));
                        assert moboItem != null;
                        createOrder.setInt(9, moboItem.getItemID());
                        createOrder.setInt(10, Integer.parseInt(Objects.requireNonNull(moboQuantity.getSelectedItem()).toString()));
                        assert storageItem != null;
                        createOrder.setInt(11, storageItem.getItemID());
                        createOrder.setInt(12, Integer.parseInt(Objects.requireNonNull(storageQuantity.getSelectedItem()).toString()));
                        assert psuItem != null;
                        createOrder.setInt(13, psuItem.getItemID());
                        createOrder.setInt(14, Integer.parseInt(Objects.requireNonNull(psuQuantity.getSelectedItem()).toString()));
                        assert caseItem != null;
                        createOrder.setInt(15, caseItem.getItemID());
                        createOrder.setInt(16, Integer.parseInt(Objects.requireNonNull(caseQuantity.getSelectedItem()).toString()));
                        createOrder.setString(17, "PC");
                        int rowsAffectedO = createOrder.executeUpdate();
                        if (rowsAffectedO == 1) {
                            createOrder.close();
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
                                PreparedStatement createPayment = connection.prepareStatement("INSERT INTO Payments (userID, orderID, price, remainingBal, paymentStatus, dueDate) VALUES (?,?,?,?,?,?)");
                                createPayment.setInt(1, userID);
                                PreparedStatement getOrderID = connection.prepareStatement("SELECT orderID FROM Orders WHERE userID = "+userID);
                                ResultSet rs = getOrderID.executeQuery();
                                while (rs.next()) {
                                    createPayment.setInt(2, rs.getInt("orderID"));
                                }
                                getOrderID.close();
                                rs.close();
                                createPayment.setDouble(3, pcPrice);
                                createPayment.setDouble(4, pcPrice);
                                createPayment.setString(5, "Payment Due");
                                createPayment.setString(6, dateFormat.format(futureDate));
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
                    }
                } catch (SQLException createOrder) {
                    System.out.println(createOrder.getMessage());
                }
            }
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
        logoutButton.addActionListener(e -> {
            try {
                connection.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            new App();
            dispose();
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
            getCPU.setString(1, "cpu");
            rs = getCPU.executeQuery();
            while (rs.next()) {
                itemID = rs.getInt("partID");
                name = rs.getString("partName");
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
            PreparedStatement getGPU = connection.prepareStatement("select * from Parts where partType = ?");
            getGPU.setString(1, "gpu");
            rs = getGPU.executeQuery();
            while (rs.next()) {
                itemID = rs.getInt("partID");
                name = rs.getString("partName");
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
            PreparedStatement getMobo = connection.prepareStatement("select * from Parts where partType = ?");
            getMobo.setString(1, "mobo");
            rs = getMobo.executeQuery();
            while (rs.next()) {
                itemID = rs.getInt("partID");
                name = rs.getString("partName");
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
            PreparedStatement getPSU = connection.prepareStatement("select * from Parts where partType = ?");
            getPSU.setString(1, "psu");
            rs = getPSU.executeQuery();
            while (rs.next()) {
                itemID = rs.getInt("partID");
                name = rs.getString("partName");
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
            PreparedStatement getRAM = connection.prepareStatement("select * from Parts where partType = ?");
            getRAM.setString(1, "ram");
            rs = getRAM.executeQuery();
            while (rs.next()) {
                itemID = rs.getInt("partID");
                name = rs.getString("partName");
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
            PreparedStatement getCase = connection.prepareStatement("select * from Parts where partType = ?");
            getCase.setString(1, "pcCase");
            rs = getCase.executeQuery();
            while (rs.next()) {
                itemID = rs.getInt("partID");
                name = rs.getString("partName");
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
            PreparedStatement getStorage = connection.prepareStatement("select * from Parts where partType == 'storage'");
            rs = getStorage.executeQuery();
            while (rs.next()) {
                itemID = rs.getInt("partID");
                name = rs.getString("partName");
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
        BigDecimal round = new BigDecimal(pcPrice).setScale(2, RoundingMode.HALF_UP);
        pcPrice = round.doubleValue();
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
            return getItemName();
        }
    }

    public boolean checkBox() {
        if (cpuDropdown.getSelectedIndex() == -1) {
            errorLabel.setText("Please Select a CPU");
            errorLabel.setVisible(true);
        } else if (gpuDropdown.getSelectedIndex() == -1) {
            errorLabel.setText("Please Select a GPU");
            errorLabel.setVisible(true);
        } else if (ramDropdown.getSelectedIndex() == -1) {
            errorLabel.setText("Please Select a RAM module");
            errorLabel.setVisible(true);
        } else if (moboDropdown.getSelectedIndex() == -1) {
            errorLabel.setText("Please Select a Motherboard");
            errorLabel.setVisible(true);
        } else if (storageDropdown.getSelectedIndex() == -1) {
            errorLabel.setText("Please Select a Harddrive");
            errorLabel.setVisible(true);
        } else if (psuDropdown.getSelectedIndex() == -1) {
            errorLabel.setText("Please Select a PSU");
            errorLabel.setVisible(true);
        } else if (caseDropdown.getSelectedIndex() == -1) {
            errorLabel.setText("Please Select a Case");
            errorLabel.setVisible(true);
        }else {
            return true;
        }
        return false;
    }

    public void updateStock() {
        try {
            int updatedStock = cpuItem.getQuantity() - Integer.parseInt(Objects.requireNonNull(cpuQuantity.getSelectedItem()).toString());
            PreparedStatement updateCPUStock = connection.prepareStatement("UPDATE Parts SET quantity = ? WHERE partID = ?");
            updateCPUStock.setInt(1, updatedStock);
            updateCPUStock.setInt(2, cpuItem.getItemID());
            updateCPUStock.executeUpdate();
            updateCPUStock.close();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        try {
            int updatedStock = gpuItem.getQuantity() - Integer.parseInt(Objects.requireNonNull(gpuQuantity.getSelectedItem()).toString());
            PreparedStatement updateGPUStock = connection.prepareStatement("UPDATE Parts SET quantity = ? WHERE partID = ?");
            updateGPUStock.setInt(1, updatedStock);
            updateGPUStock.setInt(2, gpuItem.getItemID());
            updateGPUStock.executeUpdate();
            updateGPUStock.close();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        try {
            int updatedStock = ramItem.getQuantity() - Integer.parseInt(Objects.requireNonNull(ramQuantity.getSelectedItem()).toString());
            PreparedStatement updateRAMStock = connection.prepareStatement("UPDATE Parts SET quantity = ? WHERE partID = ?");
            updateRAMStock.setInt(1, updatedStock);
            updateRAMStock.setInt(2, ramItem.getItemID());
            updateRAMStock.executeUpdate();
            updateRAMStock.close();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        try {
            int updatedStock = storageItem.getQuantity() - Integer.parseInt(Objects.requireNonNull(storageQuantity.getSelectedItem()).toString());
            PreparedStatement updateStroageStock = connection.prepareStatement("UPDATE Parts SET quantity = ? WHERE partID = ?");
            updateStroageStock.setInt(1, updatedStock);
            updateStroageStock.setInt(2, storageItem.getItemID());
            updateStroageStock.executeUpdate();
            updateStroageStock.close();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        try {
            int updatedStock = moboItem.getQuantity() - Integer.parseInt(Objects.requireNonNull(moboQuantity.getSelectedItem()).toString());
            PreparedStatement updateMOBOStock = connection.prepareStatement("UPDATE Parts SET quantity = ? WHERE partID = ?");
            updateMOBOStock.setInt(1, updatedStock);
            updateMOBOStock.setInt(2, moboItem.getItemID());
            updateMOBOStock.close();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        try {
            int updatedStock = psuItem.getQuantity() - Integer.parseInt(Objects.requireNonNull(psuQuantity.getSelectedItem()).toString());
            PreparedStatement updatePSUStock = connection.prepareStatement("UPDATE Parts SET quantity = ? WHERE partID = ?");
            updatePSUStock.setInt(1, updatedStock);
            updatePSUStock.setInt(2, psuItem.getItemID());
            updatePSUStock.executeUpdate();
            updatePSUStock.close();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        try {
            int updatedStock = caseItem.getQuantity() - Integer.parseInt(Objects.requireNonNull(caseQuantity.getSelectedItem()).toString());
            PreparedStatement updateCaseStock = connection.prepareStatement("UPDATE Parts SET quantity = ? WHERE partID = ?");
            updateCaseStock.setInt(1, updatedStock);
            updateCaseStock.setInt(2, caseItem.getItemID());
            updateCaseStock.executeUpdate();
            updateCaseStock.close();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }
}