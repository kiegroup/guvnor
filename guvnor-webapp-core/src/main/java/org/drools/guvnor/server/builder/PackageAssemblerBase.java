/*
 * Copyright 2011 JBoss Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package org.drools.guvnor.server.builder;

import java.io.IOException;
import java.io.StringReader;
import java.util.Iterator;

import org.drools.compiler.DroolsError;
import org.drools.compiler.DroolsParserException;
import org.drools.guvnor.client.common.AssetFormats;
import org.drools.guvnor.server.contenthandler.ContentHandler;
import org.drools.guvnor.server.contenthandler.ContentManager;
import org.drools.guvnor.server.contenthandler.ICompilable;
import org.drools.guvnor.server.util.DroolsHeader;
import org.drools.lang.descr.PackageDescr;
import org.drools.repository.AssetItem;
import org.drools.repository.PackageItem;
import org.drools.repository.RulesRepositoryException;

/**
 * This assembles packages in the BRMS into binary package objects, and deals
 * with errors etc. Each content type is responsible for contributing to the
 * package.
 */
abstract class PackageAssemblerBase extends AssemblerBase {

    protected PackageAssemblerBase(PackageItem packageItem) {
        super( packageItem );
    }

    /**
     * Builds assets that are "rule" assets (ie things that are not functions
     * etc).
     */
    protected void buildAsset(AssetItem asset) {
        ContentHandler contentHandler = ContentManager.getHandler( asset.getFormat() );

        if ( contentHandler instanceof ICompilable && !asset.getDisabled() ) {
            try {
                compile( asset,
                         (ICompilable) contentHandler );
            } catch ( DroolsParserException e ) {
                errorLogger.addError( asset,
                                      e.getMessage() );
                throw new RulesRepositoryException( e );
            } catch ( IOException e ) {
                errorLogger.addError( asset,
                                      e.getMessage() );
            }
        }
    }

    private void compile(AssetItem asset,
                         ICompilable contentHandler) throws DroolsParserException,
                                                    IOException {
        contentHandler.compile( builder,
                                asset,
                                errorLogger );

        if ( builder.hasErrors() ) {
            logErrors( asset );
        }
    }

    /**
     * This prepares the package builder, loads the jars/classpath.
     *
     * @return true if everything is good to go, false if its all gone horribly
     *         wrong, and we can't even get the package header up.
     */
    protected boolean setUpPackage() {

        // firstly we loadup the classpath
        builder.addPackage( new PackageDescr( packageItem.getName() ) );

        //Add package header first as declared types may depend on an import (see https://issues.jboss.org/browse/JBRULES-3133)
        loadPackageHeader();
        loadDeclaredTypes();

        if ( doesPackageBuilderHaveAnyErrors() ) {
            return false;
        }

        loadDSLFiles();
        loadFunctions();

        return !errorLogger.hasErrors();
    }

    private boolean doesPackageBuilderHaveAnyErrors() {
        if ( builder.hasErrors() ) {
            // if we have any failures, lets drop out now, no point in going any further
            recordBuilderErrors( packageItem.getFormat(),
                                 packageItem.getName(),
                                 packageItem.getUUID(),
                                 true,
                                 false );
            return true;
        }
        return false;
    }

    private void loadFunctions() {
        try {
            addDrl( getAllFunctionsAsOneString().toString() );
        } catch ( IOException e ) {
            throw new RulesRepositoryException( "Unexpected error when parsing package.",
                                                e );
        } catch ( DroolsParserException e ) {
            // TODO: Not really a RulesRepositoryException is it? -Rikkola-
            throw new RulesRepositoryException( "Unexpected error when parsing package.",
                                                e );
        }

        // If the function part had errors we need to add them one by one to find out which one is bad.
        if ( builder.hasErrors() ) {
            searchTheFunctionWithAnError();
        }
    }

    /**
     * Get the function DRLs as one string because they might be calling each others.
     *
     * @return
     */
    private StringBuilder getAllFunctionsAsOneString() {
        Iterator<AssetItem> functionsIterator = getAssetItemIterator( AssetFormats.FUNCTION );
        StringBuilder stringBuilder = new StringBuilder();

        while ( functionsIterator.hasNext() ) {
            AssetItem function = functionsIterator.next();
            if ( !function.getDisabled() ) {
                stringBuilder.append( function.getContent() );
            }
        }

        return stringBuilder;
    }

    private void searchTheFunctionWithAnError() {
        builder.clearErrors();
        Iterator<AssetItem> functionsIterator = getAssetItemIterator( AssetFormats.FUNCTION );

        while ( functionsIterator.hasNext() ) {
            AssetItem function = functionsIterator.next();
            if ( !function.getDisabled() ) {
                try {
                    addDrl( function.getContent() );
                } catch ( IOException e ) {
                    errorLogger.addError( function,
                                          "IOException: " + e.getMessage() );
                } catch ( DroolsParserException e ) {
                    errorLogger.addError( packageItem,
                                          "Parser exception: " + e.getMessage() );
                }

                if ( builder.hasErrors() ) {
                    logErrors( function );
                }
            }
        }
    }

    private void loadPackageHeader() {
        try {
            addDrl( DroolsHeader.getDroolsHeader( packageItem ) );
        } catch ( IOException e ) {
            errorLogger.addError( packageItem,
                                  "IOException: " + e.getMessage() );
        } catch ( DroolsParserException e ) {
            errorLogger.addError( packageItem,
                                  "Parser exception: " + e.getMessage() );
        }
    }

    private void loadDeclaredTypes() {
        Iterator<AssetItem> declaredTypesIterator = getAssetItemIterator( AssetFormats.DRL_MODEL );

        while ( declaredTypesIterator.hasNext() ) {
            AssetItem assetItem = declaredTypesIterator.next();
            if ( !assetItem.getDisabled() ) {
                try {
                    addDrl( assetItem.getContent() );
                } catch ( DroolsParserException e ) {
                    errorLogger.addError( assetItem,
                                          "Parser exception: " + e.getMessage() );
                } catch ( IOException e ) {
                    errorLogger.addError( assetItem,
                                          "IOException: " + e.getMessage() );
                }
            }
        }
    }

    private boolean isEmpty(String content) {
        return content == null || content.trim().length() == 0;
    }

    /**
     * This will return true if there is an error in the package configuration
     * or functions.
     *
     * @return
     */
    public boolean isPackageConfigurationInError() {
        return errorLogger.hasErrors() && this.errorLogger.getErrors().get(0).isPackageItem();
    }

    private void addDrl(String drl) throws IOException,
                                   DroolsParserException {
        if ( isEmpty( drl ) ) {
            return;
        }
        
        builder.addPackageFromDrl( new StringReader( drl ) );
    }

    private void recordBuilderErrors(String format,
                                     String name,
                                     String uuid,
                                     boolean isPackageItem,
                                     boolean isAssetItem) {
        for ( DroolsError droolsError : builder.getErrors().getErrors() ) {
            errorLogger.addError(
                                  droolsError.getMessage(),
                                  format,
                                  name,
                                  uuid,
                                  isPackageItem,
                                  isAssetItem );
        }

        // clear the errors, so we don't double report.
        builder.clearErrors();
    }

    private void logErrors(AssetItem asset) {
        this.recordBuilderErrors( asset.getFormat(),
                                  asset.getName(),
                                  asset.getUUID(),
                                  false,
                                  true );
    }
}
