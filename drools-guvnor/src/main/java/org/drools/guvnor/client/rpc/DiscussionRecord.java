package org.drools.guvnor.client.rpc;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

/**
 * This is a discussion record item.
 * @author Michael Neale
 */
public class DiscussionRecord implements Serializable {
    public long timestamp = (new Date()).getTime();
    public String note;
    public String author;
}
