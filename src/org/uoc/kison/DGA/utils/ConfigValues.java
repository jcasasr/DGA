package org.uoc.kison.DGA.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author jcasasr
 *
 */
public class ConfigValues {

    InputStream inputStream;
    boolean ExitOnEdgeModificationError = false;

    public void getPropValues() throws IOException {

        try {
            Properties prop = new Properties();
            String propFileName = "config.properties";

            inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);

            if (inputStream != null) {
                prop.load(inputStream);
            } else {
                throw new FileNotFoundException("Property file '" + propFileName + "' not found in the classpath!");
            }
            
            // get the property value and print it out
            ExitOnEdgeModificationError = getBooleanValue(prop.getProperty("ExitOnEdgeModificationError"));
            
        } catch (Exception e) {
            System.out.println("Exception: " + e);
        } finally {
            inputStream.close();
        }
    }
    
    private boolean getBooleanValue(String value) {
        if(value.compareToIgnoreCase("1") == 0) {
            return true;
        } else {
            return false;
        }
    }
    
    public boolean getExitOnEdgeModificationError() {
        return ExitOnEdgeModificationError;
    }
}
