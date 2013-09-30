package org.guvnor.inbox.backend.server;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Date: 9/30/13
 * Time: 4:06 PM
 * To change this template use File | Settings | File Templates.
 */
public interface InboxBackend {

    List<InboxEntry> loadRecentEdited( String userName );

    List<InboxEntry> loadIncoming( String userName );

    List<InboxEntry> readEntries( String userName,
                                  String boxName );

    void addToIncoming( String itemPath,
                        String note,
                        String userFrom,
                        String userName );
}
