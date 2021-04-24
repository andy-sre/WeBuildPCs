package app.userarea.dashboard.orderarea;

import app.App;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class ViewOrder extends JFrame {
    private final int orderID;
    private final int userID;
    private JButton logoutButton;
    private JPanel panel;
    private JLabel casePart;
    private JLabel psuPart;
    private JLabel storagePart;
    private JLabel moboPart;
    private JLabel ramPart;
    private JLabel gpuPart;
    private JLabel cpuPart;
    private JLabel cpuQuantity;
    private JLabel gpuQuantity;
    private JLabel ramQuantity;
    private JLabel moboQuantity;
    private JLabel storageQuantity;
    private JLabel psuQuantity;
    private JLabel caseQuantity;
    private JLabel cpuPrice;
    private JLabel gpuPrice;
    private JLabel ramPrice;
    private JLabel moboPrice;
    private JLabel storagePrice;
    private JLabel psuPrice;
    private JLabel casePrice;
    private JLabel pcPrice;
    private JLabel remainingBal;
    private JLabel orderStatus;
    private JButton returnButton;
    private JButton makePaymentButton;
    private JLabel eircode;
    private JButton refundOrderButton;
    private JLabel paymentStatus;
    private JPanel pcOrder;
    private JPanel rentalOrder;
    private JLabel serverPart;
    private JLabel serverQuantity;
    private JLabel serverPrice;
    private JLabel dueDateLabel;
    private Connection connection;
    private int cpu;
    private int gpu;
    private int psu;
    private int ram;
    private int mobo;
    private int pcCase;
    private int storage;
    private int server;
    private String orderType;

    public ViewOrder(int userID, int orderID, String fname) {
        this.orderID = orderID;
        this.userID = userID;
        Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
        this.setTitle("Computer Shop - Welcome");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setBounds(0, 0, size.width, size.height);
        this.setVisible(true);
        this.add(panel);
        pcOrder.setVisible(false);
        rentalOrder.setVisible(false);
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:identifier.sqlite");
        } catch (SQLException connectionStart) {
            System.out.println(connectionStart.getMessage());
        }
        getOrder();
        getParts();
        getPrice();
        getEircode();
        if (orderType.equals("PC")) {
            pcOrder.setVisible(true);
            rentalOrder.setVisible(false);
        } else {
            rentalOrder.setVisible(true);
            pcOrder.setVisible(false);
        }
        makePaymentButton.setVisible(Double.parseDouble(remainingBal.getText()) != 0 && !orderStatus.getText().equals("Refund Requested"));
        logoutButton.addActionListener(e -> {
            try {
                connection.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            new App();
            dispose();
        });
        makePaymentButton.addActionListener(e -> {
            new Payment(userID, orderID, fname);
            dispose();
        });
        refundOrderButton.addActionListener(e -> {
            try {
                PreparedStatement setRefund = connection.prepareStatement("UPDATE Orders SET orderStatus = ? WHERE orderID = ?");
                setRefund.setString(1, "Refund Requested");
                setRefund.setInt(2, orderID);
                int rowsAffected = setRefund.executeUpdate();
                if (rowsAffected == 1) {
                    setRefund.close();
                    JOptionPane.showMessageDialog(null, "Refund requested on order: " + orderID);
                    connection.close();
                    new ViewOrder(userID, orderID, fname);
                    dispose();
                }
            } catch (SQLException getOrder) {
                System.out.println(getOrder.getMessage());
            }

        });
        returnButton.addActionListener(e -> {
            try {
                connection.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            new OrderViewDash(userID, fname);
            dispose();

        });
    }

    private void getOrder() {
        try {
            PreparedStatement getOrders = connection.prepareStatement("select * FROM Orders where orderID = ?");
            getOrders.setInt(1, orderID);
            ResultSet rs = getOrders.executeQuery();
            while (rs.next()) {
                orderType = rs.getString("orderType");
                if (orderType.equals("PC")) {
                    cpu = rs.getInt("cpuID");
                    cpuQuantity.setText(String.valueOf(rs.getInt("cpuAmount")));
                    gpu = rs.getInt("gpuID");
                    gpuQuantity.setText(String.valueOf(rs.getInt("gpuAmount")));
                    ram = rs.getInt("ramID");
                    ramQuantity.setText(String.valueOf(rs.getInt("ramAmount")));
                    mobo = rs.getInt("motherBoardID");
                    moboQuantity.setText(String.valueOf(rs.getInt("motherBoardAmount")));
                    pcCase = rs.getInt("pcCaseID");
                    caseQuantity.setText(String.valueOf(rs.getInt("pcCaseAmount")));
                    psu = rs.getInt("psuID");
                    psuQuantity.setText(String.valueOf(rs.getInt("psuAmount")));
                    storage = rs.getInt("storageID");
                    storageQuantity.setText(String.valueOf(rs.getInt("storageAmount")));
                    orderStatus.setText(rs.getString("orderStatus"));
                } else {
                    serverQuantity.setText(String.valueOf(rs.getInt("serverAmount")));
                    server = rs.getInt("serverID");
                }

            }
            if (orderStatus.getText().equals("Refund Approved") || paymentStatus.getText().equals("Refund Approved") || orderStatus.getText().equals("Refund Requested")) {
                makePaymentButton.setVisible(false);
                refundOrderButton.setVisible(false);
            }
            rs.close();
            getOrders.close();
        } catch (SQLException getOrder) {
            System.out.println(getOrder.getMessage());
        }
    }

    private void getParts() {
        try {
            PreparedStatement getCPU = connection.prepareStatement("SELECT partName, price FROM Parts where partID = ?");
            getCPU.setInt(1, cpu);
            ResultSet rs = getCPU.executeQuery();
            while (rs.next()) {
                cpuPart.setText(rs.getString("partName"));
                cpuPrice.setText(String.valueOf(rs.getDouble("price")));
            }
            rs.close();
            getCPU.close();
        } catch (SQLException getPart) {
            System.out.println(getPart.getMessage());
        }
        try {
            PreparedStatement getGPU = connection.prepareStatement("SELECT partName, price FROM Parts where partID = ?");
            getGPU.setInt(1, gpu);
            ResultSet rs = getGPU.executeQuery();
            while (rs.next()) {
                gpuPart.setText(rs.getString("partName"));
                gpuPrice.setText(String.valueOf(rs.getDouble("price")));
            }
            rs.close();
            getGPU.close();
        } catch (SQLException getPart) {
            System.out.println(getPart.getMessage());
        }
        try {
            PreparedStatement getRAM = connection.prepareStatement("SELECT partName, price FROM Parts where partID = ?");
            getRAM.setInt(1, ram);
            ResultSet rs = getRAM.executeQuery();
            while (rs.next()) {
                ramPart.setText(rs.getString("partName"));
                ramPrice.setText(String.valueOf(rs.getDouble("price")));
            }
            rs.close();
            getRAM.close();
        } catch (SQLException getPart) {
            System.out.println(getPart.getMessage());
        }
        try {
            PreparedStatement getMobo = connection.prepareStatement("SELECT partName, price FROM Parts where partID = ?");
            getMobo.setInt(1, mobo);
            ResultSet rs = getMobo.executeQuery();
            while (rs.next()) {
                moboPart.setText(rs.getString("partName"));
                moboPrice.setText(String.valueOf(rs.getDouble("price")));
            }
            rs.close();
            getMobo.close();
        } catch (SQLException getPart) {
            System.out.println(getPart.getMessage());
        }
        try {
            PreparedStatement getPSU = connection.prepareStatement("SELECT partName, price FROM Parts where partID = ?");
            getPSU.setInt(1, psu);
            ResultSet rs = getPSU.executeQuery();
            while (rs.next()) {
                psuPart.setText(rs.getString("partName"));
                psuPrice.setText(String.valueOf(rs.getDouble("price")));
            }
            rs.close();
            getPSU.close();
        } catch (SQLException getPart) {
            System.out.println(getPart.getMessage());
        }
        try {
            PreparedStatement getStorage = connection.prepareStatement("SELECT partName, price FROM Parts where partID = ?");
            getStorage.setInt(1, storage);
            ResultSet rs = getStorage.executeQuery();
            while (rs.next()) {
                storagePart.setText(rs.getString("partName"));
                storagePrice.setText(String.valueOf(rs.getDouble("price")));
            }
            rs.close();
            getStorage.close();
        } catch (SQLException getPart) {
            System.out.println(getPart.getMessage());
        }
        try {
            PreparedStatement getCase = connection.prepareStatement("SELECT partName, price FROM Parts where partID = ?");
            getCase.setInt(1, pcCase);
            ResultSet rs = getCase.executeQuery();
            while (rs.next()) {
                casePart.setText(rs.getString("partName"));
                casePrice.setText(String.valueOf(rs.getDouble("price")));
            }
            rs.close();
            getCase.close();
        } catch (SQLException getPart) {
            System.out.println(getPart.getMessage());
        }
        try {
            PreparedStatement getCase = connection.prepareStatement("SELECT partName, price FROM Parts where partID = ?");
            getCase.setInt(1, server);
            ResultSet rs = getCase.executeQuery();
            while (rs.next()) {
                serverPart.setText(rs.getString("partName"));
                serverPrice.setText(String.valueOf(rs.getDouble("price")));
            }
            rs.close();
            getCase.close();
        } catch (SQLException getPart) {
            System.out.println(getPart.getMessage());
        }
    }

    private void getPrice() {
        try {
            PreparedStatement getPrice = connection.prepareStatement("select remainingBal, paymentStatus, price, dueDate FROM Payments where orderID = ?");
            getPrice.setInt(1, orderID);
            ResultSet rs = getPrice.executeQuery();
            while (rs.next()) {
                pcPrice.setText(String.valueOf(rs.getDouble("price")));
                remainingBal.setText(String.valueOf(rs.getDouble("remainingBal")));
                paymentStatus.setText(rs.getString("paymentStatus"));
                dueDateLabel.setText(rs.getString("dueDate"));
            }
            rs.close();
            getPrice.close();
        } catch (SQLException getOrder) {
            System.out.println(getOrder.getMessage());
        }
    }

    private void getEircode() {
        try {
            PreparedStatement getEircode = connection.prepareStatement("select eircode FROM Users where userID = ?");
            getEircode.setInt(1, userID);
            ResultSet rs = getEircode.executeQuery();
            while (rs.next()) {
                eircode.setText(rs.getString("eircode"));
            }
            rs.close();
            getEircode.close();
        } catch (SQLException getOrder) {
            System.out.println(getOrder.getMessage());
        }
    }
}
