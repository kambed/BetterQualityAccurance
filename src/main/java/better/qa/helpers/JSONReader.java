package better.qa.helpers;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

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
     * @param filePath file path
     * @return JSON string
     */
    public static String getJsonString(String filePath) {
        try {
            InputStream inputStream = JSONReader.class
                    .getClassLoader()
                    .getResourceAsStream(filePath);
            assert inputStream != null;
            return new String(inputStream.readAllBytes());
        } catch (IOException e) {
            throw new RuntimeException("Failed to read JSON file: %s".formatted(filePath), e);
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
