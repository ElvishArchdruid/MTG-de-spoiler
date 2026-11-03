import com.google.gson.JsonObject;

/**
 * Card
 * Class representing a single MtG card
 */
public class Card {
    private JsonObject json;

    public Card (JsonObject json) {
        this.json = json;
    }

    public String getName() {
        return json.get("name").getAsString();
    }

    public String getManaCost() {
        return json.get("mana_cost").getAsString();
    }

    public String getType() {
        return json.get("type_line").getAsString();
    }

    public String getText() {
        return json.get("oracle_text").getAsString();
    }

    public String getImageURL() {
        return json.getAsJsonObject("image_uris").get("normal").getAsString();
    }

} 
