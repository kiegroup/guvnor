package org.drools.guvnor.client.packages;
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



import java.util.HashMap;
import java.util.Map;

import org.drools.guvnor.client.common.ErrorPopup;
import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.common.LoadingPopup;
import org.drools.guvnor.client.modeldriven.SuggestionCompletionEngine;
import org.drools.guvnor.client.rpc.RepositoryServiceFactory;
import org.drools.guvnor.client.messages.Constants;

import com.google.gwt.user.client.Command;
import com.google.gwt.core.client.GWT;
import com.gwtext.client.util.Format;
import org.drools.guvnor.client.modeldriven.FactTypeFilter;

/**
 * This utility cache will maintain a cache of suggestion completion engines,
 * as they are somewhat heavy to load.
 * If it needs to be loaded, then it will load, and then call the appropriate action,
 * and keep it in the cache.
 *
 * @author Michael Neale
 *
 */
public class SuggestionCompletionCache {

    private static SuggestionCompletionCache INSTANCE = null;

    Map<String, SuggestionCompletionEngine> cache = new HashMap<String, SuggestionCompletionEngine>();
    private final Constants constants;


    public static SuggestionCompletionCache getInstance() {
        if (INSTANCE == null) INSTANCE = new SuggestionCompletionCache();
        return INSTANCE;
    }

    private SuggestionCompletionCache() {
        constants = GWT.create(Constants.class);
    }

    /**
     * This should only be used for tests !
     */
    SuggestionCompletionCache(Constants cs) {
        constants = cs;
    }


    /**
     * This will do the action, after refreshing the cache if necessary.
     */
    public void doAction(String packageName,
                         Command command) {

        if (!this.cache.containsKey( packageName )) {
            loadPackage(packageName, command);
        } else {
            command.execute();
        }
    }

    public SuggestionCompletionEngine getEngineFromCache(String packageName) {
        SuggestionCompletionEngine eng = (SuggestionCompletionEngine) cache.get( packageName );
        if (eng == null) {
            ErrorPopup.showMessage(constants.UnableToGetContentAssistanceForThisRule());
            return null;
        }
        return eng;
    }


    public void loadPackage(final String packageName, final Command command) {

        LoadingPopup.showMessage(Format.format(constants.InitialisingInfoFor0PleaseWait(), packageName));

        RepositoryServiceFactory.getService().loadSuggestionCompletionEngine( packageName, new GenericCallback<SuggestionCompletionEngine>() {
            public void onSuccess(SuggestionCompletionEngine engine) {
                cache.put( packageName, engine );
                command.execute();
            }

            public void onFailure(Throwable t) {
            	LoadingPopup.close();
                ErrorPopup.showMessage(Format.format(constants.UnableToValidatePackageForSCE(), packageName));
            	command.execute();
            }
        });
    }

    /**
     * Removed the package from the cache, causing it to be loaded the next time.
     */
    public void refreshPackage(String packageName, Command done) {
        if (cache.containsKey( packageName )) {
            cache.remove( packageName );
            loadPackage( packageName, done );
        } else {
            done.execute();
        }
    }

    /**
     * Reloads a package and then applies the given filter.
     * @param packageName the package name.
     * @param filter the filter.
     * @param done the command to be executed after the filter is applied.
     */
    public void applyFactFilter(final String packageName,final FactTypeFilter filter, final Command done){
        this.refreshPackage(packageName, new Command() {
            public void execute() {
                getEngineFromCache(packageName).filterFactTypes(filter);
                done.execute();
            }
        });
    }
}