/**
 * Card
 * Class representing a single MtG card
 */
public class Card {
    private String name;
    private String manaCost;
    private String type;
    private String text;

    public Card (String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getManaCost() {
        return manaCost;
    }

    public String getType() {
        return type;
    }

    public String getText() {
        return text;
    }
} 
