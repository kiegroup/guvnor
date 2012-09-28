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
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.drools.guvnor.client.rpc.*;
import org.drools.guvnor.server.repository.Preferred;
import org.drools.guvnor.server.standalonededitor.BRLRuleAssetProvider;
import org.drools.guvnor.server.standalonededitor.NewRuleAssetProvider;
import org.drools.guvnor.server.standalonededitor.RuleAssetProvider;
import org.drools.guvnor.server.standalonededitor.UUIDRuleAssetProvider;
import org.drools.ide.common.client.modeldriven.brl.RuleModel;
import org.drools.ide.common.server.util.BRLPersistence;
import org.drools.ide.common.server.util.BRXMLPersistence;
import org.drools.repository.RulesRepository;

import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.UUID;
import javax.inject.Inject;

import org.drools.guvnor.server.util.LoggingHelper;

/**
 * All the needed Services in order to get Guvnor's Editors running as standalone
 * app.
 */
public class StandaloneEditorServiceImplementation extends RemoteServiceServlet
        implements
        StandaloneEditorService {

    private static final LoggingHelper log = LoggingHelper.getLogger(StandaloneEditorServiceImplementation.class);
    
    private static final long serialVersionUID = 530l;
    
    @Inject @Preferred
    public RulesRepository repository;

    @Inject
    private ServiceImplementation serviceImplementation;
    
    @Inject
    private RepositoryAssetService repositoryAssetService;
    

    public StandaloneEditorInvocationParameters getInvocationParameters(String parametersUUID) throws DetailedSerializationException {

        HttpSession session = this.getThreadLocalRequest().getSession();

        try {
            //Get the parameters from the session
            @SuppressWarnings("unchecked")
            Map<String, Object> sessionParameters = (Map<String, Object>) session.getAttribute(parametersUUID);

            if (sessionParameters == null || sessionParameters.isEmpty()) {
                throw new DetailedSerializationException("Error initializing Guided Editor",
                        "No initial parameters were supplied");
            }

            boolean hideLHSInEditor = isHideLHSInEditor(sessionParameters);
            boolean hideRHSInEditor = isHideRHSInEditor(sessionParameters);
            boolean hideAttributesInEditor = isHideAttributesInEditor(sessionParameters);
            String clientName = getClientName(sessionParameters);
            
            Asset[] activeWorkingSets = getActiveWorkingSets(sessionParameters);
            Asset[] activeTemporalWorkingSets = getActiveTemporalWorkingSets(sessionParameters);
            
            StandaloneEditorInvocationParameters invocationParameters = new StandaloneEditorInvocationParameters();
            this.loadRuleAssetsFromSessionParameters(sessionParameters,
                    invocationParameters);
            invocationParameters.setHideLHS(hideLHSInEditor);
            invocationParameters.setHideRHS(hideRHSInEditor);
            invocationParameters.setHideAttributes(hideAttributesInEditor);
            invocationParameters.setClientName(clientName);
            invocationParameters.setActiveWorkingSets(activeWorkingSets);
            invocationParameters.setActiveTemporalWorkingSets(activeTemporalWorkingSets);
            
            return invocationParameters;
        } finally {
            //clear session parameters
            session.removeAttribute(parametersUUID);
        }

    }

    /**
     * Convert each value of GE_ACTIVE_WORKING_SET_UUIDS_PARAMETER_NAME to a
     * RuleAssets[]. The information is retrieved from the repository. This
     * means that each UUID must exist there.
     * @param sessionParameters
     * @return 
     */
    private Asset[] getActiveWorkingSets(Map<String, Object> sessionParameters) throws DetailedSerializationException {
        try {
            String[] wsUUID = (String[])sessionParameters.get(StandaloneEditorServlet.STANDALONE_EDITOR_SERVLET_PARAMETERS.GE_ACTIVE_WORKING_SET_UUIDS_PARAMETER_NAME.getParameterName());
            
            List<Asset> result = new ArrayList<Asset>();
            
            //for UUIDs, we need to get them from repository
            if (wsUUID != null && wsUUID.length > 0){
                Asset[] workingSetRuleAssets = repositoryAssetService.loadRuleAssets(wsUUID);
                result.addAll(Arrays.asList(workingSetRuleAssets));
            }
            
            return result.toArray(new Asset[result.size()]);
        } catch (SerializationException ex) {
            log.error("Error getting Working Set Definitions", ex);
            throw new DetailedSerializationException("Error getting Working Set Definitions", ex.getLocalizedMessage());
        }
    }
    
    
    /**
     * Combines GE_VALID_FACT_TYPE_PARAMETER_NAME and GE_ACTIVE_WORKING_SET_XML_DEFINITIONS_PARAMETER_NAME
     * and creates an array of RuleAssets[].The returned assets are going to be generated
     * on the fly and never be persisted in the repository
     * @param sessionParameters
     * @return 
     */
    private Asset[] getActiveTemporalWorkingSets(Map<String, Object> sessionParameters) {
            String[] validFacts = (String[])sessionParameters.get(StandaloneEditorServlet.STANDALONE_EDITOR_SERVLET_PARAMETERS.GE_VALID_FACT_TYPE_PARAMETER_NAME.getParameterName());
            String[] xmlDefinitions = (String[])sessionParameters.get(StandaloneEditorServlet.STANDALONE_EDITOR_SERVLET_PARAMETERS.GE_ACTIVE_WORKING_SET_XML_DEFINITIONS_PARAMETER_NAME.getParameterName());
            
            List<Asset> result = new ArrayList<Asset>();
            
            //for validFacts we need to create a temporal Working Set RuleAsset
            if (validFacts != null && validFacts.length > 0){
                final Asset workingSet = new Asset();
                workingSet.setUuid( "workingSetMock"+UUID.randomUUID().toString() );
            
                WorkingSetConfigData wsConfig = new WorkingSetConfigData();
                wsConfig.validFacts = validFacts;
            
                workingSet.setContent( wsConfig );
                
                result.add(workingSet);
            }
            
            //for each xml working set definition we need to unmarshall it
            //to WorkingSetConfigData and create a Working Set Rule Asset
            if (xmlDefinitions != null && xmlDefinitions.length > 0){
                //Unmarshal each definition and put it in the list
                XStream xt = new XStream(new DomDriver());
                
                for (String xml : xmlDefinitions) {
                    WorkingSetConfigData workingSetConfigData = (WorkingSetConfigData)xt.fromXML(xml);

                    final Asset workingSet = new Asset();
                    workingSet.setUuid( "workingSetMock"+UUID.randomUUID().toString() );

                    workingSet.setContent( workingSetConfigData );

                    result.add(workingSet);
                }
            }
            
            
            return result.toArray(new Asset[result.size()]);
    }
    

    private String getClientName(Map<String, Object> sessionParameters) {
        Object attribute = sessionParameters.get(StandaloneEditorServlet.STANDALONE_EDITOR_SERVLET_PARAMETERS.GE_CLIENT_NAME_PARAMETER_NAME.getParameterName());
        return attribute.toString();
    }

    private boolean isHideAttributesInEditor(Map<String, Object> sessionParameters) {
        boolean hideAttributesInEditor = false;
        Object attribute = sessionParameters.get(StandaloneEditorServlet.STANDALONE_EDITOR_SERVLET_PARAMETERS.GE_HIDE_RULE_ATTRIBUTES_PARAMETER_NAME.getParameterName());
        if (attribute != null) {
            hideAttributesInEditor = Boolean.parseBoolean(attribute.toString());
        }
        return hideAttributesInEditor;
    }

    private boolean isHideRHSInEditor(Map<String, Object> sessionParameters) {
        boolean hideRHSInEditor = false;
        Object attribute = sessionParameters.get(StandaloneEditorServlet.STANDALONE_EDITOR_SERVLET_PARAMETERS.GE_HIDE_RULE_RHS_PARAMETER_NAME.getParameterName());
        if (attribute != null) {
            hideRHSInEditor = Boolean.parseBoolean(attribute.toString());
        }
        return hideRHSInEditor;
    }

    private boolean isHideLHSInEditor(Map<String, Object> sessionParameters) {
        boolean hideLHSInEditor = false;
        Object attribute = sessionParameters.get(StandaloneEditorServlet.STANDALONE_EDITOR_SERVLET_PARAMETERS.GE_HIDE_RULE_LHS_PARAMETER_NAME.getParameterName());
        if (attribute != null) {
            hideLHSInEditor = Boolean.parseBoolean(attribute.toString());
        }
        return hideLHSInEditor;
    }

    /**
     * To open the Standalone Editor, you should be gone through
     * StandaloneEditorServlet first. This servlet put all the POST parameters into
     * session. This method takes those parameters and load the corresponding
     * assets.
     * This method will set the assets in parameters
     *
     * @param parameters
     * @throws DetailedSerializationException
     */
    private void loadRuleAssetsFromSessionParameters(Map<String, Object> sessionParameters,
                                                     StandaloneEditorInvocationParameters invocationParameters) throws DetailedSerializationException {

        String packageName = getPackageName(sessionParameters);
        String categoryName = getCategoryName(sessionParameters);
        String[] initialBRL = getInitialBRL(sessionParameters);
        String[] assetsUUIDs = getAssetUUIDs(sessionParameters);

        boolean createNewAsset = isCreateNewAsset(sessionParameters);
        String assetName = getAssetName(sessionParameters);
        String assetFormat = getAssetFormat(sessionParameters);

        RuleAssetProvider provider;
        if (createNewAsset) {
            provider = new NewRuleAssetProvider(packageName,
                    categoryName,
                    assetName,
                    assetFormat, serviceImplementation, repositoryAssetService);
            invocationParameters.setTemporalAssets(false);
        } else if (assetsUUIDs != null) {
            provider = new UUIDRuleAssetProvider(assetsUUIDs, repositoryAssetService);
            invocationParameters.setTemporalAssets(false);
        } else if (initialBRL != null) {
            provider = new BRLRuleAssetProvider(packageName,
                    initialBRL, repositoryAssetService);
            invocationParameters.setTemporalAssets(true);
        } else {
            throw new IllegalStateException();
        }

        invocationParameters.setAssetsToBeEdited(provider.getRuleAssets());

    }

    private String getAssetFormat(Map<String, Object> sessionParameters) {
        return (String) sessionParameters.get(StandaloneEditorServlet.STANDALONE_EDITOR_SERVLET_PARAMETERS.GE_ASSET_FORMAT_PARAMETER_NAME.getParameterName());
    }

    private String getAssetName(Map<String, Object> sessionParameters) {
        return (String) sessionParameters.get(StandaloneEditorServlet.STANDALONE_EDITOR_SERVLET_PARAMETERS.GE_ASSET_NAME_PARAMETER_NAME.getParameterName());
    }

    private boolean isCreateNewAsset(Map<String, Object> sessionParameters) {
        boolean createNewAsset = false;
        Object attribute = sessionParameters.get(StandaloneEditorServlet.STANDALONE_EDITOR_SERVLET_PARAMETERS.GE_CREATE_NEW_ASSET_PARAMETER_NAME.getParameterName());
        if (attribute != null) {
            createNewAsset = Boolean.parseBoolean(attribute.toString());
        }
        return createNewAsset;
    }

    private String[] getAssetUUIDs(Map<String, Object> sessionParameters) {
        return (String[]) sessionParameters.get(StandaloneEditorServlet.STANDALONE_EDITOR_SERVLET_PARAMETERS.GE_ASSETS_UUIDS_PARAMETER_NAME.getParameterName());
    }

    private String[] getInitialBRL(Map<String, Object> sessionParameters) {
        return (String[]) sessionParameters.get(StandaloneEditorServlet.STANDALONE_EDITOR_SERVLET_PARAMETERS.GE_BRL_PARAMETER_NAME.getParameterName());
    }

    private String getCategoryName(Map<String, Object> sessionParameters) {
        return (String) sessionParameters.get(StandaloneEditorServlet.STANDALONE_EDITOR_SERVLET_PARAMETERS.GE_CATEGORY_PARAMETER_NAME.getParameterName());
    }

    private String getPackageName(Map<String, Object> sessionParameters) {
        return (String) sessionParameters.get(StandaloneEditorServlet.STANDALONE_EDITOR_SERVLET_PARAMETERS.GE_PACKAGE_PARAMETER_NAME.getParameterName());
    }

    /**
     * Returns the DRL source code of the given assets.
     *
     * @param assets
     * @return
     * @throws SerializationException
     */
    public String[] getAsstesDRL(Asset[] assets) throws SerializationException {

        String[] sources = new String[assets.length];

        for (int i = 0; i < assets.length; i++) {
            sources[i] = repositoryAssetService.buildAssetSource(assets[i]);
        }

        return sources;
    }

    /**
     * Returns the BRL source code of the given assets.
     *
     * @param assets
     * @return
     * @throws SerializationException
     */
    public String[] getAsstesBRL(Asset[] assets) throws SerializationException {

        String[] sources = new String[assets.length];

        BRLPersistence converter = BRXMLPersistence.getInstance();
        for (int i = 0; i < assets.length; i++) {
            sources[i] = converter.marshal((RuleModel) assets[i].getContent());
        }

        return sources;
    }
    
}
