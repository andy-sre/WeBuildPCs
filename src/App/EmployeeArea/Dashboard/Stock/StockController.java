package App.EmployeeArea.Dashboard.Stock;

import App.App;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class StockController extends JFrame {
    private JPanel panel;
    private JButton logoutButton;
    private JButton addItemButton;
    private JButton editItemButton;
    private JButton deleteItemButton;
    private JTable table1;
    private JPanel table;
    private JTextField itemAddField;
    private JTextField priceAddField;
    private JTextField quantityAddField;
    private JButton cancelAddButton;
    private JButton submitAddButton;
    private JPanel addItem;
    private JLabel errorLabel;
    private JPanel editItem;
    private JButton submitEditButton;
    private JButton cancelEditButton;
    private JTextField itemEditField;
    private JTextField priceEditField;
    private JTextField quantityEditField;
    private JButton returnButton;
    private final DefaultTableModel model = new DefaultTableModel(new String[]{"Part ID", "Name", "Price", "Quantity"}, 0);
    private Connection connection;
    private final String db;

    public StockController(int employeeID, String fname, String db) {
        this.db = db;
        errorLabel.setVisible(false);
        addItem.setVisible(false);
        editItem.setVisible(false);
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
        addItemButton.addActionListener(e -> {
            table.setVisible(false);
            addItem.setVisible(true);
            submitAddButton.addActionListener(e1 -> {
                if (itemAddField.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Please enter an item name");
                } else {
                    try {
                        Double.parseDouble(priceAddField.getText());
                    } catch (NumberFormatException error) {
                        JOptionPane.showMessageDialog(null, "Price is not a number");
                        System.out.println(error.getMessage());
                    }
                    try {
                        Integer.parseInt(quantityAddField.getText());
                    } catch (NumberFormatException error) {
                        JOptionPane.showMessageDialog(null, "Quantity is not a number");
                        System.out.println(error.getMessage());
                    }
                    try {
                        PreparedStatement addItemPS = connection.prepareStatement("INSERT INTO Parts (partType, price, quantity, partName) VALUES (? ,? ,? ,?)");
                        addItemPS.setString(1, db);
                        addItemPS.setDouble(2, Double.parseDouble(priceAddField.getText()));
                        addItemPS.setInt(3, Integer.parseInt(quantityAddField.getText()));
                        addItemPS.setString(4, itemAddField.getText());
                        int rowsAffected = addItemPS.executeUpdate();
                        if (rowsAffected == 1) {
                            JOptionPane.showMessageDialog(null, "Item added to the database");
                            addItemPS.close();
                            connection.close();
                            new StockController(employeeID, fname, db);
                            dispose();
                        }
                    } catch (SQLException error) {
                        System.err.println(error.getMessage());
                    }
                }
            });
        });
        cancelAddButton.addActionListener(e -> {
            try {
                connection.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            new StockController(employeeID, fname, db);
            dispose();
        });

        editItemButton.addActionListener(e -> {
            if (table1.getSelectionModel().isSelectionEmpty()) {
                errorLabel.setText("Please select a row first!");
                errorLabel.setVisible(true);
            } else {
                errorLabel.setVisible(false);
                int selectedID = (Integer.parseInt(model.getValueAt(table1.getSelectedRow(), 0).toString()));
                itemEditField.setText(model.getValueAt(table1.getSelectedRow(), 1).toString());
                priceEditField.setText(model.getValueAt(table1.getSelectedRow(), 2).toString());
                quantityEditField.setText(model.getValueAt(table1.getSelectedRow(), 3).toString());
                table.setVisible(false);
                editItem.setVisible(true);
                submitEditButton.addActionListener(e12 -> {
                    if (itemEditField.getText().equals("")) {
                        JOptionPane.showMessageDialog(null, "Please enter an item name");
                    } else {
                        try {
                            Double.parseDouble(priceEditField.getText());
                        } catch (NumberFormatException error) {
                            JOptionPane.showMessageDialog(null, "Price is not a number");
                            System.out.println(error.getMessage());
                        }
                        try {
                            Integer.parseInt(quantityEditField.getText());
                        } catch (NumberFormatException error) {
                            JOptionPane.showMessageDialog(null, "Quantity is not a number");
                            System.out.println(error.getMessage());
                        }
                        try {
                            PreparedStatement updateItem = connection.prepareStatement("UPDATE Parts SET partName = ?, price = ?, quantity = ? WHERE partID = ?");
                            updateItem.setString(1, itemEditField.getText());
                            updateItem.setDouble(2, Double.parseDouble(priceEditField.getText()));
                            updateItem.setInt(3, Integer.parseInt(quantityEditField.getText()));
                            updateItem.setString(4, db);
                            int rowsAffected = updateItem.executeUpdate();
                            if (rowsAffected == 1) {
                                JOptionPane.showMessageDialog(null, "Item has been updated");
                                updateItem.close();
                                connection.close();
                                new StockController(employeeID, fname, db);
                                dispose();
                            }
                        } catch (SQLException error) {
                            System.err.println(error.getMessage());
                        }
                    }
                });
            }
        });
        deleteItemButton.addActionListener(e -> {
            if (table1.getSelectionModel().isSelectionEmpty()) {
                errorLabel.setText("Please select a row first!");
                errorLabel.setVisible(true);
            } else {
                errorLabel.setVisible(false);
                int selectedID = (Integer.parseInt(model.getValueAt(table1.getSelectedRow(), 0).toString()));
                int input = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this item?");
                if(input == 0) {
                    try {
                        PreparedStatement deleteItem = connection.prepareStatement("DELETE FROM Parts WHERE partID = ?");
                        deleteItem.setInt(1, selectedID);
                        int rowsAffected = deleteItem.executeUpdate();
                        if (rowsAffected == 1) {
                            JOptionPane.showMessageDialog(null, "Item has been deleted");
                            deleteItem.close();
                            connection.close();
                            new StockController(employeeID, fname, db);
                            dispose();
                        }
                    } catch (SQLException error) {
                        System.err.println(error.getMessage());
                    }
                }
            }
        });
        cancelEditButton.addActionListener(e -> {
            try {
                connection.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            new StockController(employeeID, fname, db);
            dispose();
        });
        returnButton.addActionListener(e -> {
            try {
                connection.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            new StockDash(employeeID, fname);
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

    public void getTable() {
        model.setRowCount(0);
        try {
            PreparedStatement getParts = connection.prepareStatement("select * from Parts where partType = ?");
            getParts.setString(1, db);
            ResultSet rs = getParts.executeQuery();
            while (rs.next()) {
                int productID = rs.getInt("partID");
                String name = rs.getString("partName");
                double price = rs.getDouble("price");
                int quantity = rs.getInt("quantity");
                model.addRow(new Object[]{productID, name, price, quantity});
            }
            table1.setModel(model);
            rs.close();
            getParts.close();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }
}