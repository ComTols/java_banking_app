package UI;

import javax.swing.*;
import java.net.URL;

/**
 * Starts the Program and initialized the windows
 * @author MaximilianSchüller
 * @version v1.0_stable_alpha
 */
public class Program {
    /**
     * starts the programs and initializes the Mainscreen
     * @param args commandline parameters
     */
    public static void main(String[] args) {
        new MainScreen();
    }

    /**
     * sets the icon of a given JFrame based on the given name of the asset
     * @param frame the Frame of which the icon should be changed
     * @param name the name that decides which icon is suited
     */
    public static void setIcon(JFrame frame, String name) {
        URL url = frame.getClass().getResource("/assets/"+name+".png");
        ImageIcon imageIcon = new ImageIcon(url);
        frame.setIconImage(imageIcon.getImage());
    }
    /**
     * sets the icon of a given JDialog based on the given name of the asset
     * @param frame the Dialog of which the icon should be changed
     * @param name the name that decides which icon is suited
     */
    public static void setIcon(JDialog frame, String name) {
        URL url = frame.getClass().getResource("/assets/"+name+".png");
        ImageIcon imageIcon = new ImageIcon(url);
        frame.setIconImage(imageIcon.getImage());
    }
}
