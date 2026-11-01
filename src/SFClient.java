import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class SFClient {
    private static SFClient obj;
    private HttpClient client;
    String url = "https://api.scryfall.com/cards/named?fuzzy=lightning+bolt";

    private SFClient() {
        client = HttpClient.newHttpClient();
    }

    public static SFClient getInstance() {
        if (obj == null) {
            obj = new SFClient();
        }
        return obj;
    }

    public void SFRequest() throws IOException, InterruptedException {
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