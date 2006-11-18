package org.drools.resource;

/**
 * This class is the base resource handler that includes attributes/methods that
 * all sub-classes will need. 
 * 
 * @author James Williams (james.williams@redhat.com)
 *
 */
public abstract class BaseResourceHandler {

    //All implementations will be URL based
    protected String repositoryUrl = "";
}
