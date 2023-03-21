import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class App {
    public static void main(String[] args) throws IOException, ParseException {
        URL url = new URL(
                "https://www.googleapis.com/customsearch/v1?key=AIzaSyCmzKgAMEhEpmMNlCyVaE2VrfXLjnwXlHM&cx=b415c802c15614b87&q=php");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");
        BufferedReader br = new BufferedReader(new InputStreamReader(
                (conn.getInputStream())));

        String output;
        StringBuilder resultJson = new StringBuilder();
        System.out.println("Output from Server .... \n");
        while ((output = br.readLine()) != null) {
            resultJson.append(output);
        }
        conn.disconnect();


        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(resultJson.toString());
        JsonNode itemsNode = rootNode.get("items");

        List<String> result = new ArrayList<>();

        for (JsonNode itemNode : itemsNode) {
            String title = itemNode.get("title").asText();
            result.add(title);
            String link = itemNode.get("link").asText();
            result.add(link);
        }

        for (String res : result) {
            System.out.println(res);
        }

    }
}
