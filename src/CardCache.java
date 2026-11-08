import java.util.HashMap;
import java.util.Map;

/**
 * CardCache
 * Stores Card objects created by API requests locally to reduce need for further requests
 * Reads from data for card search
 */
public class CardCache {
    private static CardCache obj;
    private Map<String, Card> cache;

    private CardCache() {
        cache = new HashMap<>();
    };

    public static CardCache getInstance() {
        if (obj == null) {
            obj = new CardCache();
        }

        return obj;
    }

    public void addCard(Card card) {
        cache.put(card.getName().toUpperCase(), card);
    }

    public Card getCard(String name) {
        return cache.get(name);
    }

    public boolean containsCard(String name) {
        return cache.containsKey(name);
    }
}
