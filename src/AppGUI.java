import javax.swing.*;
import javax.swing.border.Border;

import java.awt.*;

public class AppGUI extends JFrame {
    private JTextField searchbar;
    private JButton searchbutton;
    private JLabel imageLabel;
    
    public AppGUI() {
        setTitle("Card Search");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JPanel searchPanel = new JPanel();

        searchbar = new JTextField(20);
        searchbutton = new JButton("Search");
        searchPanel.add(searchbar);
        searchPanel.add(searchbutton);

        add(searchPanel, BorderLayout.NORTH);

        JPanel cardPanel = new JPanel();

        imageLabel = new JLabel();
        cardPanel.add(imageLabel);

        add(cardPanel, BorderLayout.CENTER);

        searchbutton.addActionListener(e -> {
            String cardname = searchbar.getText();

            Card card = SFClient.getInstance().searchCard(cardname);
             if (card != null) {
                displayCardImage(card.getImageURL());
             }
        });

        setVisible(true);
    }

    private void displayCardImage(String imageurl) {
        try {
            if (imageurl == null || imageurl.isBlank()) {
                System.out.println("Image URL is missing.");
                return;
            }

            java.net.URL url = new java.net.URL(imageurl);

            java.awt.image.BufferedImage img = javax.imageio.ImageIO.read(url);

            if (img == null) {
                System.out.println("Could not read image.");
                return;
            }

            Image scaledImage = img.getScaledInstance(250, 350, Image.SCALE_SMOOTH);

            imageLabel.setIcon(new ImageIcon(scaledImage));
            imageLabel.revalidate();
            imageLabel.repaint();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
