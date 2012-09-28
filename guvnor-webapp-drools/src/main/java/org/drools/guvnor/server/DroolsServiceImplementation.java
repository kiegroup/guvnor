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

import java.util.Iterator;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.drools.guvnor.client.rpc.DroolsService;
import org.drools.guvnor.client.rpc.Module;
import org.drools.guvnor.client.rpc.ValidatedResponse;
import org.drools.guvnor.server.cache.RuleBaseCache;
import org.drools.guvnor.server.repository.Preferred;
import org.drools.guvnor.server.util.BRMSSuggestionCompletionLoader;
import org.drools.guvnor.server.util.LoggingHelper;
import org.drools.repository.RulesRepository;
import org.jboss.seam.remoting.annotations.WebRemote;
import org.jboss.seam.security.annotations.LoggedIn;

import com.google.gwt.user.client.rpc.SerializationException;

@ApplicationScoped
public class DroolsServiceImplementation
    implements
    DroolsService {

    private static final LoggingHelper log = LoggingHelper.getLogger( DroolsService.class );

    private RulesRepository            rulesRepository;

    private ServiceSecurity            serviceSecurity;

    public DroolsServiceImplementation() {
        // Never used, just here because the CDI spec says there has to be an empty constructor
    }

    @Inject
    public DroolsServiceImplementation(@Preferred RulesRepository rulesRepository,
                                       ServiceSecurity serviceSecurity) {
        this.rulesRepository = rulesRepository;
        this.serviceSecurity = serviceSecurity;
    }

    @WebRemote
    @LoggedIn
    public ValidatedResponse validateModule(Module data) throws SerializationException {
        serviceSecurity.checkSecurityIsPackageDeveloperWithPackageUuid( data.getUuid() );
        log.info( "USER:" + getCurrentUserName() + " validateModule module [" + data.getName() + "]" );

        RuleBaseCache.getInstance().remove( data.getUuid() );
        BRMSSuggestionCompletionLoader loader = createBRMSSuggestionCompletionLoader();
        loader.getSuggestionEngine( rulesRepository.loadModule( data.getName() ),
                                    data.getHeader() );

        return validateBRMSSuggestionCompletionLoaderResponse( loader );
    }

    BRMSSuggestionCompletionLoader createBRMSSuggestionCompletionLoader() {
        return new BRMSSuggestionCompletionLoader();
    }

    private ValidatedResponse validateBRMSSuggestionCompletionLoaderResponse(BRMSSuggestionCompletionLoader loader) {
        ValidatedResponse res = new ValidatedResponse();
        if ( loader.hasErrors() ) {
            res.hasErrors = true;
            String err = "";
            for ( Iterator iter = loader.getErrors().iterator(); iter.hasNext(); ) {
                err += (String) iter.next();
                if ( iter.hasNext() ) err += "\n";
            }
            res.errorHeader = "Package validation errors";
            res.errorMessage = err;
        }
        return res;
    }

    private String getCurrentUserName() {
        return rulesRepository.getSession().getUserID();
    }
}
