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

package org.drools.guvnor.client.moduleeditor.drools;



import java.util.HashMap;
import java.util.Map;

import org.drools.guvnor.client.common.ErrorPopup;
import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.common.LoadingPopup;
import org.drools.guvnor.client.explorer.RefreshModuleDataModelEvent;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.rpc.RepositoryServiceFactory;
import org.drools.ide.common.client.modeldriven.FactTypeFilter;
import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Command;

/**
 * This utility cache will maintain a cache of suggestion completion engines,
 * as they are somewhat heavy to load.
 * If it needs to be loaded, then it will load, and then call the appropriate action,
 * and keep it in the cache.
 */
public class SuggestionCompletionCache {

    private static SuggestionCompletionCache INSTANCE = null;

    Map<String, SuggestionCompletionEngine> cache = new HashMap<String, SuggestionCompletionEngine>();
    
    /**
     * Fact Type Filter per package. A package can only have 1 filter at a time. 
     */
    Map<String, FactTypeFilter> filters = new HashMap<String, FactTypeFilter>();
    private final Constants constants;
    private EventBus eventBus;


    public static SuggestionCompletionCache getInstance() {
        if (INSTANCE == null){
            INSTANCE = new SuggestionCompletionCache();
        }
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
    
    public void setEventBus(final EventBus eventBus) {
    	this.eventBus = eventBus;    	
    	setRefreshHandler();
    }

    public SuggestionCompletionEngine getEngineFromCache(String packageName) {
        SuggestionCompletionEngine eng = cache.get( packageName );
        if (eng == null) {
            ErrorPopup.showMessage(constants.UnableToGetContentAssistanceForThisRule());
            return null;
        }
        return eng;
    }


    /**
     * Refresh SCE for a specific package removing any filter and retrieving
     * the last version of it.
     * @param packageName
     * @param done 
     */
    public void refreshPackage(final String packageName, final Command done) {

        LoadingPopup.showMessage(constants.InitialisingInfoFor0PleaseWait(packageName));
        
        //removes any existing filter
        this.filters.remove(packageName);
        
        RepositoryServiceFactory.getService().loadSuggestionCompletionEngine( packageName, new GenericCallback<SuggestionCompletionEngine>() {
            public void onSuccess(SuggestionCompletionEngine engine) {
                cache.put( packageName, engine );
                done.execute();
            }

            public void onFailure(Throwable t) {
                LoadingPopup.close();
                ErrorPopup.showMessage(constants.UnableToValidatePackageForSCE(packageName));
                done.execute();
            }
        });
    }
    
    /**
     * Gets the last version of SCE for a package and then applies any pre-existing
     * filter to it.
     * @param packageName
     * @param done 
     */
    public void loadPackage(final String packageName, final Command done) {
        
        //get any pre-existing filter for this package
        final FactTypeFilter filter = this.filters.get(packageName);
        
        //refresh the package
        this.refreshPackage(packageName, new Command() {

            public void execute() {

                //applies any pre-existing filter.
                if (filter != null){
                    //set the filter again
                    filters.put(packageName, filter);
                    getEngineFromCache(packageName).setFactTypeFilter(filter);
                }
                
                if (done != null){
                    done.execute();
                }
            }
        });
    }
    
    /**
     * Reloads a package and then applies the given filter.
     * @param packageName the package name.
     * @param filter the filter.
     * @param done the command to be executed after the filter is applied.
     */
    public void applyFactFilter(final String packageName, final FactTypeFilter filter, final Command done) {
        
        //set the filter
        this.filters.put(packageName, filter);
        
        //apply it
        this.loadPackage(packageName, done);
        
    }
        
    private void setRefreshHandler() {
        eventBus.addHandler(RefreshModuleDataModelEvent.TYPE,
                new RefreshModuleDataModelEvent.Handler() {
                    public void onRefreshModuleDataModel(
                    		RefreshModuleDataModelEvent refreshModuleDataModelEvent) {
                        loadPackage(refreshModuleDataModelEvent.getModuleName(), refreshModuleDataModelEvent.getCallbackCommand());
                    }
                });
    }
}
