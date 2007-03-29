package org.drools.brms.client.rpc;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * This is the "payload" of a rule asset.
 * Includes the meta data.
 * 
 * @author Michael Neale
 */
public class RuleAsset
    implements
    IsSerializable {
    
    public MetaData metaData;
    public IsSerializable content;

    public String uuid;
    public boolean archived = false;

}
