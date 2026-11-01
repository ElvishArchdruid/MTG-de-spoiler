import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

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
     * @param cardName name of the target card as a String
     * @throws IOException
     * @throws InterruptedException
     */
    public void SFRequest(String cardName) throws IOException, InterruptedException {
        String url = "https://api.scryfall.com/cards/named?fuzzy=" + cardName;
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

        System.out.println(response.body());
    }
}