package app.userarea;

import app.App;
import app.employeearea.EmployeeLogin;
import app.userarea.dashboard.UserDash;
import utils.BCrypt;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.sql.*;
import java.util.Locale;

public class Login extends JFrame {
    private JPanel panel;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton returnButton;
    private JButton loginButton;
    private JLabel errorLabel;
    private JButton employeeLogin;
    private String passHashed;
    private int userID;
    private String fname;

    public Login() {
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
        employeeLogin.addActionListener(e -> {
            new EmployeeLogin();
            dispose();
        });
    }

    private static boolean isValidEmail(String email) {
        String regex = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
        return email.matches(regex);
    }

    private void login() {
        String email = emailField.getText().toLowerCase(Locale.ROOT);
        errorLabel.setVisible(false);
        if (checkBlank()) {
            if (isValidEmail(email)) {
                try {
                    Connection connection = DriverManager.getConnection("jdbc:sqlite:identifier.sqlite");
                    PreparedStatement loginUser = connection.prepareStatement("SELECT * FROM Users WHERE email = ?");
                    loginUser.setString(1, email);
                    ResultSet rs = loginUser.executeQuery();
                    while (rs.next()) {
                        passHashed = rs.getString("password");
                        userID = rs.getInt("userID");
                        fname = rs.getString("fname");
                    }
                    String passString = new String(passwordField.getPassword());
                    if (BCrypt.checkpw(passString, passHashed)) {
                        rs.close();
                        connection.close();
                        JOptionPane.showMessageDialog(null, "Login Successful!  Logging you in now!");
                        new UserDash(userID, fname);
                        dispose();
                    } else {
                        rs.close();
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
            errorLabel.setText("Please enter your email");
            emailField.setBorder(new LineBorder(Color.red, 1));
            errorLabel.setVisible(true);
        } else if (passwordField.getPassword().length == 0) {
            errorLabel.setText("Please enter your password");
            passwordField.setBorder(new LineBorder(Color.red, 1));
            errorLabel.setVisible(true);
        } else {
            return true;
        }
        return false;
    }
}