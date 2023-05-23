import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainScreen extends JFrame {
    private JPanel panelMain;

    public MainScreen() {
        setContentPane(panelMain);
        UserControl.control.ui = this;
        createAndShowGui();
    }

    private void createAndShowGui() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setJMenuBar(new MainScreenMenu());
        setSize(400, 350);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setTitle("Bank");
        setIconImage(new ImageIcon("src/assets/money-coins.png").getImage());
        setLocationByPlatform(true);
        setVisible(true);
        new LoginDialog();
    }
}
