import javax.swing.*;
import java.awt.*;

public class AppGUI extends JFrame {
    private JTextField searchbar;
    private JButton searchbutton;
    private JLabel resultLabel;
    private JLabel imageLabel;
    
    public AppGUI() {
        setTitle("Card Search");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout());

        searchbar = new JTextField(20);
        searchbutton = new JButton("Search");
        resultLabel = new JLabel("Enter a card name");
        imageLabel = new JLabel();
        
        add(searchbar);
        add(searchbutton);
        add(resultLabel);
        add(imageLabel);

        searchbutton.addActionListener(e -> {
            String cardname = searchbar.getText();

            Card card = SFClient.getInstance().searchCard(cardname);
             if (card != null) {
                resultLabel.setText("Found: " + card.getName());
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

            java.awt.image.BufferedImage img =
                    javax.imageio.ImageIO.read(url);

            if (img == null) {
                System.out.println("Could not read image.");
                return;
            }

            Image scaledImage = img.getScaledInstance(
                    250, 350, Image.SCALE_SMOOTH
            );

            imageLabel.setIcon(new ImageIcon(scaledImage));
            imageLabel.revalidate();
            imageLabel.repaint();

        } catch (Exception e) {
            System.out.println("Error loading image:");
            e.printStackTrace();
        }
    }
}
