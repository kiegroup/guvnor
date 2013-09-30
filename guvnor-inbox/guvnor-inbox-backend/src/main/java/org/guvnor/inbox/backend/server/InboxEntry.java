package org.guvnor.inbox.backend.server;

/**
 * And entry in an inbox.
 */
public class InboxEntry {

    private String from;
    private String itemPath;
    private String note;
    private long timestamp;

    public InboxEntry() {
    }

    public InboxEntry( String itemPath,
                       String note,
                       String userFrom ) {
        this.itemPath = itemPath;
        this.note = note;
        this.timestamp = System.currentTimeMillis();
        this.from = userFrom;
    }

    public String getFrom() {
        return from;
    }

    public String getItemPath() {
        return itemPath;
    }

    public String getNote() {
        return note;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
