package org.drools.brms.server.builder;

import org.drools.repository.PackageItem;

import junit.framework.TestCase;

public class ContentPackageAssemblerTest extends TestCase {

    
    public void FIXME_testSimplePackage() throws Exception {
        PackageItem pkg = null;
        ContentPackageAssembler asm = new ContentPackageAssembler(pkg);
        assertFalse(asm.hasErrors());
        assertNotNull(asm.getBinaryPackage());
        org.drools.rule.Package bin = asm.getBinaryPackage();
        assertEquals(pkg.getName(), bin.getName());
        assertTrue(bin.isValid());
        
        assertEquals(2, bin.getRules().length);
        
        
        
    }
    
    public void testErrorsInConfig() {
        
    }
    
    public void testErrorsInFunctionAndRuleAsset() {
        
    }
    
    public void testComplexAssets() {
        
    }
    
    
    
}
