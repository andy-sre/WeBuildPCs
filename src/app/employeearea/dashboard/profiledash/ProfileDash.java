package app.employeearea.dashboard.profiledash;

import app.App;
import app.employeearea.dashboard.EmployeeDash;

import javax.swing.*;
import java.awt.*;

public class ProfileDash extends JFrame{
    private JButton logoutButton;
    private JButton returnButton;
    private JButton createNewEmployeeButton;
    private JButton editMyProfileButton;
    private JPanel panel;

    public ProfileDash(int employeeID, String fname) {
        Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
        this.setTitle("Computer Shop - Welcome");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setBounds(0,0,size.width, size.height);
        this.setVisible(true);
        this.add(panel);
        createNewEmployeeButton.addActionListener(e -> {
            new NewEmployee(employeeID, fname);
            dispose();
        });
        editMyProfileButton.addActionListener(e -> {
            new EditProfile(employeeID, fname);
            dispose();
        });
        returnButton.addActionListener(e -> {
            new EmployeeDash(employeeID, fname);
            dispose();
        });
        logoutButton.addActionListener(e -> {
            new App();
            dispose();
        });
    }
}
