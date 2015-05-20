package org.guvnor.asset.mgmt;

import static org.junit.Assert.assertNotNull;

import java.io.InputStream;
import java.net.URL;

import org.junit.Test;

public class PropertiesOnClassPathTest {

    @Test
    public void propertiesHaveBeenPackaged() { 
       String fileName = "/guvnor-asset-mgmt.properties";
      URL resourceUrl = this.getClass().getResource(fileName);
      assertNotNull("'" + fileName + "' not available on classpath!", resourceUrl);
      InputStream resourceStream = this.getClass().getResourceAsStream(fileName);
      assertNotNull("'" + fileName + "' could not be opened!", resourceStream);
    }
}
