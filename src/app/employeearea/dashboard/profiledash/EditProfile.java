package app.employeearea.dashboard.profiledash;

import app.App;
import utils.BCrypt;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class EditProfile extends JFrame {
    private JPanel panel;
    private JButton logoutButton;
    private JButton returnButton;
    private JTextField firstField;
    private JTextField lastField;
    private JTextField emailField;
    private JButton submitButton;
    private JPasswordField passwordField;
    private JLabel errorLabel;
    private Connection connection;
    private String passwordHashed;
    private String password;

    public EditProfile(int employeeID, String fname) {
        errorLabel.setVisible(false);
        Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
        this.setTitle("Computer Shop - Welcome");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setBounds(0, 0, size.width, size.height);
        this.setVisible(true);
        this.add(panel);
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:identifier.sqlite");
        } catch (SQLException connect) {
            System.out.println(connect.getMessage());
        }
        try {
            PreparedStatement getEmployee = connection.prepareStatement("SELECT * FROM Employee WHERE employeeID = " + employeeID);
            ResultSet rs = getEmployee.executeQuery();
            while (rs.next()) {
                firstField.setText(rs.getString("fname"));
                lastField.setText(rs.getString("lname"));
                emailField.setText(rs.getString("email"));
                passwordHashed = rs.getString("password");
            }
            rs.close();
            getEmployee.close();
        } catch (SQLException updateEmployeeErr) {
            System.out.println(updateEmployeeErr.getMessage());
        }
        submitButton.addActionListener(e -> {
            if (checkBlank()) {
                if (isValidEmail(emailField.getText())) {
                    errorLabel.setVisible(false);
                    password = new String(passwordField.getPassword());
                    if (BCrypt.checkpw(password, passwordHashed)) {
                        try {
                            PreparedStatement updateEmployee = connection.prepareStatement("UPDATE Employee SET fname = ?, lname = ?, email = ? WHERE employeeID = " + employeeID);
                            updateEmployee.setString(1, firstField.getText());
                            updateEmployee.setString(2, lastField.getText());
                            updateEmployee.setString(3, emailField.getText());
                            int rowsAffected = updateEmployee.executeUpdate();
                            if (rowsAffected == 1) {
                                JOptionPane.showMessageDialog(null, "Your user details have been updated");
                                passwordField.setText("");
                                updateEmployee.close();
                            }
                        } catch (SQLException userUpdateError) {
                            System.out.println(userUpdateError.getMessage());
                        }
                    } else {
                        errorLabel.setText("Password does not match! Try again!");
                        errorLabel.setVisible(true);
                    }
                } else {
                    errorLabel.setText("Email is not valid, please try again");
                    errorLabel.setVisible(true);
                }
            }
        });
        returnButton.addActionListener(e -> {
            try {
                connection.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            new ProfileDash(employeeID, fname);
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

    private static boolean isValidEmail(String email) {
        String regex = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
        return email.matches(regex);
    }

    public boolean checkBlank() {
        if (firstField.getText().isEmpty()) {
            errorLabel.setText("Please enter a first name");
            errorLabel.setVisible(true);
        } else if (lastField.getText().isEmpty()) {
            errorLabel.setText("Please enter a last name");
            errorLabel.setVisible(true);
        } else if (emailField.getText().isEmpty()) {
            errorLabel.setText("Please enter a email");
            errorLabel.setVisible(true);
        } else if (passwordField.getPassword().length == 0) {
            errorLabel.setText("Please enter a password");
            errorLabel.setVisible(true);
        } else {
            return true;
        }
        return false;
    }
}
