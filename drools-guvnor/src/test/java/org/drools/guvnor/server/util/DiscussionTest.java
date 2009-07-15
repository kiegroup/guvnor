package org.drools.guvnor.server.util;

import org.drools.guvnor.client.rpc.DiscussionRecord;
import junit.framework.TestCase;

/**
 * @author Michael Neale
 */
public class DiscussionTest extends TestCase {

    public void testPersist() throws InterruptedException {
        DiscussionRecord dr = new DiscussionRecord();
        dr.author = "mic";
        dr.note = "hey hey";

        DiscussionRecord dr2 = new DiscussionRecord();
        dr2.author = "chloe";
        dr2.note = "hey hey";

        Thread.sleep(100);

        Discussion d = new Discussion();
        String xml = d.toString(new DiscussionRecord[] {dr, dr2});
        System.err.println(xml);

        DiscussionRecord[] res = d.fromString(xml);
        assertEquals(2, res.length);

        assertEquals("mic", res[0].author);

        assertEquals(dr.timestamp, res[0].timestamp);
        

        assertNotNull(d.fromString(null));
        assertNotNull(d.fromString(""));

    }

}
