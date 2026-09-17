package configReader;

import java.io.InputStream;
import java.util.Properties;

public class ConfigReader {

    private static final Properties properties = new Properties();

    static {
        try {
            InputStream input1 = ConfigReader.class.getClassLoader()
                    .getResourceAsStream("env.properties");

            if (input1 != null) {
                properties.load(input1);
            } else {
                throw new IllegalStateException("env.properties was not found.");
            }

            InputStream input2 = ConfigReader.class.getClassLoader()
                    .getResourceAsStream("endpoints.properties");

            if (input2 != null) {
                properties.load(input2);
            }

            String env = System.getProperty("env");
            if (env == null || env.isBlank()) {
                env = "UAT";
            }

            String fileName = "credentials-" + env + ".properties";

            InputStream input3 = ConfigReader.class.getClassLoader()
                    .getResourceAsStream(fileName);

            if (input3 != null) {
                properties.load(input3);
            }

        } catch (Exception e) {
            throw new IllegalStateException(
                    "Failed to load application configuration files.",
                    e
            );
        }
    }

    public static String get(String key) {
        String systemValue = System.getProperty(key);

        if (systemValue != null && !systemValue.isBlank()) {
            return systemValue.trim();
        }

        String propertyValue = properties.getProperty(key);

        if (propertyValue != null && !propertyValue.isBlank()) {
            return propertyValue.trim();
        }

        return null;
    }
}