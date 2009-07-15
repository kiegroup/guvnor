package org.drools.guvnor.client.rpc;

import java.io.Serializable;
import java.util.Date;
import java.util.Calendar;

/**
 * This is a discussion record item.
 * @author Michael Neale
 */
public class DiscussionRecord implements Serializable {
    public long timestamp = Calendar.getInstance().getTimeInMillis();
    public String note;
    public String author;
}
