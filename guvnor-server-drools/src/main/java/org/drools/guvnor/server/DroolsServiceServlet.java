/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.guvnor.server;

import javax.inject.Inject;

import org.drools.guvnor.client.rpc.DroolsService;
import org.drools.guvnor.client.rpc.Module;
import org.drools.guvnor.client.rpc.ValidatedResponse;
import org.jboss.solder.core.Veto;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * GWT Entry point for DroolService RPC calls
 */
@Veto
public class DroolsServiceServlet
        extends RemoteServiceServlet
        implements
    DroolsService {

    private static final long serialVersionUID = 6734413565458736245L;

    @Inject
    DroolsService             droolsService;

    @Override
    public ValidatedResponse validateModule(Module data) throws SerializationException {
        return droolsService.validateModule( data );
    }

}
