import javax.swing.*;
import java.awt.*;

public class AppGUI extends JFrame {
    private JTextField searchbar;
    private JButton searchbutton;
    
    public AppGUI() {
        setTitle("Card Search");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout());

        searchbar = new JTextField(20);
        searchbutton = new JButton("Search");
        
        add(searchbar);
        add(searchbutton);

        searchbutton.addActionListener(e -> {
            String cardname = searchbar.getText();

            SFClient.getInstance().searchCard(cardname);
        });

        setVisible(true);
    }
}
