package utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesReader {

    private final Properties properties = new Properties();

    public PropertiesReader(String propertiesFileName) {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(propertiesFileName)) {
            if (input == null) {
                System.out.println("Sorry, unable to find " + propertiesFileName);
                return;
            }
            properties.load(input);
        } catch (IOException ex) {
            throw new RuntimeException("Unable to load properties file: " + propertiesFileName + ": " + ex.getMessage());
        }
    }

    public String getProperty(String key) {
        var value = properties.getProperty(key);
        if (value == null) {
            throw new RuntimeException("Property not found: " + key);
        }
        return value;
    }
}
