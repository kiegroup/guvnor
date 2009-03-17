package org.drools.repository.events;

import org.drools.repository.AssetItem;

import java.io.InputStream;
import java.io.ByteArrayInputStream;

/**
 * @author Michael Neale
 */
public class MockLoadEvent implements LoadEvent {
    boolean loadCalled;

    public InputStream loadContent(AssetItem item) {
                this.loadCalled = true;
                return new ByteArrayInputStream("hey".getBytes());
            }
}
