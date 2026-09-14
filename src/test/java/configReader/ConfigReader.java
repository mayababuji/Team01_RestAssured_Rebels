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

            // Load API end points (endpoints.properties) 
            InputStream input2 = ConfigReader.class.getClassLoader()
                    .getResourceAsStream("endpoints.properties");
            if (input2 != null) {
                properties.load(input2);
            }

            // Load environment-specific credentials(has email, password)
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