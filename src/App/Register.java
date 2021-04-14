package App;

import App.UserArea.Login;
import utils.BCrypt;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.Arrays;

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

    public Register() {
        errorLabel.setVisible(false);
        Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
        this.setTitle("Computer Shop - Welcome");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setBounds(0,0,size.width, size.height);
        this.setVisible(true);
        this.add(panel);
        returnButton.addActionListener(e -> {
            new App();
            dispose();
        });
        submitButton.addActionListener(e -> {
            errorLabel.setVisible(false);
            if(checkBlank()) {
                if(isValidEmail(emailField.getText())) {
                    if(isValidPassword(new String(passwordField.getPassword()))) {
                        if(Arrays.equals(passwordField.getPassword(), cpasswordField.getPassword())) {
                            try {
                                connection = DriverManager.getConnection("jdbc:sqlite:identifier.sqlite");
                                PreparedStatement checkUserExists = connection.prepareStatement("SELECT * FROM Users WHERE email = ?");
                                checkUserExists.setString(1, emailField.getText());
                                ResultSet rs = checkUserExists.executeQuery();
                                if(rs.next()) {
                                    errorLabel.setText("User already exists.  Try logging in!");
                                    errorLabel.setVisible(true);
                                } else {
                                    String passString = new String(passwordField.getPassword());
                                    String passHashed = BCrypt.hashpw(passString, BCrypt.gensalt());
                                    rs.close();
                                    try {
                                        checkUserExists.close();
                                        PreparedStatement createUser = connection.prepareStatement("INSERT INTO Users (fname, lname, eircode, password, email) VALUES (?,?,?,?,?)");
                                        createUser.setString(1, firstNameField.getText());
                                        createUser.setString(2, lastNameField.getText());
                                        createUser.setString(3, eircodeField.getText());
                                        createUser.setString(4, passHashed);
                                        createUser.setString(5, emailField.getText());
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
                            errorLabel.setVisible(true);
                        }
                    } else {
                        errorLabel.setText("Password must have: 8 Characters, 1 Number, 1 Lowercase Letter, 1 Uppercase Letter, 1 Special Character");
                        errorLabel.setVisible(true);
                    }
                } else {
                    errorLabel.setText("Email is not valid, please try again");
                    errorLabel.setVisible(true);
                }
            }
        });
    }

    public boolean checkBlank() {
        if (firstNameField.getText().isEmpty()) {
            errorLabel.setText("Please enter a first name");
            errorLabel.setVisible(true);
        } else if (lastNameField.getText().isEmpty()) {
            errorLabel.setText("Please enter a last name");
            errorLabel.setVisible(true);
        } else if (emailField.getText().isEmpty()) {
            errorLabel.setText("Please enter a email");
            errorLabel.setVisible(true);
        } else if (eircodeField.getText().isEmpty()) {
            errorLabel.setText("Please enter an Eircode");
            errorLabel.setVisible(true);
        } else if (passwordField.getPassword().length == 0) {
            errorLabel.setText("Please enter a password");
            errorLabel.setVisible(true);
        } else if (cpasswordField.getPassword().length == 0) {
            errorLabel.setText("Please confirm your password");
            errorLabel.setVisible(true);
        } else {
            return true;
        }
        return false;
    }
    static boolean isValidEmail(String email) {
        String regex = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
        return email.matches(regex);
    }
    static boolean isValidPassword(String password) {
        String regex = "(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}";
        return password.matches(regex);
    }
}