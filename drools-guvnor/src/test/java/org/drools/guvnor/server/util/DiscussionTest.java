package org.drools.guvnor.server.util;

import org.drools.guvnor.client.rpc.DiscussionRecord;
import junit.framework.TestCase;

import java.util.List;
import java.util.ArrayList;

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
        List<DiscussionRecord> drs = new ArrayList<DiscussionRecord>();
        drs.add(dr);
        drs.add(dr2);
        String xml = d.toString(drs);
        System.err.println(xml);

        List<DiscussionRecord> res = d.fromString(xml);
        assertEquals(2, res.size());

        assertEquals("mic", res.get(0).author);

        assertEquals(dr.timestamp, res.get(0).timestamp);
        

        assertNotNull(d.fromString(null));
        assertNotNull(d.fromString(""));

        DiscussionRecord dr3 = new DiscussionRecord();
        dr3.author = "sam";
        dr3.note = "yeah !";
        res.add(dr3);

        assertTrue(d.toString(res).indexOf("sam") > -1);
        List<DiscussionRecord> d_ = d.fromString(d.toString(res));
        assertEquals(3, d_.size());
        assertEquals("sam", d_.get(2).author);


    }

}
