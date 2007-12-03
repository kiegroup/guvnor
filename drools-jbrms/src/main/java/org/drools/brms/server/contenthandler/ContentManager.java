package org.drools.brms.server.contenthandler;

/*
 * Copyright 2005 Barry Knapp
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
	private static ContentManager INSTANCE;

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
		if (INSTANCE == null) {
			//have to do this annoying thing, as in some cases, letting the classloader
			//load it up means that it will fail as the classes aren't yet available.
			//so have to use this nasty anti-pattern here. Sorry.
			synchronized (ContentManager.class) {
				ContentManager.INSTANCE = new ContentManager(CONTENT_CONFIG_PROPERTIES);
			}
		}
		return INSTANCE;
	}
}
