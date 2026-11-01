/**
 * Card
 * Class representing a single MtG card
 */
public class Card {
    private String name;
    private String manaCost;
    private String type;
    private String text;

    public Card (String name, String manaCost, String type, String text) {
        this.name = name;
        this.manaCost = manaCost;
        this.type = type;
        this.text = text;
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
