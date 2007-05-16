package org.drools.brms.client;

import java.lang.reflect.Method;

import org.drools.brms.client.rpc.RepositoryService;
import org.drools.brms.client.rpc.RepositoryServiceAsync;
import org.drools.brms.client.rpc.SecurityService;
import org.drools.brms.client.rpc.SecurityServiceAsync;
import org.drools.brms.server.ServiceImplementation;

import com.google.gwt.user.client.rpc.AsyncCallback;

import junit.framework.TestCase;


/**
 * This will verify that the interfaces are kosher for GWT to use.
 * @author Michael Neale
 */
public class AsyncInterfaceTest extends TestCase {
    
    public void testService() throws Exception {
        
        checkService( RepositoryService.class, RepositoryServiceAsync.class );
        checkService( SecurityService.class, SecurityServiceAsync.class );
        
    }

    private void checkService(Class clsInt, Class clsAsync) throws NoSuchMethodException {
        for ( Method m : clsInt.getMethods()) {
            
            Class[] paramClasses = new Class[m.getParameterTypes().length + 1];
            Class[] sourceParamClasses = m.getParameterTypes();
            for ( int i = 0; i < sourceParamClasses.length; i++ ) {
                paramClasses[i] = sourceParamClasses[i];
            }
            paramClasses[sourceParamClasses.length] = AsyncCallback.class;
            assertNotNull(clsAsync.getMethod( m.getName(), paramClasses ));
        }
    }
    
}
