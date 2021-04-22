package app.employeearea.dashboard.profiledash;

import app.App;
import utils.BCrypt;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.Arrays;

public class NewEmployee extends JFrame {
    private JPanel panel;
    private JButton logoutButton;
    private JTextField firstField;
    private JTextField lastField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JPasswordField cpasswordField;
    private JButton submitButton;
    private JButton returnButton;
    private JLabel errorLabel;
    private Connection connection;

    public NewEmployee(int employeeID, String fname) {
        errorLabel.setVisible(false);
        Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
        this.setTitle("Computer Shop - Welcome");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setBounds(0,0,size.width, size.height);
        this.setVisible(true);
        this.add(panel);
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:identifier.sqlite");
        } catch (SQLException connect) {
            System.out.println(connect.getMessage());
        }
        submitButton.addActionListener(e -> {
            if (checkBlank()) {
                if(isValidEmail(emailField.getText())) {
                    if(isValidPassword(new String(passwordField.getPassword()))) {
                        if(Arrays.equals(passwordField.getPassword(), cpasswordField.getPassword())) {
                            try {
                                errorLabel.setVisible(false);
                                PreparedStatement checkEmployee = connection.prepareStatement("SELECT * FROM Employee WHERE email = ?");
                                checkEmployee.setString(1, emailField.getText());
                                ResultSet rs = checkEmployee.executeQuery();
                                if(rs.next()) {
                                    errorLabel.setText("User already exists!");
                                    errorLabel.setVisible(true);
                                } else {
                                    String passString = new String(passwordField.getPassword());
                                    String passHashed = BCrypt.hashpw(passString, BCrypt.gensalt());
                                    rs.close();
                                    checkEmployee.close();
                                    try {
                                        PreparedStatement createEmployee = connection.prepareStatement("INSERT INTO Employee (fname, lname, password, email) VALUES (?, ?, ? ,?)");
                                        createEmployee.setString(1, firstField.getText());
                                        createEmployee.setString(2, lastField.getText());
                                        createEmployee.setString(3, passHashed);
                                        createEmployee.setString(4, emailField.getText());
                                        int rowsAffected = createEmployee.executeUpdate();
                                        if (rowsAffected == 1) {
                                            JOptionPane.showMessageDialog(null, "Account created!  Direct user to log in!");
                                            createEmployee.close();
                                            firstField.setText("");
                                            lastField.setText("");
                                            passwordField.setText("");
                                            cpasswordField.setText("");
                                            emailField.setText("");
                                        }
                                    } catch (SQLException registerUser) {
                                        System.err.println(registerUser.getMessage());
                                    }
                                }
                                rs.close();
                            } catch (SQLException updateEmployeeErr) {
                                System.out.println(updateEmployeeErr.getMessage());
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
        }  else if (passwordField.getPassword().length == 0) {
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
    private static boolean isValidEmail(String email) {
        String regex = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
        return email.matches(regex);
    }
    private static boolean isValidPassword(String password) {
        String regex = "(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}";
        return password.matches(regex);
    }
}
