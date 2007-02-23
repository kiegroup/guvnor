package org.drools.brms.server;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import junit.framework.TestCase;

import org.apache.commons.fileupload.FileItem;
import org.drools.repository.AssetItem;
import org.drools.repository.RulesRepository;

public class FileUploadServletTest extends TestCase {

    public void testUploadFile() throws Exception {
        FileUploadServlet serv = new FileUploadServlet();
        FileUploadServlet.FormData upload = new FileUploadServlet.FormData();
        upload.file = new MockFile();
        
        
        
        RulesRepository repo = new RulesRepository(SessionHelper.getSession());
       
        AssetItem item = repo.loadDefaultPackage().addAsset( "testUploadFile", "description" );
        upload.uuid = item.getUUID();
        
        serv.attachFile( upload, repo );
        
        AssetItem item2 = repo.loadDefaultPackage().loadAsset( "testUploadFile" );
        byte[] data = item2.getBinaryContentAsBytes();
        
        assertNotNull(data);
        assertEquals("foo bar", new String(data));
        assertEquals("foo.bar", item2.getBinaryContentAttachmentFileName());
        
    }
    
    static class MockFile implements FileItem {

        InputStream stream = new ByteArrayInputStream("foo bar".getBytes());
        
        public void delete() {

            
        }

        public byte[] get() {

            return null;
        }

        public String getContentType() {

            return null;
        }

        public String getFieldName() {

            return null;
        }

        public InputStream getInputStream() throws IOException {
            return stream;
        }

        public String getName() {
            return "foo.bar";
        }

        public OutputStream getOutputStream() throws IOException {

            return null;
        }

        public long getSize() {
            return 0;
        }

        public String getString() {
            return null;
        }

        public String getString(String arg0) throws UnsupportedEncodingException {
            return null;
        }

        public boolean isFormField() {
            return false;
        }

        public boolean isInMemory() {
            return false;
        }

        public void setFieldName(String arg0) {

            
        }

        public void setFormField(boolean arg0) {

            
        }

        public void write(File arg0) throws Exception {

            
        }
        
    }
    
}
