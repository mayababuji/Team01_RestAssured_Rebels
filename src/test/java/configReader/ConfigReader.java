package configReader;

import java.io.InputStream;
import java.util.Properties;

public class ConfigReader {

    private static final Properties properties = new Properties();

    static {
        try (InputStream input = ConfigReader.class.getClassLoader()
                .getResourceAsStream("env.properties")) {

            if (input == null) {
                throw new RuntimeException("env.properties not found in src/test/resources");
            }

            properties.load(input);

        } catch (Exception e) {
            throw new RuntimeException("Failed to load env.properties", e);
        }
    }

    public static String get(String key) {
        return properties.getProperty(key);
    }
}
