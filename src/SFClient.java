import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * SFClient
 * Class that handles requests to Scryfall API.
 * Singleton class so only one instance should ever exist.
 */
public class SFClient {
    private static SFClient obj;
    private HttpClient client;

    private SFClient() {
        client = HttpClient.newHttpClient();
    }

    /**
     * Gets the existing instance of SFClient or creates one if it doesn't exist already.
     * @return SFClient object
     */
    public static SFClient getInstance() {
        if (obj == null) {
            obj = new SFClient();
        }
        return obj;
    }

    /**
     * Creates a request for a single card and sends it to the Scryfall API.
     * @param cardname Name of the target card as a String
     * @return cardJson - JsonObject of the target card
     * @throws IOException
     * @throws InterruptedException
     * @throws BadResponseException 
     */
    private JsonObject cardnameRequest(String cardname) throws IOException, InterruptedException, BadResponseException {
        String url = "https://api.scryfall.com/cards/named?fuzzy="
                        + URLEncoder.encode(cardname, StandardCharsets.UTF_8);

        HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("User-Agent", "MTG-De-Spoiler/1.0")
                        .header("Accept", "application/json")
                        .GET()
                        .build();

        HttpResponse<String> response = client.send(
                        request,
                        HttpResponse.BodyHandlers.ofString()
        );

        if (response.statusCode() == 200) {
            JsonObject cardJson = JsonParser
                            .parseString(response.body())
                            .getAsJsonObject();

            return cardJson;
        }
        
        throw new BadResponseException(response.body());
    }

    public Card getCard(String cardname) {
        cardname = cardname.toUpperCase();

        if (CardCache.getInstance().containsCard(cardname)) {
            System.out.println("Cache hit");
            return CardCache.getInstance().getCard(cardname);
        }
        try {
            JsonObject cardJson = cardnameRequest(cardname);
            Card card = new Card(cardJson.get("name").getAsString().toUpperCase());
            CardCache.getInstance().addCard(card);

            return card;
        } catch (IOException | InterruptedException | BadResponseException e) {
            e.printStackTrace();
            return null;
        }


    }
}