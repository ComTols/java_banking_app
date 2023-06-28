package UI;

import javax.swing.*;
import java.net.URL;

/**
 * Starts the Program and initialized the windows
 * @author MaximilianSchüller
 * @version v1.0_stable_alpha
 */
public class Program {
    public static void main(String[] args) {
        new MainScreen();
    }

    public static void setIcon(JFrame frame, String name) {
        URL url = frame.getClass().getResource("/assets/"+name+".png");
        ImageIcon imageIcon = new ImageIcon(url);
        frame.setIconImage(imageIcon.getImage());
    }
    public static void setIcon(JDialog frame, String name) {
        URL url = frame.getClass().getResource("/assets/"+name+".png");
        ImageIcon imageIcon = new ImageIcon(url);
        frame.setIconImage(imageIcon.getImage());
    }
}
