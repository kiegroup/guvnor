package org.drools.resource;

import java.io.ByteArrayOutputStream;

import org.drools.resource.exception.ResourceAccessDeniedException;
import org.drools.resource.exception.ResourceTypeNotSupportedException;
import org.drools.resource.exception.ResourceUrlNotFoundException;

/**
 * Resource handler for interace that all implementations must provide.
 * Subversion is the first implementation of this interface. The interface
 * may need to be changed/extended to accomodate advanced line items, like
 * resource locking/deployment.
 * 
 * @author James Williams (james.williams@redhat.com)
 *
 */
public interface ResourceHandler {

    /**
     * Get the URL contents.
     * 
     * This method should also be smart enough to pull the specific version
     * if specified in the RepositoryBean version attribute.
     * 
     * @param aRepositoryBean
     * @return
     */
    public ByteArrayOutputStream getResourceStream(RepositoryBean aRepositoryBean) throws ResourceUrlNotFoundException,
                                                                                  ResourceTypeNotSupportedException,
                                                                                  ResourceAccessDeniedException;

    /**
     * Get the latest version of the resource from the repository if no version 
     * is specified.
     *  
     * This method should also be smart enough to pull the specific version
     * if specified in the RepositoryBean version attribute.
     * 
     * @param aRepositoryBean
     * @return
     * @throws ResourceTypeNotSupportedException
     */
    public String getResourceURL(RepositoryBean aRepositoryBean) throws ResourceTypeNotSupportedException;

    /**
     * Authenticate the user based on his/her credentials, typically a 
     * username/password combo.
     * 
     * @param url
     * @return
     */
    public boolean authenticate(String url);

    /**
     * Set the credentials for the resource handler calls. Typically called
     * before the user attempts to get any resources.
     * 
     * @param username
     * @param password
     */
    public void setCredentials(String username,
                               String password);

    public void setRepositoryUrl(String url);

    //resource types that need to be supported
//    public static final int DRL_FILE = 1;
//    public static final int RULE     = 2;
//    public static final int FUNCTION = 3;
//    public static final int DSL_FILE = 4;
//    public static final int XLS_FILE = 5;

}
