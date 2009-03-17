package org.drools.repository.events;

import org.drools.repository.AssetItem;

import java.io.InputStream;

/**
 * This event handler is used to provide alternative asset content.
 * When the asset payload (content) is fetched, it will call this, and it will use its input stream as the source
 * of data rather then the JCR node.
 *
 * Use with care ! (it could slow things down).
 *
 * To install, create an instance of LoadEvent, make it available on the classpath and then set the system property
 * 'loadEventListener' with the value of the full name of the class. 
 *
 * @author Michael Neale
 */
public interface LoadEvent {

    public InputStream loadContent(AssetItem item);
}
