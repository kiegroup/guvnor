package org.drools.resource;

import java.io.ByteArrayOutputStream;

import org.drools.resource.exception.ResourceAccessDeniedException;
import org.drools.resource.exception.ResourceTypeNotSupportedException;
import org.drools.resource.exception.ResourceUrlNotFoundException;
import org.drools.resource.util.SvnUtil;
import org.tmatesoft.svn.core.SVNException;

/**
 * Resource Handler implementation for subversion. Assumes that there is an HTTP
 * accessible subversion repository with the following structure:
 * 
 * Base URL - Example: http://localhost/svn/resource-handler Subfolders off the
 * base URL: 
 * 	/drls 
 * 	/dsls 
 *  /functions 
 *  /rules 
 *  /spreadsheets
 * 
 * Security can be enabled and if it's enabled, you will need to have
 * appropriate credentials to access any URL resource.
 * 
 * @author James Williams (james.williams@redhat.com)
 * 
 */
public class SvnResourceHandler extends BaseResourceHandler
    implements
    ResourceHandler {

    private String username = "";
    private String password = "";

    /*
     * (non-Javadoc)
     * 
     * @see org.drools.resource.ResourceHandler#authenticate(java.lang.String)
     */
    public boolean authenticate(String url) {
        return SvnUtil.authenticate( username,
                                     password,
                                     url );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.drools.resource.ResourceHandler#setCredentials(java.lang.String,
     *      java.lang.String)
     */
    public void setCredentials(String username,
                               String password) {
        this.username = username;
        this.password = password;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.drools.resource.ResourceHandler#getResourceStream(org.drools.resource.RepositoryBean)
     */
    public ByteArrayOutputStream getResourceStream(RepositoryBean aRepositoryBean) throws ResourceUrlNotFoundException,
                                                                                  ResourceTypeNotSupportedException,
                                                                                  ResourceAccessDeniedException {

        String url = new String();
        ByteArrayOutputStream baos;
        try {
            url = getResourceURL( aRepositoryBean );
            if ( !authenticate( url ) ) {
                throw new ResourceAccessDeniedException( url,
                                                         this.username,
                                                         this.password );
            }

            else {
                baos = SvnUtil.getFileContentsFromSvn( url,
                                                       this.username,
                                                       this.password,
                                                       aRepositoryBean.getVersionInLong() );
            }
        }

        catch ( SVNException e ) {
            e.printStackTrace();
            throw new ResourceUrlNotFoundException( url );
        } catch ( ResourceTypeNotSupportedException exc ) {
            throw exc;
        }
        return baos;
    }

    /*
     * 
     * @see org.drools.resource.ResourceHandler#getResourceURL(org.drools.resource.RepositoryBean)
     */
    public String getResourceURL(RepositoryBean aRepositoryBean) throws ResourceTypeNotSupportedException {

        StringBuffer urlBuf = new StringBuffer( repositoryUrl );
        if ( aRepositoryBean.getResourceType() == ResourceType.DRL_FILE ) {
            urlBuf.append( "/drls/" );
            urlBuf.append( aRepositoryBean.getName() );
            urlBuf.append( ".drl" );
        } else if ( aRepositoryBean.getResourceType() == ResourceType.FUNCTION ) {
            urlBuf.append( "/functions/" );
            urlBuf.append( aRepositoryBean.getName() );
            urlBuf.append( ".function" );
        } else if ( aRepositoryBean.getResourceType() == ResourceType.RULE ) {
            urlBuf.append( "/rules/" );
            urlBuf.append( aRepositoryBean.getName() );
            urlBuf.append( ".rule" );
        } else if ( aRepositoryBean.getResourceType() == ResourceType.DSL_FILE ) {
            urlBuf.append( "/dsls/" );
            urlBuf.append( aRepositoryBean.getName() );
            urlBuf.append( ".dsl" );
        } else if ( aRepositoryBean.getResourceType() == ResourceType.XLS_FILE ) {
            urlBuf.append( "/spreadsheets/" );
            urlBuf.append( aRepositoryBean.getName() );
            urlBuf.append( ".xls" );
        } else {
            throw new ResourceTypeNotSupportedException( urlBuf.toString() );
        }

        return urlBuf.toString();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setRepositoryUrl(String url) {
        this.repositoryUrl = url;
    }

    public String getRepositoryUrl() {
        return this.repositoryUrl;
    }
}
