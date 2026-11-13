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
import java.sql.ResultSet;
import java.util.zip.GZIPInputStream;

import javax.xml.crypto.Data;

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

                    downloadFile(downloadUrl, "tags");
                    break;
                }
            }
        } else {
            throw new BadResponseException(response.body());
        }
    }

    public void bulkCardDownload() throws IOException, InterruptedException, BadResponseException {
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

                if (dataset.get("type").getAsString().equals("oracle_cards")) {
                    String downloadUrl = dataset.get("jsonl_download_uri").getAsString();

                    downloadFile(downloadUrl, "oracle-cards");
                    break;
                }
            }
        } else {
            throw new BadResponseException(response.body());
        }
    }

    private void downloadFile(String url, String name) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .GET()
                        .build();

        Path path = Paths.get("data/"+name+".json.gz");
        
        Files.createDirectories(path.getParent());

        HttpResponse<Path> response = client.send(
                        request,
                        HttpResponse.BodyHandlers.ofFile(path)
        );

        if (response.statusCode() != 200) {
            throw new IOException("Failed to download");
        }
        
    }
}