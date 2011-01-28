

package org.drools.guvnor.client.ruleeditor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.drools.guvnor.client.common.AssetFormats;
import org.junit.Ignore;
import org.junit.Test;

public class SpringContextValidatorTest {
	
	SpringContextValidator validator = new SpringContextValidator();
	
	@Test
	public void testValidator()  {
			validator.setContent("<html lang=\"es\">"+
					 	"<head>"+
					     "<title>Ejemplo</title>"+
					   "</head>"+
					   "<body>"+
					     "<p>ejemplo</p>"+
					   "</body>"+
					 "</html>");

					
					
			validator.validate();	        
	        assertTrue(validator.validate());
	}
	
	    
}
