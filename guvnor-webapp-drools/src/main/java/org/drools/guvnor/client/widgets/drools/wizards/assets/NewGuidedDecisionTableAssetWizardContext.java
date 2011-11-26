/*
 * Copyright 2011 JBoss Inc
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
package org.drools.guvnor.client.widgets.drools.wizards.assets;

import org.drools.guvnor.client.rpc.NewGuidedDecisionTableAssetConfiguration;
import org.drools.guvnor.client.widgets.wizards.WizardPlace;
import org.drools.ide.common.client.modeldriven.dt52.GuidedDecisionTable52.TableFormat;

import com.google.gwt.place.shared.PlaceTokenizer;

/**
 * A container for the details required to create a new Guided Decision Table
 * Asset on the repository
 */
public class NewGuidedDecisionTableAssetWizardContext extends NewAssetWizardContext {

    private final TableFormat tableFormat;

    public NewGuidedDecisionTableAssetWizardContext(NewGuidedDecisionTableAssetConfiguration configuration) {
        super( configuration.getAssetName(),
               configuration.getPackageName(),
               configuration.getPackageUUID(),
               configuration.getDescription(),
               configuration.getInitialCategory(),
               configuration.getFormat() );
        this.tableFormat = configuration.getTableFormat();
    }

    public TableFormat getTableFormat() {
        return this.tableFormat;
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = hash + 31 * tableFormat.hashCode();
        return hash;
    }

    @Override
    public boolean equals(Object o) {
        if ( !(o instanceof NewGuidedDecisionTableAssetWizardContext) ) {
            return false;
        }
        if ( !super.equals( o ) ) {
            return false;
        }
        NewGuidedDecisionTableAssetWizardContext that = (NewGuidedDecisionTableAssetWizardContext) o;
        if ( !tableFormat.equals( that.tableFormat ) ) return false;
        return true;
    }

    public static class Tokenizer
        implements
        PlaceTokenizer<WizardPlace<NewGuidedDecisionTableAssetWizardContext>> {

        private final String ASSET_NAME   = "ASSET_NAME=";
        private final String PACKAGE_NAME = "?PACKAGE_NAME=";
        private final String PACKAGE_UUID = "?PACKAGE_UUID=";
        private final String TABLE_FORMAT = "?TABLE_FORMAT=";
        private final String DESCRIPTION  = "?DESCRIPTION=";
        private final String CATEGORY     = "?CATEGORY=";
        private final String FORMAT       = "?FORMAT=";

        public String getToken(WizardPlace<NewGuidedDecisionTableAssetWizardContext> place) {
            StringBuilder sb = new StringBuilder();
            sb.append( ASSET_NAME );
            sb.append( nullSafe( place.getContext().getAssetName() ) );
            sb.append( PACKAGE_NAME );
            sb.append( nullSafe( place.getContext().getPackageName() ) );
            sb.append( PACKAGE_UUID );
            sb.append( nullSafe( place.getContext().getPackageUUID() ) );
            sb.append( TABLE_FORMAT );
            sb.append( place.getContext().getTableFormat().toString() );
            sb.append( DESCRIPTION );
            sb.append( nullSafe( place.getContext().getDescription() ) );
            sb.append( CATEGORY );
            sb.append( nullSafe( place.getContext().getInitialCategory() ) );
            sb.append( FORMAT );
            sb.append( nullSafe( place.getContext().getFormat() ) );
            return sb.toString();
        }

        private String nullSafe(String s) {
            return s == null ? "" : s;
        }

        public WizardPlace<NewGuidedDecisionTableAssetWizardContext> getPlace(String token) {
            String assetName = getAssetName( token );
            String packageName = getPackageName( token );
            String packageUUID = getPackageUUID( token );
            TableFormat tableFormat = getTableFormat( token );
            String description = getDescription( token );
            String category = getCategory( token );
            String format = getFormat( token );

            NewGuidedDecisionTableAssetConfiguration config = new NewGuidedDecisionTableAssetConfiguration( assetName,
                                                                                                            packageName,
                                                                                                            packageUUID,
                                                                                                            tableFormat,
                                                                                                            description,
                                                                                                            category,
                                                                                                            format );

            NewGuidedDecisionTableAssetWizardContext context = new NewGuidedDecisionTableAssetWizardContext( config );
            return new WizardPlace<NewGuidedDecisionTableAssetWizardContext>( context );
        }

        private String getAssetName(String token) {
            return token.substring( token.indexOf( ASSET_NAME ) + ASSET_NAME.length(),
                                    token.indexOf( PACKAGE_NAME ) );
        }

        private String getPackageName(String token) {
            return token.substring( token.indexOf( PACKAGE_NAME ) + PACKAGE_NAME.length(),
                                    token.indexOf( PACKAGE_UUID ) );
        }

        private String getPackageUUID(String token) {
            return token.substring( token.indexOf( PACKAGE_UUID ) + PACKAGE_UUID.length(),
                                    token.indexOf( TABLE_FORMAT ) );
        }

        private TableFormat getTableFormat(String token) {
            String tableFormat = token.substring( token.indexOf( TABLE_FORMAT ) + TABLE_FORMAT.length(),
                                                  token.indexOf( DESCRIPTION ) );
            return TableFormat.valueOf( tableFormat );
        }

        private String getDescription(String token) {
            return token.substring( token.indexOf( DESCRIPTION ) + DESCRIPTION.length(),
                                    token.indexOf( CATEGORY ) );
        }

        private String getCategory(String token) {
            return token.substring( token.indexOf( CATEGORY ) + CATEGORY.length(),
                                    token.indexOf( FORMAT ) );
        }

        private String getFormat(String token) {
            return token.substring( token.indexOf( FORMAT ) + FORMAT.length() );
        }

    }

}
