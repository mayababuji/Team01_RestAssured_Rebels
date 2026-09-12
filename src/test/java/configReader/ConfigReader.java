/*package configReader;

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
} */


    package configReader;

import java.io.InputStream;
import java.util.Properties;

public class ConfigReader {

    private static Properties properties = new Properties();

    static {
        try {
            // Load env.properties (has base.uri)
            InputStream input1 = ConfigReader.class.getClassLoader()
                    .getResourceAsStream("env.properties");
            properties.load(input1);

            // Load endpoints.properties (has /login, /batches, /programs, etc.)
            InputStream input2 = ConfigReader.class.getClassLoader()
                    .getResourceAsStream("endpoints.properties");
            if (input2 != null) {
                properties.load(input2);
            }

            // Load credentials-UAT.properties (has email, password)
            String env = System.getProperty("env");
            if (env == null) {
                env = "UAT";
            }
            String fileName = "credentials-" + env + ".properties";
            InputStream input3 = ConfigReader.class.getClassLoader()
                    .getResourceAsStream(fileName);
            if (input3 != null) {
                properties.load(input3);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String get(String key) {
        return properties.getProperty(key);
    }
}
