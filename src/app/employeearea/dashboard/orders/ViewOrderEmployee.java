package app.employeearea.dashboard.orders;

import app.App;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ViewOrderEmployee extends JFrame {
    private final int orderID;
    private final SimpleDateFormat sFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    private final Date current = new Date();
    private JPanel panel;
    private JButton logoutButton;
    private Connection connection;
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
    private JLabel paymentStatus;
    private JLabel eircodeLabel;
    private JLabel customerName;
    private JPanel OrderView;
    private JLabel overdueLabel;
    private JButton rejectRefundButton;
    private JButton approveRefundButton;
    private JPanel refundArea;
    private JLabel refundLabel;
    private JButton beginBuildButton;
    private JButton finishBuildButton;
    private JPanel rentalOrder;
    private JLabel serverPart;
    private JLabel serverQuantity;
    private JLabel serverPrice;
    private int cpu;
    private int gpu;
    private int psu;
    private int ram;
    private int mobo;
    private int pcCase;
    private int storage;
    private int userID;
    private int server;
    private String orderType;

    public ViewOrderEmployee(int employeeID, int orderID, String fname) {
        this.orderID = orderID;
        Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
        this.setTitle("Computer Shop - Welcome");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setBounds(0, 0, size.width, size.height);
        this.setVisible(true);
        this.add(panel);
        refundArea.setVisible(false);
        OrderView.setVisible(false);
        refundLabel.setVisible(false);
        beginBuildButton.setVisible(false);
        finishBuildButton.setVisible(false);

        try {
            connection = DriverManager.getConnection("jdbc:sqlite:identifier.sqlite");
        } catch (SQLException connectionStart) {
            System.out.println(connectionStart.getMessage());
        }
        getOrder();
        getParts();
        getPrice();
        getUserDetails();
        if (orderType.equals("PC")) {
            OrderView.setVisible(true);
            rentalOrder.setVisible(false);
        } else {
            rentalOrder.setVisible(true);
            OrderView.setVisible(false);
        }
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
            new ViewOrders(employeeID, fname);
            dispose();
        });
        approveRefundButton.addActionListener(e -> {
            approveRefund(employeeID,orderID,fname);
        });

        rejectRefundButton.addActionListener(e -> {
            rejectRefund(employeeID,orderID,fname);
        });

        if (orderStatus.getText().equals("Builder Assigned")) {
            finishBuildButton.setVisible(false);
            beginBuildButton.setVisible(true);
        }
        beginBuildButton.addActionListener(e -> {
            beginBuild(employeeID, orderID, fname);
        });

        if (orderStatus.getText().equals("Build Started")) {
            finishBuildButton.setVisible(true);
            beginBuildButton.setVisible(false);
        }

        finishBuildButton.addActionListener(e -> {
            finishBuild(employeeID,orderID,fname);
        });
    }

    private void finishBuild(int employeeID, int orderID, String fname){
        try {
            PreparedStatement beginBuild = connection.prepareStatement("UPDATE Orders SET orderStatus = ? WHERE orderID = ?");
            beginBuild.setString(1, "Order Complete & Shipped");
            beginBuild.setInt(2, orderID);
            int rowsAffected = beginBuild.executeUpdate();
            if (rowsAffected == 1) {
                beginBuild.close();
                JOptionPane.showMessageDialog(null, "You completed and shipped order: " + orderID);
                connection.close();
                new ViewOrderEmployee(employeeID, orderID, fname);
                dispose();
            }
        } catch (SQLException eBal) {
            System.out.println(eBal.getMessage());
        }
    }

    private void beginBuild(int employeeID, int orderID, String fname){
        try {
            PreparedStatement beginBuild = connection.prepareStatement("UPDATE Orders SET orderStatus = ? WHERE orderID = ?");
            beginBuild.setString(1, "Build Started");
            beginBuild.setInt(2, orderID);
            int rowsAffected = beginBuild.executeUpdate();
            if (rowsAffected == 1) {
                beginBuild.close();
                JOptionPane.showMessageDialog(null, "You began building order: " + orderID);
                connection.close();
                new ViewOrderEmployee(employeeID, orderID, fname);
                dispose();
            }
        } catch (SQLException eBal) {
            System.out.println(eBal.getMessage());
        }
    }

    private void rejectRefund(int employeeID, int orderID, String fname){
        try {
            PreparedStatement declineRefund = connection.prepareStatement("UPDATE Orders SET orderStatus = ? WHERE orderID = ?");
            declineRefund.setString(1, "Refund Declined");
            declineRefund.setInt(2, orderID);
            int rowsAffectedBal = declineRefund.executeUpdate();
            if (rowsAffectedBal == 1) {
                declineRefund.close();
                JOptionPane.showMessageDialog(null, "Refund Declined!");
                connection.close();
                new ViewOrderEmployee(employeeID, orderID, fname);
                dispose();
            }
        } catch (SQLException eBal) {
            System.out.println(eBal.getMessage());
        }
    }

    private void approveRefund(int employeeID, int orderID,String fname){
        try {
            PreparedStatement approveRefund = connection.prepareStatement("UPDATE Orders SET orderStatus = ? WHERE orderID = ?");
            approveRefund.setString(1, "Refund Approved");
            approveRefund.setInt(2, orderID);
            int rowsAffected = approveRefund.executeUpdate();
            if (rowsAffected == 1) {
                System.out.println("Test Approve");
                approveRefund.close();
                try {
                    PreparedStatement setBalance = connection.prepareStatement("UPDATE Payments SET remainingBal = ?, paymentStatus = ? WHERE orderID = ?");
                    setBalance.setDouble(1, 0.0);
                    setBalance.setString(2, "Refund Approved");
                    setBalance.setInt(3, orderID);
                    int rowsAffectedBal = setBalance.executeUpdate();
                    if (rowsAffectedBal == 1) {
                        approveRefund.close();
                        JOptionPane.showMessageDialog(null, "Refund Approved!");
                        connection.close();
                        new ViewOrderEmployee(employeeID, orderID, fname);
                        dispose();
                    }
                } catch (SQLException eBal) {
                    System.out.println(eBal.getMessage());
                }
            }
        } catch (SQLException eStatus) {
            System.out.println(eStatus.getMessage());
        }
    }

    private void getOrder() {
        try {
            PreparedStatement getOrders = connection.prepareStatement("select * FROM Orders where orderID = ?");
            getOrders.setInt(1, orderID);
            ResultSet rs = getOrders.executeQuery();
            while (rs.next()) {
                orderStatus.setText(rs.getString("orderStatus"));
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
                userID = rs.getInt("userID");
            }
            if (orderStatus.getText().equals("Refund Requested")) {
                refundArea.setVisible(true);
                OrderView.setVisible(false);
            } else if (orderStatus.getText().equals("Refund Approved")) {
                refundArea.setVisible(true);
                refundLabel.setVisible(true);
                approveRefundButton.setVisible(false);
                rejectRefundButton.setVisible(false);
                OrderView.setVisible(false);
            } else {
                refundArea.setVisible(false);
                OrderView.setVisible(true);
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
                cpuPrice.setText(rs.getString("price"));
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
                gpuPrice.setText(rs.getString("price"));
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
                ramPrice.setText(rs.getString("price"));
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
                moboPrice.setText(rs.getString("price"));
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
                psuPrice.setText(rs.getString("price"));
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
                storagePrice.setText(rs.getString("price"));
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
                casePrice.setText(rs.getString("price"));
            }
            rs.close();
            getCase.close();
        } catch (SQLException getPart) {
            System.out.println(getPart.getMessage());
        }
        try {
            PreparedStatement getCPU = connection.prepareStatement("SELECT partName, price FROM Parts where partID = ?");
            getCPU.setInt(1, server);
            ResultSet rs = getCPU.executeQuery();
            while (rs.next()) {
                serverPart.setText(rs.getString("partName"));
                serverPrice.setText(rs.getString("price"));
            }
            rs.close();
            getCPU.close();
        } catch (SQLException getPart) {
            System.out.println(getPart.getMessage());
        }
    }

    private void getPrice() {
        String date = "";
        Double balance = 0.0;
        try {
            PreparedStatement getPrice = connection.prepareStatement("select remainingBal, paymentStatus, price, dueDate FROM Payments where orderID = ?");
            getPrice.setInt(1, orderID);
            ResultSet rs = getPrice.executeQuery();
            while (rs.next()) {
                date = rs.getString("dueDate");
                pcPrice.setText(String.valueOf(rs.getDouble("price")));
                balance = rs.getDouble("remainingBal");
                paymentStatus.setText(rs.getString("paymentStatus"));
            }
            remainingBal.setText(String.valueOf(balance));
            try {
                Date dueDate = sFormat.parse(date);
                if (current.compareTo(dueDate) >= 0) {
                    if (balance == 0) {
                        overdueLabel.setText("Payment In Full");
                    } else {
                        overdueLabel.setText("Payment was due on: " + date);
                    }
                } else {
                    if (balance == 0) {
                        overdueLabel.setText("Payment In Full");
                    } else {
                        overdueLabel.setText("Payment is due on: " + date);
                    }
                }
            } catch (ParseException e) {
                System.out.println(e.getMessage());
            }
            rs.close();
            getPrice.close();
        } catch (SQLException getOrder) {
            System.out.println(getOrder.getMessage());
        }
    }

    private void getUserDetails() {
        try {
            PreparedStatement getEircode = connection.prepareStatement("select * FROM Users where userID = ?");
            getEircode.setInt(1, userID);
            ResultSet rs = getEircode.executeQuery();
            while (rs.next()) {
                eircodeLabel.setText(rs.getString("eircode"));
                customerName.setText(rs.getString("fname") + " " + rs.getString("lname"));
            }
            rs.close();
            getEircode.close();
        } catch (SQLException getOrder) {
            System.out.println(getOrder.getMessage());
        }
    }
}
