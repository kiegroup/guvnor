

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
			String droolsSpringCtxt = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
										"<beans xmlns=\"http://www.springframework.org/schema/beans\""+
										"xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""+
									    "xmlns:context=\"http://www.springframework.org/schema/context\""+
									    "xmlns:drools=\"http://drools.org/schema/drools-spring\""+
									    "xsi:schemaLocation=\"http://www.springframework.org/schema/beans"+
									    " http://www.springframework.org/schema/beans/spring-beans-2.5.xsd"+
									    " http://www.springframework.org/schema/context"+
									    " http://www.springframework.org/schema/context/spring-context-2.5.xsd"+
									    " http://drools.org/schema/drools-spring"+
									    " http://drools.org/schema/drools-spring.xsd\""+ 
									    "default-autowire=\"byName\">" +
									    "<drools:connection id=\"connection1\" type=\"local\" />"+
									    "<drools:execution-node id=\"node1\" connection=\"connection1\" />"+
									    "<drools:kbase id=\"kbase1\" node=\"node1\">"+
										"<drools:resource source=\"classpath:changesets/change-set-1.xml\" type=\"CHANGE_SET\" />"+
									        "<drools:model source=\"classpath:model/person.xsd\" />"+
									    "</drools:kbase>"+
									    "<drools:kbase id=\"kbase2\" node=\"node1\">"+
									        "<drools:resource source=\"classpath:changesets/change-set-2.xml\" type=\"CHANGE_SET\" />"+
									    "</drools:kbase>"+
							            "<drools:ksession id=\"ksession1\" type=\"stateful\"  kbase=\"kbase1\" node=\"node1\"/>"+
									    "<drools:ksession id=\"ksession2\" type=\"stateless\" kbase=\"kbase2\" node=\"node1\"/>"+
									    "</beans>";
										
		
			String springCtxt = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+
								"<!DOCTYPE beans PUBLIC \"-//SPRING//DTD BEAN//EN\" \"http://www.springframework.org/dtd/spring-beans.dtd\">"+
								"<beans>"+
								"<bean id=\"fileEventType\" class=\"com.devdaily.springtest1.bean.FileEventType\">"+
								"<property name=\"eventType\" value=\"10\"/>"+
								"<property name=\"description\" value=\"A sample description here\"/>"+
								"</bean"+
								"</beans>";

			
		validator.setContent(springCtxt);

					
					
			validator.validate();	        
	        assertTrue(validator.validate());
	}
	
	    
}
