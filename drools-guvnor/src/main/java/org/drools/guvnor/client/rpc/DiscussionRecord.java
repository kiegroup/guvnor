package org.drools.guvnor.client.rpc;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

/**
 * This is a discussion record item.
 * @author Michael Neale
 */
public class DiscussionRecord implements IsSerializable {

    public DiscussionRecord() {}
    public DiscussionRecord(String userName, String note) {
        this.author = userName;
        this.note = note;
    }
    public long timestamp = (new Date()).getTime();
    public String note;
    public String author;
}
