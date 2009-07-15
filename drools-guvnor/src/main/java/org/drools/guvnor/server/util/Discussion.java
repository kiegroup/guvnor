package org.drools.guvnor.server.util;

import org.drools.guvnor.client.rpc.DiscussionRecord;
import com.thoughtworks.xstream.XStream;

/**
 * @author Michael Neale
 */
public class Discussion {

    private XStream xs = getXStream();

    public String toString(DiscussionRecord[] recs) {
        return xs.toXML(recs);
    }

    public DiscussionRecord[] fromString(String data) {
        if (data == null || data.equals("")) return new DiscussionRecord[0];
        return (DiscussionRecord[]) xs.fromXML(data);
    }


    public XStream getXStream() {
        XStream xs = new XStream();
        xs.alias("discussion-record", DiscussionRecord.class);
        xs.alias("discussion", DiscussionRecord[].class);
        return xs;
    }
}
