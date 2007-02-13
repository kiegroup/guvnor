package org.drools.scm.jcr;

import org.drools.repository.AssetItem;
import org.drools.repository.PackageItem;
import org.drools.repository.RepositorySessionUtil;
import org.drools.repository.RulesRepository;
import org.drools.scm.ScmAction;
import org.drools.scm.jcr.JcrActionFactory.AddFile;

import junit.framework.TestCase;

public class JcrActionFactoryTest extends TestCase {

    public void testMapPathNameToPackage() {
        JcrActionFactory fact = new JcrActionFactory(null);
        assertEquals("org.foo.bar", fact.toPackageName("org/foo/bar"));
        assertEquals("foo", fact.toPackageName("foo"));
        assertEquals("FooBar", fact.toPackageName("FooBar"));
        
    
        assertEquals("org/foo/bar", fact.toDirectoryName("org.foo.bar"));
        assertEquals("foo", fact.toDirectoryName("foo"));
    }
    
    public void testAddFiles() throws Exception {
        RulesRepository repo = RepositorySessionUtil.getRepository();
        
        repo.createPackage( "testAddFiles.package", "just for testing" );
        
        JcrActionFactory fact = new JcrActionFactory(repo);
        
        byte[] data = "this is content".getBytes();
        ScmAction action = fact.addFile( "testAddFiles/package", "someFile.drl", data );
        
        fact.execute( action, "some message" );
        
        PackageItem pk = repo.loadPackage( "testAddFiles.package" );
        AssetItem asset = pk.loadAsset( "someFile" );
        
        assertEquals("drl", asset.getFormat());
        assertEquals("this is content", asset.getContent());
        assertEquals("some message", asset.getDescription());
        assertEquals("Draft", asset.getStateDescription());
    }
    
    public void testUpdateFiles() throws Exception {
        RulesRepository repo = RepositorySessionUtil.getRepository();
        PackageItem pkg = repo.loadDefaultPackage();
        AssetItem asset = pkg.addAsset( "testUpdateFilesSVN", "something" );
        
        asset.updateContent( "lala" );
        asset.checkin( "yeah" );
        String oldVersion = asset.getVersionNumber();
        
        JcrActionFactory fact = new JcrActionFactory(repo);
        ScmAction action = fact.updateFile( "default", "testUpdateFilesSVN.drl", "lala".getBytes(), "lala2".getBytes() );

        fact.execute( action, "goo" );
        
        AssetItem asset2 = pkg.loadAsset( "testUpdateFilesSVN" );
        assertFalse(oldVersion.equals( asset2.getVersionNumber() ));
        assertEquals("lala2", asset2.getContent());
        assertEquals("goo", asset2.getCheckinComment());
        
    }
    
    
    
}
