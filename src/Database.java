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
            initDB();
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

    private void initDB() {
        createTable("""
            CREATE TABLE IF NOT EXISTS cards(  
                id UUID PRIMARY KEY,
                name TEXT,
                cmc NUMERIC NOT NULL DEFAULT 0,
                color_identity TEXT[] NOT NULL DEFAULT '{}',
                edhrec_rank INTEGER,
                image_url TEXT
            )""");

        createTable("""
            CREATE TABLE IF NOT EXISTS tags(
                id uuid PRIMARY KEY,
                label TEXT,
                description TEXT
            )""");

        createTable("""
            CREATE TABLE IF NOT EXISTS card_taggings(
                card_id UUID,
                tag_id UUID,
                weight TEXT,
                FOREIGN KEY (card_id) REFERENCES cards(id),
                FOREIGN KEY (tag_id) REFERENCES tags(id)
            )""");

        importCards();
        importTags();
        importTaggings();
    }

    private void importTaggings() {
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
        } catch (SQLException e) {
            e.printStackTrace();;
        }
    }

    private void importCards(){
        String sql = """
            INSERT INTO cards
            (id, name, cmc, color_identity, edhrec_rank, image_url)
            VALUES (?, ?, ?, ?, ?, ?)
            ON CONFLICT (id) DO NOTHING
        """;

        try (
            GZIPInputStream gzip = new GZIPInputStream(new FileInputStream("data/oracle-cards.json.gz"));
            BufferedReader reader = new BufferedReader(new InputStreamReader(gzip));
            PreparedStatement stmnt = connection.prepareStatement(sql);
        ) {
            String line;

            while ((line = reader.readLine()) != null) {
                JsonObject card = JsonParser.parseString(line).getAsJsonObject();

                stmnt.setObject(1, java.util.UUID.fromString(card.get("id").getAsString()));
                stmnt.setObject(2, card.get("name").getAsString());
                stmnt.setObject(3, card.get("cmc").getAsFloat());
                System.out.println(card.get("color_identity").getAsString());
                stmnt.setObject(4, card.get("color_identity").getAsString());
                stmnt.setObject(5, card.get("edhrec_rank").getAsInt());
                stmnt.setObject(6, card.get("image_uris").getAsJsonObject().get("normal").getAsString());

                stmnt.executeUpdate();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void importTags() {
        String sql = """
            INSERT INTO tags
            (id, label, description)
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

                stmnt.setObject(1, java.util.UUID.fromString(tag.get("id").getAsString()));
                stmnt.setObject(2, tag.get("label").getAsString());
                stmnt.setObject(3, tag.get("description").getAsString());

                stmnt.executeUpdate();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createTable(String sql) {
        try (Statement stmnt = connection.createStatement()) {
            stmnt.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
