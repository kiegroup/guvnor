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

import com.google.gwt.user.client.rpc.SerializationException;
import org.drools.guvnor.client.rpc.SuggestionCompletionEngineService;
import org.drools.guvnor.server.util.LoggingHelper;
import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.repository.ModuleItem;
import org.drools.repository.RulesRepository;
import org.drools.repository.RulesRepositoryException;
import org.jboss.seam.remoting.annotations.WebRemote;
import org.jboss.seam.security.annotations.LoggedIn;

import javax.inject.Inject;

public class SuggestionCompletionEngineServiceImplementation
        implements SuggestionCompletionEngineService {

    private static final LoggingHelper log = LoggingHelper.getLogger(SuggestionCompletionEngineService.class);

    @Inject
    private RulesRepository rulesRepository;

    @WebRemote
    @LoggedIn
    public SuggestionCompletionEngine loadSuggestionCompletionEngine(String packageName) throws SerializationException {
        //No need to check role based permission here. Package auto completion suggestion should be available to everybody.
        //serviceSecurity.checkSecurityIsPackageReadOnlyWithPackageName( packageName );
        SuggestionCompletionEngine suggestionCompletionEngine = null;
        try {
            ModuleItem packageItem = rulesRepository.loadModule(packageName);
            suggestionCompletionEngine = new SuggestionCompletionEngineLoaderInitializer().loadFor(packageItem);
        } catch (RulesRepositoryException e) {
            log.error("An error occurred loadSuggestionCompletionEngine: " + e.getMessage());
            throw new SerializationException(e.getMessage());
        }
        return suggestionCompletionEngine;
    }
}
