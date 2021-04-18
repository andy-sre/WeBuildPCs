package App.UserArea.Dashboard;

import App.UserArea.Dashboard.OrderArea.OrderCreate;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UserDash extends JFrame{
    private JPanel panel;
    private JButton logoutButton;
    private JButton newOrderButton;
    private JButton viewOrdersButton;
    private JButton editDetailsButton;
    private JLabel welcomeLabel;

    public UserDash(int userID, String fname) {
        Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
        this.setTitle("Computer Shop - Welcome");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setBounds(0,0,size.width, size.height);
        this.setVisible(true);
        this.add(panel);
        welcomeLabel.setText("Welcome, " + fname + " to your dashboard");
        newOrderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new OrderCreate(userID);
            }
        });
    }
}
