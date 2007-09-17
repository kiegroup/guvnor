package org.drools.brms.server.contenthandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * This configures the content handlers based on a props file.
 * @author Barry Knapp
 */
public class ContentManager {

	private static final Logger log = Logger.getLogger( ContentManager.class );
	public static String CONTENT_CONFIG_PROPERTIES = "/contenthandler.properties";
	private static final ContentManager INSTANCE = new ContentManager(CONTENT_CONFIG_PROPERTIES);

    /**
     * This is a map of the contentHandlers to use.
     */
	private final Map<String, ContentHandler> contentHandlers = new HashMap<String, ContentHandler>();



	ContentManager(String configPath) {
		log.debug("Loading content properties");
		Properties props = new Properties();
		try {
			props.load(this.getClass().getResourceAsStream(configPath));
			for (Iterator iter = props.keySet().iterator(); iter.hasNext();) {
				String contentHandler = (String) iter.next();
				String val = props.getProperty(contentHandler);

				contentHandlers.put(contentHandler, loadContentHandlerImplementation( val ));

			}
		} catch (IOException e) {
			log.fatal("UNABLE to load content handlers. Ahem, nothing will actually work. Ignore subsequent errors until this is resolved.", e);
		}
	}

    /**
     * Return the content handlers.
     */
    public Map<String, ContentHandler> getContentHandlers() {

        return contentHandlers;
    }


    private ContentHandler loadContentHandlerImplementation(String val) throws IOException {

		try {
            return (ContentHandler) Thread.currentThread().getContextClassLoader().loadClass( val ).newInstance();

        } catch ( InstantiationException e ) {
            log.error( e );
            return null;
        } catch ( IllegalAccessException e ) {
            log.error( e );
            return null;
        } catch ( ClassNotFoundException e ) {
            log.error( e );
            return null;
        }

	}


	public static ContentManager getInstance() {
		return INSTANCE;
	}
}
