import java.util.HashMap;
import java.util.Map;


public class CardCache {
    private CardCache obj;
    private Map<String, Card> cache;

    private CardCache() {
        cache = new HashMap<>();
    };

    private CardCache getInstance() {
        if (obj == null) {
            obj = new CardCache();
        }

        return obj;
    }

    public void addCard(Card card) {
        cache.put(card.getName(), card);
    }
}
