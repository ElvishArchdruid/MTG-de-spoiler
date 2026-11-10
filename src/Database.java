import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.zip.GZIPInputStream;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Database {
    private static Database obj;
    private Connection connection;

    private Database() {
        try {
            connection = connect();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Database getInstance() {
        if (obj == null) {
            obj = new Database();
        }

        return obj;
    }

    private Connection connect() throws SQLException {
        return DriverManager.getConnection(
            "jdbc:postgresql://localhost:5432/postgres", 
            "postgres",
            "sleep6");
    }

    public void importTaggings() throws SQLException {
        String sql = """
            INSERT INTO card_taggings
            (card_id, tag_id, weight)
            VALUES (?, ?, ?)
        """;

        try (
            GZIPInputStream gzip = new GZIPInputStream(new FileInputStream("data/tags.json.gz"));
            BufferedReader reader = new BufferedReader(new InputStreamReader(gzip));
            PreparedStatement stmnt = connection.prepareStatement(sql);
        ) {
            String line;

            while ((line = reader.readLine()) != null) {
                JsonObject tag = JsonParser.parseString(line).getAsJsonObject();
                stmnt.setObject(2, java.util.UUID.fromString(tag.get("id").getAsString()));

                JsonArray taggings = tag.get("taggings").getAsJsonArray();
                for (JsonElement t : taggings) {
                    stmnt.setObject(1, java.util.UUID.fromString(t.getAsJsonObject().get("oracle_id").getAsString()));
                    stmnt.setObject(3, t.getAsJsonObject().get("weight").getAsString());
                    stmnt.executeUpdate();
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void importCards(){
        String sql = """
            INSERT INTO cards
            (id, name)
            VALUES (?, ?)
            ON CONFLICT (id) DO NOTHING
        """;

        try (
            GZIPInputStream gzip = new GZIPInputStream(new FileInputStream("data/oracle-cards.json.gz"));
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(gzip)
            );
            PreparedStatement stmnt = connection.prepareStatement(sql);
        ) {
            String line;

            while ((line = reader.readLine()) != null) {
                JsonObject card = JsonParser.parseString(line).getAsJsonObject();
                if (card.get("oracle_id")!=null) {
                    stmnt.setObject(1, java.util.UUID.fromString(card.get("oracle_id").getAsString()));
                    stmnt.setObject(2, card.get("name").getAsString());
                    stmnt.executeUpdate();
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void importTags() throws SQLException {
        String sql = """
            INSERT INTO tags
            (id, label)
            VALUES (?, ?)
        """;

        try (
            GZIPInputStream gzip = new GZIPInputStream(new FileInputStream("data/tags.json.gz"));
            BufferedReader reader = new BufferedReader(new InputStreamReader(gzip));
            PreparedStatement stmnt = connection.prepareStatement(sql);
        ) {
            String line;

            while ((line = reader.readLine()) != null) {
                JsonObject tag = JsonParser.parseString(line).getAsJsonObject();
                stmnt.setObject(1, java.util.UUID.fromString(tag.get("id").getAsString()));
                stmnt.setObject(2, tag.get("label").getAsString());

                stmnt.executeUpdate();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

