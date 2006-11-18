/*******************************************************************************
 *    JBoss, Home of Professional Open Source
 *    Copyright 2006, JBoss Inc., and individual contributors as indicated
 *    by the @authors tag. See the copyright.txt in the distribution for a
 *    full listing of individual contributors.
 *   
 *    This is free software; you can redistribute it and/or modify it
 *    under the terms of the GNU Lesser General Public License as
 *    published by the Free Software Foundation; either version 2.1 of
 *    the License, or (at your option) any later version.
 *   
 *    This software is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *    Lesser General Public License for more details.
 *   
 *    You should have received a copy of the GNU Lesser General Public
 *    License along with this software; if not, write to the Free
 *    Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 *    02110-1301 USA, or see the FSF site: http://www.fsf.org.
 *******************************************************************************/
package org.drools.resource.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.drools.resource.RepositoryBean;
import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

/**
 * Subversion utility class that uses JavaSVN to get rules and rule lists from
 * Subversion via http. It is assumed that you have a working SVN Repository
 * that is URL enabled. This class does support credentials, so the repository
 * doesn't have to be setup with anonymous access, although that's the easiest
 * to get working.
 * 
 * @author James Williams
 * 
 */
public class SvnUtil {
    private static Logger logger = Logger.getLogger( SvnUtil.class );

    /**
     * Get a DRL file's contents from Subversion.
     * 
     * @param svnUrl -
     *            URL
     * @param svnUsername -
     *            username
     * @param svnPassword -
     *            password
     * @param ruleVersion -
     *            tag or trunk SVN folder, which represents the subdirectory
     *            that contains the DRL.
     * @param drlName -
     *            DRL file name
     * @return
     * @throws SVNException
     */
    public static ByteArrayOutputStream getFileContentsFromSvn(String url,
                                                               String svnUsername,
                                                               String svnPassword,
                                                               long version) throws SVNException {
        String username = svnUsername;
        String password = svnPassword;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        setupLibrary();

        try {
            SVNRepository repository = null;

            /*
             * Creates an instance of SVNRepository to work with the repository.
             * All user's requests to the repository are relative to the
             * repository location used to create this SVNRepository. SVNURL is
             * a wrapper for URL strings that refer to repository locations.
             */
            repository = SVNRepositoryFactoryImpl.create( SVNURL.parseURIEncoded( url ) );

            /*
             * User's authentication information is provided via an
             * ISVNAuthenticationManager instance. SVNWCUtil creates a default
             * usre's authentication manager given user's name and password.
             */
            ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager( username,
                                                                                                  password );

            /*
             * Sets the manager of the user's authentication information that
             * will be used to authenticate the user to the server (if needed)
             * during operations handled by the SVNRepository.
             */
            repository.setAuthenticationManager( authManager );

            /*
             * This Map will be used to get the file properties. Each Map key is
             * a property name and the value associated with the key is the
             * property value.
             */
            Map fileProperties = new HashMap();

            /*
             * Checks up if the specified path really corresponds to a file. If
             * doesn't the program exits. SVNNodeKind is that one who says what
             * is located at a path in a revision. -1 means the latest revision.
             */
            SVNNodeKind nodeKind = repository.checkPath( "",
                                                         -1 );

            if ( nodeKind == SVNNodeKind.NONE ) {
                logger.error( "There is no entry at '" + url + "'." );

            } else if ( nodeKind == SVNNodeKind.DIR ) {
                logger.error( "The entry at '" + url + "' is a directory while a file was expected." );

            }

            /*
             * Gets the contents and properties of the file located at filePath
             * in the repository at the latest revision (which is meant by a
             * negative revision number).
             */
            repository.getFile( "",
                                version,
                                fileProperties,
                                baos );

        } catch ( SVNException e ) {
            e.printStackTrace();
            logger.error( "The SVN file cannot be found. " + url );
            throw e;
        }

        return baos;
    }

    /**
     * Get a list of rule repository resources from subversion.
     * 
     * @param criteriaBean
     * @return
     * @throws SVNException
     */
    public static List getRepositoryBeanList(RepositoryBean criteriaBean,
                                             String username,
                                             String password,
                                             String repositoryUrl) throws RuntimeException {
        String url = repositoryUrl;
        List resourceList = new ArrayList();

        logger.debug( "URL is: " + url );
        /*
         * initializes the library (it must be done before ever using the
         * library itself)
         */
        setupLibrary();
        try {
            SVNRepository repository = SVNRepositoryFactory.create( SVNURL.parseURIEncoded( url ) );
            // we need to narrow down collection list to resources in a folder
            // before
            // this call
            Collection entries = repository.getDir( url,
                                                    -1,
                                                    null,
                                                    (Collection) null );

            Collections.sort( resourceList );
            Iterator i = entries.iterator();
            while ( i.hasNext() ) {
                String svnDetail = ((SVNDirEntry) i.next()).toString();
                // here is where we need to insert criteria logic
            }
        } catch ( SVNException e ) {
            logger.error( "Repository does not exist or is unavailable" );
            throw new RuntimeException( e );
        }

        return resourceList;
    }

    /**
     * Write the DRL file out to a String.
     * 
     * @param baos
     * @return
     */
    public static String writeFile(ByteArrayOutputStream baos) {
        String fileText = null;
        try {
            fileText = baos.toString( "text/html" );
        } catch ( IOException ioe ) {
            logger.error( ioe );
        }

        return fileText;
    }

    public static ByteArrayOutputStream getByteArrayOutputFromFile(File file) throws IOException {
        InputStream is = new FileInputStream( file );

        // Get the size of the file
        long length = file.length();

        // You cannot create an array using a long type.
        // It needs to be an int type.
        // Before converting to an int type, check
        // to ensure that file is not larger than Integer.MAX_VALUE.
        if ( length > Integer.MAX_VALUE ) {
            // File is too large
        }

        // Create the byte array to hold the data
        byte[] bytes = new byte[(int) length];

        // Read in the bytes
        int offset = 0;
        int numRead = 0;
        while ( offset < bytes.length && (numRead = is.read( bytes,
                                                             offset,
                                                             bytes.length - offset )) >= 0 ) {
            offset += numRead;
        }

        // Ensure all the bytes have been read in
        if ( offset < bytes.length ) {
            throw new IOException( "Could not completely read file " + file.getName() );
        }

        // Close the input stream and return bytes
        is.close();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.write( bytes );
        return baos;
    }

    /*
     * Initializes the library to work with a repository either via svn:// (and
     * svn+ssh://) or via http:// (and https://)
     */
    public static void setupLibrary() {
        // for DAV (over http and https)
        DAVRepositoryFactory.setup();

        // for SVN (over svn and svn+ssh)
        SVNRepositoryFactoryImpl.setup();

        // For File        
        FSRepositoryFactory.setup();
    }

    /**
     * authenticate a subversion user for access to a particular URL.
     * 
     * @param criteriaBean
     * @return
     * @throws SVNException
     */
    public static boolean authenticate(String username,
                                       String password,
                                       String repositoryUrl) throws RuntimeException {

        try {
            logger.debug( "Subversion authentication for : username=" + username + ", password=" + password + ", repositoryURL=" + repositoryUrl );

            setupLibrary();
            SVNRepository repository = null;

            /*
             * Creates an instance of SVNRepository to work with the repository.
             * All user's requests to the repository are relative to the
             * repository location used to create this SVNRepository. SVNURL is
             * a wrapper for URL strings that refer to repository locations.
             */
            repository = SVNRepositoryFactory.create( SVNURL.parseURIEncoded( repositoryUrl ) );

            /*
             * User's authentication information is provided via an
             * ISVNAuthenticationManager instance. SVNWCUtil creates a default
             * usre's authentication manager given user's name and password.
             */
            ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager( username,
                                                                                                  password );

            /*
             * Sets the manager of the user's authentication information that
             * will be used to authenticate the user to the server (if needed)
             * during operations handled by the SVNRepository.
             */
            repository.setAuthenticationManager( authManager );
            repository.testConnection();
        } catch ( SVNException e ) {
            logger.error( e.getErrorMessage() );
            return false;
        }

        return true;
    }

}
