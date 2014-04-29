package org.guvnor.udc.service;

import org.guvnor.udc.model.InboxPageRequest;
import org.guvnor.udc.model.InboxPageRow;
import org.jboss.errai.bus.server.annotations.Remote;
import org.uberfire.paging.PageResponse;

@Remote
public interface UDCVfsService extends UDCStorageService {

    void addToIncoming(String itemPath, String note, String userFrom, String userName);

    PageResponse<InboxPageRow> loadInbox(InboxPageRequest request);
    
    String[] getUsersVfs();
    
    void addToRecentEdited(String itemPath, String note);
    
    void addToRecentOpened(String itemPath, String note);

}
