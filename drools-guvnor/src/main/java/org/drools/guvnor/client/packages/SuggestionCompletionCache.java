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

import com.google.gwt.user.client.Command;

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

    private static SuggestionCompletionCache INSTANCE = new SuggestionCompletionCache();

    Map cache = new HashMap();



    public static SuggestionCompletionCache getInstance() {
        return INSTANCE;
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
            ErrorPopup.showMessage( "Unable to get content assistance for this rule." );
            return null;
        }
        return eng;
    }


    void loadPackage(final String packageName, final Command command) {
        System.out.println("Loading package Suggestions...");
        RepositoryServiceFactory.getService().loadSuggestionCompletionEngine( packageName, new GenericCallback() {
            public void onSuccess(Object data) {
                SuggestionCompletionEngine engine = (SuggestionCompletionEngine) data;
                cache.put( packageName, engine );
                command.execute();
            }

            public void onFailure(Throwable t) {
            	LoadingPopup.close();
            	ErrorPopup.showMessage("Unable to validate package configuration (eg, DSLs) for [" + packageName + "]. " +
    			"Suggestion completions may not operate for graphical editors for this package.");
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

}