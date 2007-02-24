package org.drools.brms.client.rpc;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Simple DTO for snapshot info.
 * @author Michael Neale
 */
public class SnapshotInfo
    implements
    IsSerializable {

    public String name;
    public String comment;
    public String uuid;
    
}
