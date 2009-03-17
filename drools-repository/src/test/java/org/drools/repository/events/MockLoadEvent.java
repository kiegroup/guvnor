package org.drools.repository.events;

import org.drools.repository.AssetItem;

import java.io.InputStream;
import java.io.ByteArrayInputStream;

/**
 * @author Michael Neale
 */
public class MockLoadEvent implements LoadEvent {

            public InputStream loadContent(AssetItem item) {
                return new ByteArrayInputStream("hey".getBytes());
            }
}
