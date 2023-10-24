package better.qa.helpers;

import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class JSONReader {

    public static Map<String, Object> readJSONFile(String filePath) {
        Map<String, Object> resultMap = new HashMap<>();
        try {
            String content = new String(Files.readAllBytes(Paths.get(filePath)));
            JSONObject jsonObject = new JSONObject(content);
            resultMap = toMap(jsonObject);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resultMap;
    }

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
