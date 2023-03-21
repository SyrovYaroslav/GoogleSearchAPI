import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;


@WebServlet(value = "/")
public class GoogleSearchServise extends HttpServlet {
    private TemplateEngine engine;

    @Override
    public void init() {
        engine = new TemplateEngine();

        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
        resolver.setPrefix("/templates/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode("HTML5");
        resolver.setOrder(engine.getTemplateResolvers().size());
        resolver.setCacheable(false);
        engine.addTemplateResolver(resolver);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/html");

        Map<String, String> result = new HashMap<>();
        String hider = "";
        Context simpleContext = new Context(
                req.getLocale(),
                Map.of("results", result, "hider", hider)
        );

        engine.process("index", simpleContext, resp.getWriter());
        resp.getWriter().close();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/html");
        String parametr = req.getParameter("Search");
        String hider = "";
        Map<String, String> result = new HashMap<>();
        if(!parametr.equals("")) {
            hider = "Searched:";
            URL url = new URL(
                    "https://www.googleapis.com/customsearch/v1?key=AIzaSyCmzKgAMEhEpmMNlCyVaE2VrfXLjnwXlHM&cx=b415c802c15614b87&q=" + parametr);
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

            for (JsonNode itemNode : itemsNode) {
                String title = itemNode.get("title").asText();
                String link = itemNode.get("link").asText();
                result.put(link, title);
            }
        }
        Context simpleContext = new Context(
                req.getLocale(),
                Map.of("results", result, "hider", hider)
        );

        engine.process("index", simpleContext, resp.getWriter());
        resp.getWriter().close();

        resp.sendRedirect("/");
    }
}
