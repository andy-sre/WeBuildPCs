package App;

import javax.swing.*;
import java.awt.*;

public class Register extends JFrame {
    private JPanel panel;
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField emailField;
    private JTextField eircodeField;
    private JTextField passwordField;
    private JTextField cpasswordField;
    private JButton submitButton;
    private JButton returnButton;
    private JLabel errorLabel;

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
                    if(isValidPassword(passwordField.getText())) {
                        System.out.println("Pass");
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
        } else if (passwordField.getText().isEmpty()) {
            errorLabel.setText("Please enter a password");
            errorLabel.setVisible(true);
        } else if (cpasswordField.getText().isEmpty()) {
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
