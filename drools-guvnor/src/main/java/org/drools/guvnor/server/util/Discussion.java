package org.drools.guvnor.server.util;

import org.drools.guvnor.client.rpc.DiscussionRecord;
import com.thoughtworks.xstream.XStream;

import java.util.List;
import java.util.Collections;
import java.util.ArrayList;

/**
 * @author Michael Neale
 */
public class Discussion {

    private XStream xs = getXStream();

    public String toString(List<DiscussionRecord> recs) {
        return xs.toXML(recs);
    }

    public List<DiscussionRecord> fromString(String data) {
        if (data == null || data.equals("")) return new ArrayList<DiscussionRecord>();
        return (List<DiscussionRecord>) xs.fromXML(data);
    }


    public XStream getXStream() {
        XStream xs = new XStream();
        xs.alias("discussion-record", DiscussionRecord.class);
        xs.alias("discussion", List.class);
        return xs;
    }
}
