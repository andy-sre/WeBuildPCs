package app;

import app.userarea.Login;
import utils.BCrypt;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.sql.*;
import java.util.Arrays;
import java.util.Locale;

public class Register extends JFrame {
    private JPanel panel;
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField emailField;
    private JTextField eircodeField;
    private JButton submitButton;
    private JButton returnButton;
    private JLabel errorLabel;
    private JPasswordField passwordField;
    private JPasswordField cpasswordField;
    private Connection connection;
    private boolean problem;

    public Register() {
        errorLabel.setVisible(false);
        Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
        this.setTitle("Computer Shop - Welcome");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setBounds(0,0,size.width, size.height);
        this.setVisible(true);
        this.add(panel);
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:identifier.sqlite");
        } catch (SQLException connection) {
            connection.printStackTrace();
        }
        returnButton.addActionListener(e -> {
            new App();
            dispose();
        });
        submitButton.addActionListener(e -> {
            String email = emailField.getText().toLowerCase(Locale.ROOT);
            errorLabel.setVisible(false);
            if(checkBlank()) {
                if(isValidEmail(email)) {
                    if(isValidPassword(new String(passwordField.getPassword()))) {
                        if(Arrays.equals(passwordField.getPassword(), cpasswordField.getPassword())) {
                            try {
                                PreparedStatement checkUserExists = connection.prepareStatement("SELECT * FROM Users WHERE email = ?");
                                checkUserExists.setString(1, email);
                                ResultSet rs = checkUserExists.executeQuery();
                                if(rs.next()) {
                                    errorLabel.setText("User already exists.  Try logging in!");
                                    errorLabel.setVisible(true);
                                } else {
                                    String passString = new String(passwordField.getPassword());
                                    String passHashed = BCrypt.hashpw(passString, BCrypt.gensalt());
                                    rs.close();
                                    checkUserExists.close();
                                    try {
                                        checkUserExists.close();
                                        PreparedStatement createUser = connection.prepareStatement("INSERT INTO Users (fname, lname, eircode, password, email) VALUES (?,?,?,?,?)");
                                        createUser.setString(1, firstNameField.getText());
                                        createUser.setString(2, lastNameField.getText());
                                        createUser.setString(3, eircodeField.getText());
                                        createUser.setString(4, passHashed);
                                        createUser.setString(5, email);
                                        int rowsAffected = createUser.executeUpdate();
                                        if (rowsAffected == 1) {
                                            JOptionPane.showMessageDialog(null, "Account created!  Redirecting you to login now");
                                            createUser.close();
                                            connection.close();
                                            new Login();
                                            dispose();
                                        }
                                    } catch (SQLException registerUser) {
                                        System.err.println(registerUser.getMessage());
                                    }
                                }
                            } catch (SQLException checkUser) {
                                System.err.println(checkUser.getMessage());
                            }

                        } else {
                            errorLabel.setText("Passwords do not match.  Please try again!");
                            passwordField.setBorder(new LineBorder(Color.red,1));
                            cpasswordField.setBorder(new LineBorder(Color.red,1));
                            errorLabel.setVisible(true);
                        }
                    } else {
                        errorLabel.setText("Password must have: 8 Characters, 1 Number, 1 Lowercase Letter, 1 Uppercase Letter, 1 Special Character");
                        passwordField.setBorder(new LineBorder(Color.red,1));
                        errorLabel.setVisible(true);
                    }
                } else {
                    errorLabel.setText("Email is not valid, please try again");
                    emailField.setBorder(new LineBorder(Color.red,1));
                    errorLabel.setVisible(true);
                }
            }
        });
    }

    public boolean checkBlank() {
        if (firstNameField.getText().isEmpty()) {
            firstNameField.setBorder(new LineBorder(Color.red,1));
            problem = true;
        }
        if (lastNameField.getText().isEmpty()) {
            lastNameField.setBorder(new LineBorder(Color.red,1));
            problem = true;
        }
        if (emailField.getText().isEmpty()) {
            emailField.setBorder(new LineBorder(Color.red,1));
            problem = true;
        }
        if (eircodeField.getText().isEmpty()) {
            eircodeField.setBorder(new LineBorder(Color.red,1));
            problem = true;
        }
        if (passwordField.getPassword().length == 0) {
            passwordField.setBorder(new LineBorder(Color.red,1));
            problem = true;

        }
        if (cpasswordField.getPassword().length == 0) {
            cpasswordField.setBorder(new LineBorder(Color.red,1));
            problem = true;

        }
        if (problem == true){
            errorLabel.setText("Fill in the highlighted Fields");
            errorLabel.setVisible(true);
            problem = false;
        } else {
            return true;
        }
        return false;
    }
    private static boolean isValidEmail(String email) {
        String regex = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
        return email.matches(regex);
    }
    private static boolean isValidPassword(String password) {
        String regex = "(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}";
        return password.matches(regex);
    }
}