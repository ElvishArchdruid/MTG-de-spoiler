import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

import com.google.gson.JsonArray;
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
     * Creates a request and sends it to the Scryfall API.
     * @param query Scryfall search syntax as a String
     * @throws IOException
     * @throws InterruptedException
     */
    public void SFRequest(String query) throws IOException, InterruptedException {
        String url = "https://api.scryfall.com/cards/search?q="
                        + URLEncoder.encode(query, StandardCharsets.UTF_8);

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

        JsonObject results = JsonParser
                        .parseString(response.body())
                        .getAsJsonObject();

        System.out.println(response.body());


        JsonArray cards = results.getAsJsonArray("data");

        for (int i = 0; i < cards.size(); i++) {

            JsonObject card = cards.get(i).getAsJsonObject();

            String name = card.get("name").getAsString();
            String mana = card.get("mana_cost").getAsString();
            String type = card.get("type_line").getAsString();

            System.out.println(name);
            System.out.println(mana);
            System.out.println(type);
            System.out.println();
        }
    }
}