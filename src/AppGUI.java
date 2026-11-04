import javax.swing.*;
import javax.swing.border.Border;

import java.awt.*;

public class AppGUI extends JFrame {
    private JTextField searchbar;
    private JButton searchButton;
    private JLabel resultLabel;
    private JLabel imageLabel;
    
    public AppGUI() {
        setTitle("Card Search");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // NORTH
        JPanel searchPanel = new JPanel();

        searchbar = new JTextField(20);
        searchButton = new JButton("Search");

        searchPanel.add(searchbar);
        searchPanel.add(searchButton);

        add(searchPanel, BorderLayout.NORTH);

        // CENTER
        JPanel cardPanel = new JPanel();
        cardPanel.setLayout(new BorderLayout(10, 10));

        imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageLabel.setVerticalAlignment(SwingConstants.CENTER);

        resultLabel = new JLabel("");
        resultLabel.setHorizontalAlignment(SwingConstants.CENTER);
        resultLabel.setVerticalAlignment(SwingConstants.CENTER);

        cardPanel.add(imageLabel, BorderLayout.CENTER);
        cardPanel.add(resultLabel, BorderLayout.NORTH);

        add(cardPanel, BorderLayout.CENTER);

        searchButton.addActionListener(e -> {
            String cardname = searchbar.getText().trim();
            if (cardname.equals("")) {
                resultLabel.setText("Enter valid card name");
            } else {
                Card card = SFClient.getInstance().searchCard(cardname);
                if (card != null) {
                    resultLabel.setText("Card Found");
                    displayCardImage(card.getImageURL());
                } else {
                    resultLabel.setText("Enter valid card name");
                }
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
