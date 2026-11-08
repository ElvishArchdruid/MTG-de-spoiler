import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.GZIPInputStream;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

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
     * @return JsonObject of the target card
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

    /**
     * Gets a card object from cache or creates one from an API call and adds it to cache.
     * @param cardname Name of the card as a String
     * @return Card object of target card
     */
    public Card searchCard(String cardname) {
        cardname = cardname.toUpperCase();

        if (CardCache.getInstance().containsCard(cardname)) {
            return CardCache.getInstance().getCard(cardname);
        }
        try {
            JsonObject cardJson = cardnameRequest(cardname);
            Card card = new Card(cardJson);
            CardCache.getInstance().addCard(card);

            return card;
        } catch (IOException | InterruptedException | BadResponseException e) {
            e.printStackTrace();
            return null;
        }


    }

    public void bulkTagDownload() throws IOException, InterruptedException, BadResponseException {
        String url = "https://api.scryfall.com/bulk-data";

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
            JsonObject results = JsonParser
                        .parseString(response.body())
                        .getAsJsonObject();

            JsonArray data = results.getAsJsonArray("data");

            for (JsonElement element : data) {
                JsonObject dataset = element.getAsJsonObject();

                if (dataset.get("type").getAsString().equals("oracle_tags")) {
                    String downloadUrl = dataset.get("jsonl_download_uri").getAsString();

                    downloadFile(downloadUrl);
                    break;
                }
            }
        } else {
            throw new BadResponseException(response.body());
        }
    }

    private void downloadFile(String url) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .GET()
                        .build();

        Path path = Paths.get("data/oracle-tags.json.gz");
        
        Files.createDirectories(path.getParent());

        HttpResponse<Path> response = client.send(
                        request,
                        HttpResponse.BodyHandlers.ofFile(path)
        );

        if (response.statusCode() != 200) {
            throw new IOException("Failed to download");
        }
        
    }

    public void readData() throws JsonSyntaxException, IOException {

        try (
            GZIPInputStream gzip = new GZIPInputStream(new FileInputStream("data/oracle-tags.json.gz"));
            BufferedReader reader = new BufferedReader(new InputStreamReader(gzip));
        ) {
            String line;

            while ((line = reader.readLine()) != null) {
                JsonObject tag = JsonParser.parseString(line).getAsJsonObject();

                System.out.println(tag.get("taggings"));
            }
        }
    }
}