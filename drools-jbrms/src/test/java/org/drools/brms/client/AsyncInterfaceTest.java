package org.drools.brms.client;
/*
 * Copyright 2005 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



import java.lang.reflect.Method;

import junit.framework.TestCase;

import org.drools.brms.client.rpc.RepositoryService;
import org.drools.brms.client.rpc.RepositoryServiceAsync;
import org.drools.brms.client.rpc.SecurityService;
import org.drools.brms.client.rpc.SecurityServiceAsync;

import com.google.gwt.user.client.rpc.AsyncCallback;


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