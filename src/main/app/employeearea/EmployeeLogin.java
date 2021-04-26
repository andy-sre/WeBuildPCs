package main.app.employeearea;

import main.app.App;
import main.app.employeearea.dashboard.EmployeeDash;
import utils.BCrypt;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.sql.*;
import java.util.Locale;

public class EmployeeLogin extends JFrame {
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton returnButton;
    private JButton loginButton;
    private JLabel errorLabel;
    private JPanel panel;

    public EmployeeLogin() {
        errorLabel.setVisible(false);
        Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
        this.setTitle("Computer Shop - Welcome");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setBounds(0, 0, size.width, size.height);
        this.setVisible(true);
        this.add(panel);
        returnButton.addActionListener(e -> {
            new App();
            dispose();
        });
        loginButton.addActionListener(e -> login());
    }

    private static boolean isValidEmail(String email) {
        String regex = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
        return email.matches(regex);
    }

    private void login() {
        errorLabel.setVisible(false);
        if (checkBlank()) {
            if (isValidEmail(emailField.getText())) {
                try {
                    Connection connection = DriverManager.getConnection("jdbc:sqlite:identifier.sqlite");
                    PreparedStatement loginUser = connection.prepareStatement("SELECT employeeID, fname, password FROM Employee WHERE email = ?");
                    loginUser.setString(1, emailField.getText().toLowerCase(Locale.ROOT));
                    ResultSet rs = loginUser.executeQuery();
                    if (rs.next()) {
                        int employeeID = rs.getInt("employeeID");
                        String fname = rs.getString("fname");
                        String passHashed = rs.getString("password");
                        String passString = new String(passwordField.getPassword());
                        if (BCrypt.checkpw(passString, passHashed)) {
                            System.out.println("Test 2");
                            rs.close();
                            loginUser.close();
                            connection.close();
                            JOptionPane.showMessageDialog(null, "Login Successful!  Logging you in now!");
                            new EmployeeDash(employeeID, fname);
                            dispose();
                        }
                    } else {
                        rs.close();
                        loginUser.close();
                        errorLabel.setText("Password or email incorrect, please try again!");
                        errorLabel.setVisible(true);
                    }
                } catch (SQLException loginError) {
                    System.err.println(loginError.getMessage());
                }
            } else {
                errorLabel.setText("Email is not a valid email");
                emailField.setBorder(new LineBorder(Color.red, 1));
                errorLabel.setVisible(true);
            }
        }
    }

    public boolean checkBlank() {
        if (emailField.getText().isEmpty()) {
            errorLabel.setText("Please enter a first name");
            emailField.setBorder(new LineBorder(Color.red, 1));
            errorLabel.setVisible(true);
        } else if (passwordField.getPassword().length == 0) {
            errorLabel.setText("Please enter a password");
            passwordField.setBorder(new LineBorder(Color.red, 1));
            errorLabel.setVisible(true);
        } else {
            return true;
        }
        return false;
    }
}