package org.drools.guvnor.client.ruleeditor;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.drools.guvnor.client.rpc.BuilderResult;
import org.w3c.dom.Document;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * The Meaning of this class is to validate the Syntax of the Spring Context XML FILE.
**/
public class SpringContextValidator {
	private String content;
   
	
    public void setContent(String content) {
		this.content = content;
	}

	public String getContent() {
		return content;
	}
	
	public String validate(){				
		// Create a new factory to create parsers that will
	    // be aware of namespaces and will validate or
	    // not according to the flag setting.
		BuilderResult br = new BuilderResult();
	    DocumentBuilderFactory dbf =  DocumentBuilderFactory.newInstance();
	    dbf.setValidating(true);
	    dbf.setNamespaceAware(true);
	    
	    try {
	           DocumentBuilder builder = dbf.newDocumentBuilder();
	           builder.setErrorHandler(new MyErrorHandler());	           
	           InputStream is = new ByteArrayInputStream(content.getBytes("UTF-8"));
	           Document doc = builder.parse(is);
	       } catch (SAXException e) {
	           return e.getMessage();	    	   
	           //System.exit(1);
	       } catch (ParserConfigurationException e) {
	    	   return e.getMessage();
	    	   //System.err.println(e);
	           //System.exit(1);
	       } catch (IOException e) {
	    	   return e.getMessage();
	    	   //System.err.println(e);
	           //System.exit(1);
	       }
	    
	      
	       
		return "";	
	}
	
	
	
	
	class MyErrorHandler implements ErrorHandler {
		  public void warning(SAXParseException e) throws SAXException {
		    show("Warning", e);
		    throw (e);
		  }

		  public void error(SAXParseException e) throws SAXException {
		    show("Error", e);
		    throw (e);
		  }

		  public void fatalError(SAXParseException e) throws SAXException {
		    show("Fatal Error", e);
		    throw (e);
		  }

		  private void show(String type, SAXParseException e) {
		    System.out.println(type + ": " + e.getMessage());
		    System.out.println("Line " + e.getLineNumber() + " Column "
		        + e.getColumnNumber());
		    System.out.println("System ID: " + e.getSystemId());
		  }
		}



	
	
}
