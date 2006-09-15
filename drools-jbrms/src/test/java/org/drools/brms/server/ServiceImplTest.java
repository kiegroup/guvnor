package org.drools.brms.server;

import org.drools.repository.RulesRepository;

import junit.framework.TestCase;

public class ServiceImplTest extends TestCase {

  public void testCategory() throws Exception {
        ServiceImpl impl = new ServiceImpl(new RulesRepository(SessionHelper.getSession()));

        String[] originalCats = impl.loadChildCategories( "/" );
        
        Boolean result = impl.createCategory( "/",
                                              "TopLevel1",
                                              "a description" );
        assertTrue( result.booleanValue() );

        result = impl.createCategory( "/",
                                      "TopLevel2",
                                      "a description" );
        assertTrue( result.booleanValue() );
        
        String[] cats = impl.loadChildCategories( "/" );
        assertTrue( cats.length == originalCats.length + 2 );
        
        result = impl.createCategory( "", "Top3", "description" );
        assertTrue(result.booleanValue());
        
        result = impl.createCategory( null, "Top4", "description" );
        assertTrue(result.booleanValue());
        

    }
    
}
