package app;

import app.userarea.Login;

import javax.swing.*;
import java.awt.*;

public class App extends JFrame {
    private JPanel panel;
    private JButton loginButton;
    private JButton registerButton;

    public App() {
        Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
        this.setTitle("Computer Shop - Welcome Page");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setBounds(0,0,size.width, size.height);
        this.setVisible(true);
        this.add(panel);
        loginButton.addActionListener(e -> {
            new Login();
            dispose();
        });
        registerButton.addActionListener(e -> {
            new Register();
            dispose();
        });
    }
}
