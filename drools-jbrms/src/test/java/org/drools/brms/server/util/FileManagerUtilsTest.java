package org.drools.brms.server.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import junit.framework.TestCase;

import org.apache.commons.fileupload.FileItem;
import org.drools.repository.AssetItem;
import org.drools.repository.RulesRepository;

public class FileManagerUtilsTest extends TestCase {
    
    public void testAttachFile() throws Exception {
        
        FileManagerUtils uploadHelper = new FileManagerUtils();
        RulesRepository repo = new RulesRepository(TestEnvironmentSessionHelper.getSession());
        AssetItem item = repo.loadDefaultPackage().addAsset( "testUploadFile", "description" );
        FormData upload = new FormData();
        
        upload.setFile( new MockFile() );
        upload.setUuid( item.getUUID() );
        
        uploadHelper.attachFile( upload, repo );
        
        AssetItem item2 = repo.loadDefaultPackage().loadAsset( "testUploadFile" );
        byte[] data = item2.getBinaryContentAsBytes();
        
        assertNotNull(data);
        assertEquals("foo bar", new String(data));
        assertEquals("foo.bar", item2.getBinaryContentAttachmentFileName());
    }
    
    public void testUploadXmlFile() throws Exception {
        RulesRepository repo = new RulesRepository(TestEnvironmentSessionHelper.getSession());

        repo.createPackage( "testUploadXmlFile", "comment" );
        repo.importRulesRepository( repo.dumpRepositoryXml() );
        assertTrue( repo.containsPackage( "testUploadXmlFile" ) );
    }
    
    public void testGetFilebyUUID() throws Exception {
        FileManagerUtils uploadHelper = new FileManagerUtils();
        RulesRepository repo = new RulesRepository(TestEnvironmentSessionHelper.getSession());
        AssetItem item = repo.loadDefaultPackage().addAsset( "testGetFilebyUUID", "description" );
        FormData upload = new FormData();

        upload.setFile( new MockFile() );
        upload.setUuid( item.getUUID() );
        uploadHelper.attachFile( upload, repo );


        ByteArrayOutputStream out = new ByteArrayOutputStream ();

        String filename = uploadHelper.loadFileAttachmentByUUID(item.getUUID(), out, repo );

        assertNotNull(out.toByteArray());
        assertEquals("foo bar", new String(out.toByteArray()));
        assertEquals("foo.bar", filename);
    }
}

class MockFile implements FileItem {
    
    private static final long serialVersionUID = -9170360363970788385L;
    InputStream stream = new ByteArrayInputStream("foo bar".getBytes());
    
    public void setInputStream(InputStream is) throws IOException {
        stream.close();
        stream = is;
    }
    
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

