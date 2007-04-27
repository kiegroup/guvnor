package org.drools.brms.server.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.drools.brms.client.common.HTMLFileManagerFields;
import org.drools.repository.AssetItem;
import org.drools.repository.RulesRepository;
import org.drools.repository.RulesRepositoryException;



/**
 * This assists the file manager servlet.
 * @author Fernando Meyer
 */
public class FileManagerUtils {

    
    /**
     * This will return the file and the Asset UUID that it is to be attached to.
     */
    public void attachFile(FormData uploadItem, RulesRepository repo) throws IOException {
        
        String uuid = uploadItem.getUuid();
        InputStream fileData = uploadItem.getFile().getInputStream();
        String fileName = uploadItem.getFile().getName();

        attachFileToAsset( repo, uuid, fileData, fileName );
        uploadItem.getFile().getInputStream().close();
    }

    /**
     * This utility method attaches a file to an asset.
     */
    public void attachFileToAsset(RulesRepository repo,
                                         String uuid,
                                         InputStream fileData,
                                         String fileName) {
        
        AssetItem item = repo.loadAssetByUUID( uuid );
        item.updateBinaryContentAttachment( fileData );
        item.updateBinaryContentAttachmentFileName( fileName );
        item.checkin( "Attached file: " + fileName );
    }

    /** 
     * The get returns files based on UUID.
     * you can do a request like /fileManager?uuid=..... 
     * (need to know the UUID) and it will return it as a file.
     */

    public String loadFileAttachmentByUUID(String uuid,
                                 OutputStream out,
                                 RulesRepository repository) throws IOException {

        AssetItem item = repository.loadAssetByUUID( uuid );

        byte[] data = item.getBinaryContentAsBytes();

        out.write( data );
        out.flush();

        return item.getBinaryContentAttachmentFileName();
    }

    
    /**
     * Get the form data from the inbound request.
     */
    public FormData getFormData(HttpServletRequest request) {
        FileItemFactory factory = new DiskFileItemFactory();
        ServletFileUpload upload = new ServletFileUpload( factory );

        FormData data = new FormData();
        try {
            List items = upload.parseRequest( request );
            Iterator it = items.iterator();
            while ( it.hasNext() ) {
                FileItem item = (FileItem) it.next();
                if ( item.isFormField() && item.getFieldName().equals( HTMLFileManagerFields.FORM_FIELD_UUID ) ) {
                    data.setUuid( item.getString() );
                }
                data.setFile( item );
            }
            return data;
        } catch ( FileUploadException e ) {
            throw new RulesRepositoryException( e );
        }
    }
    

}
