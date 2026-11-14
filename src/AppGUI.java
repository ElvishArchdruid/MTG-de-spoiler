import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * AppGUI
 * Class for the app GUI.
 */
public class AppGUI extends JFrame {
    private JTextField searchbar;
    private JButton searchButton;
    private JLabel resultLabel;
    private JLabel imageLabel;
    JPanel tagPanel = new JPanel();
    private CurrentCard currentCard;
    
    public AppGUI() {
        setTitle("Card Search");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // NORTH //
        JPanel searchPanel = new JPanel();

        searchbar = new JTextField(20);
        searchButton = new JButton("Search");

        searchPanel.add(searchbar);
        searchPanel.add(searchButton);

        add(searchPanel, BorderLayout.NORTH);

        // CENTER //
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

        // EAST //
        add(tagPanel, BorderLayout.EAST);

        add(cardPanel, BorderLayout.CENTER);

        searchButton.addActionListener(e -> {
            String cardname = searchbar.getText().trim();
            searchCard(cardname);
        });

        setVisible(true);
    }

    /**
     * Updates the image label witha new card image.
     * @param imageurl - Target card's image URL String
     */
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

    /**
     * Validates search and updates current card image accordingly.
     * @param cardname given cardname String
     */
    private void searchCard(String cardname) {
        if (cardname.equals("")) {
                resultLabel.setText("Enter valid card name");
            } else {
                cardname = Database.getInstance().fixName(cardname);
                if (cardname!=null) {
                    resultLabel.setText("Card Found");
                    String url = Database.getInstance().getCardImageURL(cardname);
                    ArrayList<String> tags = Database.getInstance().getTags(cardname);
                    currentCard = new CurrentCard(cardname, url, tags);

                    displayCardImage(url);
                    updateTagPanel();
                } else {
                    resultLabel.setText("Enter valid card name");
                }
            }
    }

    private void updateTagPanel() {
        tagPanel.removeAll();
        tagPanel.setLayout(new GridLayout(0, 3, 10, 10));

        ArrayList<String> tags = currentCard.getTags();

        for (String t : tags) {
            JLabel label = new JLabel(t);
            tagPanel.add(label);
        }

        tagPanel.revalidate();
        tagPanel.repaint();
    }
}


/**
 * CurrentCard
 * Class to represent the card currently being shown by the ui
 */
class CurrentCard {
    private String name;
    private String image_url;
    private ArrayList<String> tags;

    public CurrentCard(String name, String url, ArrayList<String> tags) {
        this.name = name;
        this.image_url = url;
        this.tags = tags;
    }

    public String getName() {
        return name;
    }

    public String getImageURL() {
        return image_url;
    }

    public ArrayList<String> getTags() {
        return tags;
    }
}