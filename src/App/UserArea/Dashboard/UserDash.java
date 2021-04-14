package App.UserArea.Dashboard;

import javax.swing.*;
import java.awt.*;

public class UserDash extends JFrame{
    private JPanel panel;

    public UserDash() {
        Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
        this.setTitle("Computer Shop - Welcome");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setBounds(0,0,size.width, size.height);
        this.setVisible(true);
        this.add(panel);
    }
}
