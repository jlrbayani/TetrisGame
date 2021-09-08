package main.persistence;

import main.model.Score;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.stream.Stream;

// Represents a reader that reads from JSON data stored in a file
public class JsonReader {
    private String source;

    // EFFECTS: constructs reader to read from source file
    public JsonReader(String source) {
        this.source = source;
    }

    // EFFECTS: reads high scores from file and returns it;
    // throws IOException if an error occurs reading data from file
    public ArrayList<Score> read() throws IOException {
//        URL url = JsonReader.ge
//        String path = getClass().getResource(source).getPath();
        File jarFile = new File(JsonReader.class.getProtectionDomain().getCodeSource().getLocation().getPath());
        File file = new File(jarFile.getParent(), source);

        String jsonData = readFile(file.getPath());
        JSONObject jsonObject = new JSONObject(jsonData);

        return parseHighScores(jsonObject);
    }

    // EFFECTS: reads source file as string and returns it
    private String readFile(String source) throws IOException {
        StringBuilder contentBuilder = new StringBuilder();

        try (Stream<String> stream = Files.lines(Paths.get(source), StandardCharsets.UTF_8)) {
            stream.forEach(s -> contentBuilder.append(s));
        }

        return contentBuilder.toString();
    }

    // EFFECTS: parses high scores list from JSON object and returns it
    private ArrayList<Score> parseHighScores(JSONObject jsonObject) {
        ArrayList<Score> highScores = new ArrayList<>();

        JSONArray jsonScores = jsonObject.getJSONArray("highScores");
        addScoresToList(highScores, jsonScores);

        return highScores;
    }

    /*
     * MODIFIES: list
     * EFFECTS: Looks through the jsonArray and creates Score instances from it to add onto list
     */
    private void addScoresToList(ArrayList<Score> scores, JSONArray jsonArray) {
        for (Object json: jsonArray) {
            JSONObject nextScore = (JSONObject) json;
            scores.add(getScore(nextScore));
        }
    }

    // EFFECTS: creates a Score object from the passed in jsonObject and returns it
    private Score getScore(JSONObject jsonObject) {
        String name = jsonObject.getString("name");
        int score = jsonObject.getInt("newScore");

        return new Score(name, score);
    }
}
