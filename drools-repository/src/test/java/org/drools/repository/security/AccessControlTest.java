package org.drools.repository.security;

import junit.framework.TestCase;

import org.apache.jackrabbit.core.NodeId;
import org.apache.jackrabbit.core.security.AccessManager;
import org.drools.repository.AssetItem;
import org.drools.repository.PackageItem;
import org.drools.repository.RepositorySessionUtil;
import org.drools.repository.RulesRepository;
import org.jboss.security.identity.plugins.IdentityFactory;

public class AccessControlTest extends TestCase {

   
    public void testSecurityACL() throws Exception {

        DroolsRepositoryACLManager droolssercurity = new DroolsRepositoryACLManager( IdentityFactory.createIdentity( "group1" ) );

        RulesRepository repo = RepositorySessionUtil.getRepository();

        repo.loadDefaultPackage().addAsset( "testsecurityASSET1",
                                            "X" );
        repo.loadDefaultPackage().addAsset( "testsecurityASSET2",
                                            "X" );
        AssetItem item = RepositorySessionUtil.getRepository().loadDefaultPackage().loadAsset( "testsecurityASSET1" );

        droolssercurity.setPermission( item.getUUID(),
                                       AccessManager.READ );

        assertTrue( droolssercurity.checkPermission( NodeId.valueOf( item.getUUID() ),
                                                     AccessManager.READ ) );
        assertFalse( droolssercurity.checkPermission( NodeId.valueOf( item.getUUID() ),
                                                      AccessManager.WRITE ) );
        assertFalse( droolssercurity.checkPermission( NodeId.valueOf( item.getUUID() ),
                                                      AccessManager.REMOVE ) );

        item = RepositorySessionUtil.getRepository().loadDefaultPackage().loadAsset( "testsecurityASSET2" );
        droolssercurity.setPermission( item.getUUID(),
                                       AccessManager.READ + AccessManager.WRITE );

        assertTrue( droolssercurity.checkPermission( NodeId.valueOf( item.getUUID() ),
                                                     AccessManager.READ + AccessManager.WRITE ) );
        assertTrue( droolssercurity.checkPermission( NodeId.valueOf( item.getUUID() ),
                                                     AccessManager.READ ) );
        assertTrue( droolssercurity.checkPermission( NodeId.valueOf( item.getUUID() ),
                                                     AccessManager.WRITE ) );
        assertFalse( droolssercurity.checkPermission( NodeId.valueOf( item.getUUID() ),
                                                      AccessManager.REMOVE ) );

    }

    public void testSecurityACLMultiUsers() throws Exception {

        DroolsRepositoryACLManager droolssercurity1 = new DroolsRepositoryACLManager( IdentityFactory.createIdentity( "group1" ) );
        DroolsRepositoryACLManager droolssercurity2 = new DroolsRepositoryACLManager( IdentityFactory.createIdentity( "group2" ) );

        RulesRepository repo = RepositorySessionUtil.getRepository();

        repo.loadDefaultPackage().addAsset( "testsecurityASSET3",
                                            "X" );

        AssetItem item = RepositorySessionUtil.getRepository().loadDefaultPackage().loadAsset( "testsecurityASSET3" );

        droolssercurity2.setPermission( item.getUUID(),
                                        AccessManager.READ );

        assertFalse( droolssercurity1.checkPermission( NodeId.valueOf( item.getUUID() ),
                                                       AccessManager.READ ) );
        assertFalse( droolssercurity1.checkPermission( NodeId.valueOf( item.getUUID() ),
                                                       AccessManager.WRITE ) );
        assertFalse( droolssercurity1.checkPermission( NodeId.valueOf( item.getUUID() ),
                                                       AccessManager.REMOVE ) );

        assertTrue( droolssercurity2.checkPermission( NodeId.valueOf( item.getUUID() ),
                                                      AccessManager.READ ) );
        assertFalse( droolssercurity2.checkPermission( NodeId.valueOf( item.getUUID() ),
                                                       AccessManager.WRITE ) );
        assertFalse( droolssercurity2.checkPermission( NodeId.valueOf( item.getUUID() ),
                                                       AccessManager.REMOVE ) );

    }
    
    
    public void FIXME_testSecurityACLDeepPermission() throws Exception {

        DroolsRepositoryACLManager droolssercurity1 = new DroolsRepositoryACLManager( IdentityFactory.createIdentity( "group1" ) );
        DroolsRepositoryACLManager droolssercurity2 = new DroolsRepositoryACLManager( IdentityFactory.createIdentity( "group2" ) );

        RulesRepository repo = RepositorySessionUtil.getRepository();
        
        
        PackageItem packageitem = repo.createPackage( "testPackageSecurity", "lalalala" );
        
        AssetItem item = packageitem.addAsset( "testsecurityASSET3",
        "X" );


        droolssercurity2.setPermission( item.getUUID(),
                                        AccessManager.WRITE );
        

        assertFalse( droolssercurity1.checkPermission( NodeId.valueOf( item.getUUID() ),
                                                       AccessManager.READ ) );
        assertTrue( droolssercurity1.checkPermission( NodeId.valueOf( item.getUUID() ),
                                                       AccessManager.WRITE ) );
        assertFalse( droolssercurity1.checkPermission( NodeId.valueOf( item.getUUID() ),
                                                       AccessManager.REMOVE ) );
    }

}
