package org.drools.guvnor.server.contenthandler;

import java.io.IOException;

import org.drools.repository.AssetItem;

/**
 * =(^.^)=
 * 
 * @author Toni Rikkola
 *
 */
public interface ICanHasAttachment {

    public void onAttachmentAdded(AssetItem item) throws IOException;

    public void onAttachmentRemoved(AssetItem item) throws IOException;

}
