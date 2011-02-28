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
package org.drools.guvnor.server;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import java.util.Map;
import javax.servlet.http.HttpSession;
import org.drools.guvnor.client.rpc.DetailedSerializationException;
import org.drools.guvnor.client.rpc.RuleAsset;
import org.drools.guvnor.client.rpc.StandaloneEditorService;
import org.drools.guvnor.client.ruleeditor.standalone.StandaloneEditorInvocationParameters;
import org.drools.guvnor.server.standalonededitor.BRLRuleAssetProvider;
import org.drools.guvnor.server.standalonededitor.NewRuleAssetProvider;
import org.drools.guvnor.server.standalonededitor.RuleAssetProvider;
import org.drools.guvnor.server.standalonededitor.UUIDRuleAssetProvider;
import org.drools.ide.common.client.modeldriven.brl.RuleModel;
import org.drools.ide.common.server.util.BRLPersistence;
import org.drools.repository.RulesRepository;
import org.jboss.seam.annotations.In;

import org.drools.ide.common.server.util.BRXMLPersistence;

/**
 * All the needed Services in order to get Guvnor's Editors running as standalone
 * app.
 */
public class StandaloneEditorServiceImplementation extends RemoteServiceServlet
    implements
    StandaloneEditorService {

    @In
    public RulesRepository    repository;
    private static final long serialVersionUID = 520l;

    public RulesRepository getRulesRepository() {
        return this.repository;
    }

    private RepositoryAssetService getAssetService() {
        return RepositoryServiceServlet.getAssetService();
    }

    public StandaloneEditorInvocationParameters getInvocationParameters(String parametersUUID) throws DetailedSerializationException {

        HttpSession session = this.getThreadLocalRequest().getSession();
        
        try{
            //Get the parameters from the session
            Map<String, Object> sessionParameters = (Map<String, Object>
            ) session.getAttribute(parametersUUID);

            if (sessionParameters == null || sessionParameters.isEmpty()){
                throw new DetailedSerializationException("Error initializing Guided Editor", "No initial parameters were supplied");
            }

            boolean hideLHSInEditor = false;
            Object attribute =  sessionParameters.get( StandaloneEditorServlet.STANDALONE_EDITOR_SERVLET_PARAMETERS.GE_HIDE_RULE_LHS_PARAMETER_NAME.getParameterName() );
            if ( attribute != null ) {
                hideLHSInEditor = Boolean.parseBoolean( attribute.toString() );
            }

            boolean hideRHSInEditor = false;
            attribute = sessionParameters.get( StandaloneEditorServlet.STANDALONE_EDITOR_SERVLET_PARAMETERS.GE_HIDE_RULE_RHS_PARAMETER_NAME.getParameterName() );
            if ( attribute != null ) {
                hideRHSInEditor = Boolean.parseBoolean( attribute.toString() );
            }

            boolean hideAttributesInEditor = false;
            attribute = sessionParameters.get( StandaloneEditorServlet.STANDALONE_EDITOR_SERVLET_PARAMETERS.GE_HIDE_RULE_ATTRIBUTES_PARAMETER_NAME.getParameterName() );
            if ( attribute != null ) {
                hideAttributesInEditor = Boolean.parseBoolean( attribute.toString() );
            }
            
            attribute = sessionParameters.get( StandaloneEditorServlet.STANDALONE_EDITOR_SERVLET_PARAMETERS.GE_CLIENT_NAME_PARAMETER_NAME.getParameterName() );
            String clientName = attribute.toString();
            
            String[] validFactTypes = (String[])sessionParameters.get( StandaloneEditorServlet.STANDALONE_EDITOR_SERVLET_PARAMETERS.GE_VALID_FACT_TYPE_PARAMETER_NAME.getParameterName() );

            StandaloneEditorInvocationParameters invocationParameters = new StandaloneEditorInvocationParameters();

            this.loadRuleAssetsFromSessionParameters(sessionParameters, invocationParameters);

            invocationParameters.setHideLHS( hideLHSInEditor );
            invocationParameters.setHideRHS( hideRHSInEditor );
            invocationParameters.setHideAttributes( hideAttributesInEditor );
            invocationParameters.setValidFactTypes(validFactTypes);
            invocationParameters.setClientName(clientName);


            return invocationParameters;
        } finally{
            //clear session parameters
            session.removeAttribute(parametersUUID);
        }
        
    }

    /**
     * To open the Standalone Editor, you should be gone through 
     * StandaloneEditorServlet first. This servlet put all the POST parameters into
     * session. This method takes those parameters and load the corresponding
     * assets.
     * This method will set the assets in parameters 
     * @param parameters 
     * @throws DetailedSerializationException
     */
    private void loadRuleAssetsFromSessionParameters(Map<String, Object> sessionParameters, StandaloneEditorInvocationParameters invocationParameters) throws DetailedSerializationException {

        String packageName = (String)sessionParameters.get( StandaloneEditorServlet.STANDALONE_EDITOR_SERVLET_PARAMETERS.GE_PACKAGE_PARAMETER_NAME.getParameterName() );
        String categoryName = (String)sessionParameters.get( StandaloneEditorServlet.STANDALONE_EDITOR_SERVLET_PARAMETERS.GE_CATEGORY_PARAMETER_NAME.getParameterName() );
        String[] initialBRL = (String[])sessionParameters.get( StandaloneEditorServlet.STANDALONE_EDITOR_SERVLET_PARAMETERS.GE_BRL_PARAMETER_NAME.getParameterName() );
        String[] assetsUUIDs = (String[])sessionParameters.get( StandaloneEditorServlet.STANDALONE_EDITOR_SERVLET_PARAMETERS.GE_ASSETS_UUIDS_PARAMETER_NAME.getParameterName() );

        boolean createNewAsset = false;
        Object attribute = sessionParameters.get( StandaloneEditorServlet.STANDALONE_EDITOR_SERVLET_PARAMETERS.GE_CREATE_NEW_ASSET_PARAMETER_NAME.getParameterName() );
        if ( attribute != null ) {
            createNewAsset = Boolean.parseBoolean( attribute.toString() );
        }
        String assetName = (String) sessionParameters.get( StandaloneEditorServlet.STANDALONE_EDITOR_SERVLET_PARAMETERS.GE_ASSET_NAME_PARAMETER_NAME.getParameterName() );
        String assetFormat = (String) sessionParameters.get( StandaloneEditorServlet.STANDALONE_EDITOR_SERVLET_PARAMETERS.GE_ASSET_FORMAT_PARAMETER_NAME.getParameterName() );

        RuleAssetProvider provider;
        if ( createNewAsset ) {
            provider = new NewRuleAssetProvider( packageName,
                                                 categoryName,
                                                 assetName,
                                                 assetFormat);
            invocationParameters.setTemporalAssets(false);
        } else if ( assetsUUIDs != null ) {
            provider = new UUIDRuleAssetProvider( assetsUUIDs );
            invocationParameters.setTemporalAssets(false);
        } else if ( initialBRL != null ) {
            provider = new BRLRuleAssetProvider( packageName,
                                                 initialBRL );
            invocationParameters.setTemporalAssets(true);
        } else {
            throw new IllegalStateException();
        }

        invocationParameters.setAssetsToBeEdited(provider.getRuleAssets());

    }

    /**
     * Returns the DRL source code of the given assets.
     * @param assets
     * @return
     * @throws SerializationException
     */
    public String[] getAsstesDRL(RuleAsset[] assets) throws SerializationException {

        String[] sources = new String[assets.length];

        for ( int i = 0; i < assets.length; i++ ) {
            sources[i] = this.getAssetService().buildAssetSource( assets[i] );
        }

        return sources;
    }

    /**
     * Returns the BRL source code of the given assets.
     * @param assets
     * @return
     * @throws SerializationException
     */
    public String[] getAsstesBRL(RuleAsset[] assets) throws SerializationException {

        String[] sources = new String[assets.length];

        BRLPersistence converter = BRXMLPersistence.getInstance();
        for ( int i = 0; i < assets.length; i++ ) {
            sources[i] = converter.marshal( (RuleModel) assets[i].content );
        }

        return sources;
    }
}
