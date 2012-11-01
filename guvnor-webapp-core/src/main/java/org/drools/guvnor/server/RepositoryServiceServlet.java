/*
 * Copyright 2010 JBoss Inc
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

import java.io.IOException;
import java.io.PrintWriter;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import org.drools.guvnor.client.rpc.Module;
import org.drools.guvnor.client.rpc.ModuleService;
import org.drools.guvnor.client.rpc.RepositoryService;
import org.drools.guvnor.server.util.LoggingHelper;
import org.drools.repository.RulesRepositoryException;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import org.jboss.solder.core.Veto;
import org.drools.guvnor.client.rpc.Path;

/**
 * GWT RPC service endpoint for Repository service. A place to hang some exception handling mainly.
 * This passes on all requests unmolested to the underlying ServiceImplemention class.
 */
@Veto
public class RepositoryServiceServlet
        extends RemoteServiceServlet
        implements
        RepositoryService {

    private static final long serialVersionUID = 495822L;

    private static final LoggingHelper log = LoggingHelper.getLogger( RepositoryServiceServlet.class );

    @Inject
    private ServiceImplementation serviceImplementation;

    @Inject
    private ModuleService moduleService;

    @Override
    protected void doUnexpectedFailure(Throwable e) {
        if ( e.getCause() instanceof RulesRepositoryException ) {
            log.error( e.getMessage(),
                       e.getCause() );
            sendErrorMessage( e.getCause().getMessage() );
        } else {
            if ( e.getCause() != null ) {
                log.error( e.getMessage(),
                           e.getCause() );
            } else {
                log.error( e.getMessage(),
                           e );
            }
            sendErrorMessage( "Sorry, a technical error occurred. Please contact a system administrator." );
        }
    }

    private void close(PrintWriter writer) {
        if ( writer != null ) {
            writer.flush();
            writer.close();
        }
    }

    private void sendErrorMessage(String msg) {
        HttpServletResponse response = getThreadLocalResponse();
        response.setContentType( "text/plain" );
        response.setStatus( HttpServletResponse.SC_BAD_REQUEST );
        PrintWriter writer = null;
        try {
            writer = response.getWriter();
            writer.write( msg );
        } catch (IOException ex) {
            getServletContext().log( "respondWithUnexpectedFailure failed while sending the previous failure to the client",
                    ex );
        } finally {
            close( writer );
        }
    }

    /**
     * PLACE THE FOLLOWING IN RepositoryServiceServlet.java *
     */


    public org.drools.guvnor.client.rpc.PageResponse loadRuleListForState(org.drools.guvnor.client.rpc.StatePageRequest p0) throws com.google.gwt.user.client.rpc.SerializationException {
        return serviceImplementation.loadRuleListForState( p0 );
    }

    public org.drools.guvnor.client.rpc.TableDataResult loadRuleListForState(java.lang.String p0,
                                                                             int p1,
                                                                             int p2,
                                                                             java.lang.String p3) throws com.google.gwt.user.client.rpc.SerializationException {
        return serviceImplementation.loadRuleListForState( p0,
                p1,
                p2,
                p3 );
    }

    public org.drools.guvnor.client.rpc.TableConfig loadTableConfig(java.lang.String p0) {
        return serviceImplementation.loadTableConfig( p0 );
    }

    public Path createNewRule(java.lang.String p0,
                                          java.lang.String p1,
                                          java.lang.String p2,
                                          java.lang.String p3,
                                          java.lang.String p4) throws com.google.gwt.user.client.rpc.SerializationException {
        return serviceImplementation.createNewRule( p0,
                p1,
                p2,
                p3,
                p4 );
    }

    public Path createNewRule(org.drools.guvnor.client.rpc.NewAssetConfiguration p0) throws com.google.gwt.user.client.rpc.SerializationException {
        return serviceImplementation.createNewRule( p0 );
    }
    
    public Path createNewRule(org.drools.guvnor.client.rpc.NewAssetWithContentConfiguration p0) throws com.google.gwt.user.client.rpc.SerializationException {
        return serviceImplementation.createNewRule( p0 );
    }

    public Path createNewImportedRule(java.lang.String p0,
                                                  java.lang.String p1) throws com.google.gwt.user.client.rpc.SerializationException {
        return serviceImplementation.createNewImportedRule( p0,
                p1 );
    }

    public void deleteUncheckedRule(java.lang.String p0) {
        serviceImplementation.deleteUncheckedRule( p0 );
    }

    public void clearRulesRepository() {
        serviceImplementation.clearRulesRepository();
    }

    public java.lang.String[] listWorkspaces() {
        return serviceImplementation.listWorkspaces();
    }

    public void createWorkspace(java.lang.String p0) {
        serviceImplementation.createWorkspace( p0 );
    }

    public void removeWorkspace(java.lang.String p0) {
        serviceImplementation.removeWorkspace( p0 );
    }

    public void updateWorkspace(java.lang.String p0,
                                java.lang.String[] p1,
                                java.lang.String[] p2) {
        serviceImplementation.updateWorkspace( p0,
                p1,
                p2 );
    }

    public java.lang.String[] listStates() throws com.google.gwt.user.client.rpc.SerializationException {
        return serviceImplementation.listStates();
    }

    public java.lang.String createState(java.lang.String p0) throws com.google.gwt.user.client.rpc.SerializationException {
        return serviceImplementation.createState( p0 );
    }

    public void renameState(java.lang.String p0,
                            java.lang.String p1) throws com.google.gwt.user.client.rpc.SerializationException {
        serviceImplementation.renameState( p0,
                p1 );
    }

    public void removeState(java.lang.String p0) throws com.google.gwt.user.client.rpc.SerializationException {
        serviceImplementation.removeState( p0 );
    }

    public java.lang.String[] getCustomSelectors() throws com.google.gwt.user.client.rpc.SerializationException {
        return serviceImplementation.getCustomSelectors();
    }

    public org.drools.guvnor.client.rpc.PageResponse showLog(org.drools.guvnor.client.rpc.PageRequest p0) {
        return serviceImplementation.showLog( p0 );
    }

    public org.drools.guvnor.client.rpc.LogEntry[] showLog() {
        return serviceImplementation.showLog();
    }

    public void cleanLog() {
        serviceImplementation.cleanLog();
    }

    public java.lang.String[] loadDropDownExpression(java.lang.String[] p0,
                                                     java.lang.String p1) {
        return serviceImplementation.loadDropDownExpression( p0,
                p1 );
    }

    public org.drools.guvnor.client.rpc.PageResponse queryFullText(org.drools.guvnor.client.rpc.QueryPageRequest p0) throws com.google.gwt.user.client.rpc.SerializationException {
        return serviceImplementation.queryFullText( p0 );
    }

    public org.drools.guvnor.client.rpc.PageResponse queryMetaData(org.drools.guvnor.client.rpc.QueryMetadataPageRequest p0) throws com.google.gwt.user.client.rpc.SerializationException {
        return serviceImplementation.queryMetaData( p0 );
    }

    public org.drools.guvnor.client.rpc.TableDataResult queryMetaData(org.drools.guvnor.client.rpc.MetaDataQuery[] p0,
                                                                      java.util.Date p1,
                                                                      java.util.Date p2,
                                                                      java.util.Date p3,
                                                                      java.util.Date p4,
                                                                      boolean p5,
                                                                      int p6,
                                                                      int p7) throws com.google.gwt.user.client.rpc.SerializationException {
        return serviceImplementation.queryMetaData( p0,
                p1,
                p2,
                p3,
                p4,
                p5,
                p6,
                p7 );
    }

    public org.drools.guvnor.client.rpc.PageResponse listUserPermissions(org.drools.guvnor.client.rpc.PageRequest p0) throws org.drools.guvnor.client.rpc.DetailedSerializationException {
        return serviceImplementation.listUserPermissions( p0 );
    }

    public java.util.Map listUserPermissions() throws org.drools.guvnor.client.rpc.DetailedSerializationException {
        return serviceImplementation.listUserPermissions();
    }

    public java.util.Map retrieveUserPermissions(java.lang.String p0) {
        return serviceImplementation.retrieveUserPermissions( p0 );
    }

    public void updateUserPermissions(java.lang.String p0,
                                      java.util.Map p1) {
        serviceImplementation.updateUserPermissions( p0,
                p1 );
    }

    public boolean isDoNotInstallSample() {
        Module[] modules = moduleService.listModules();

        return modules.length != 1 || serviceImplementation.isDoNotInstallSample();
    }

    public void setDoNotInstallSample() {
        serviceImplementation.setDoNotInstallSample();
    }
    
    public void deleteUser(java.lang.String p0) {
        serviceImplementation.deleteUser( p0 );
    }

    public void createUser(java.lang.String p0) {
        serviceImplementation.createUser( p0 );
    }

    public java.util.List subscribe() {
        return serviceImplementation.subscribe();
    }

    public org.drools.guvnor.client.rpc.TableDataResult loadInbox(java.lang.String p0) throws org.drools.guvnor.client.rpc.DetailedSerializationException {
        return serviceImplementation.loadInbox( p0 );
    }

    public org.drools.guvnor.client.rpc.PageResponse loadInbox(org.drools.guvnor.client.rpc.InboxPageRequest p0) throws org.drools.guvnor.client.rpc.DetailedSerializationException {
        return serviceImplementation.loadInbox( p0 );
    }

    public java.lang.String processTemplate(java.lang.String p0,
                                            java.util.Map p1) {
        return serviceImplementation.processTemplate( p0,
                p1 );
    }

    public java.util.Map loadSpringContextElementData() throws org.drools.guvnor.client.rpc.DetailedSerializationException {
        return serviceImplementation.loadSpringContextElementData();
    }

    public boolean doesAssetExistInModule(java.lang.String p0,
                                           java.lang.String p1) throws com.google.gwt.user.client.rpc.SerializationException {
        return serviceImplementation.doesAssetExistInModule( p0,
                                                     p1 );
    }

}
