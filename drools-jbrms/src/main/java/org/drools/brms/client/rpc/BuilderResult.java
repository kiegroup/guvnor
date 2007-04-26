package org.drools.brms.client.rpc;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Returned by the builder.
 * @author Michael Neale
 */
public class BuilderResult
    implements
    IsSerializable {

    
    public String assetFormat;
    public String assetName;
    public String uuid;
    public String message;
    
}
