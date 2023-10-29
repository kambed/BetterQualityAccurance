package better.qa.helpers;

import better.qa.exception.ReadJsonFileException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * JSON reader.
 */
public class JSONReader {

    /**
     * Hidden constructor.
     */
    private JSONReader() {
    }

    /**
     * Get JSON string.
     *
     * @param filePath file path
     * @return JSON string
     */
    public static String getJsonString(String filePath) {
        try (
                InputStream inputStream = Thread
                        .currentThread()
                        .getContextClassLoader()
                        .getResourceAsStream(filePath);
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(
                                Objects.requireNonNull(inputStream),
                                StandardCharsets.UTF_8
                        )
                )
        ) {
            return reader.lines().collect(Collectors.joining(System.lineSeparator()));
        } catch (IOException e) {
            throw new ReadJsonFileException("Failed to read JSON file: %s".formatted(filePath), e);
        }
    }

    /**
     * Read JSON file.
     *
     * @param filePath file path
     * @return map
     */
    public static Map<String, Object> readJSONFile(String filePath) {
        String content = getJsonString(filePath);
        JSONObject jsonObject = new JSONObject(content);
        return toMap(jsonObject);
    }

    /**
     * Convert JSON object to map.
     *
     * @param object JSON object
     * @return map
     */
    private static Map<String, Object> toMap(JSONObject object) {
        Map<String, Object> map = new HashMap<>();
        object.keySet().forEach(key -> {
            Object value = object.get(key);
            if (value instanceof JSONObject jsonObject) {
                value = toMap(jsonObject);
            }
            map.put(key, value);
        });

        return map;
    }
}
