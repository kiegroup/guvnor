package org.drools.brms.client.rpc;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * This contains data for a package configuration.
 * @author Michael Neale
 *
 */
public class PackageConfigData
    implements
    IsSerializable {

    public String uuid;
    public String header;    
    public MetaData metaData;
    
}
