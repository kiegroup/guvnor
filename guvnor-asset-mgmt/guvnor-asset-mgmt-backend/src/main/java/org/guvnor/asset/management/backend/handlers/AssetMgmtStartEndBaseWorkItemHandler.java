/*
 * Copyright 2014 JBoss Inc
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

package org.guvnor.asset.management.backend.handlers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.enterprise.inject.spi.BeanManager;

import org.guvnor.asset.management.backend.utils.CDIUtils;
import org.guvnor.asset.management.backend.utils.DataUtils;
import org.guvnor.asset.management.social.ProcessEndEvent;
import org.guvnor.asset.management.social.ProcessStartEvent;
import org.guvnor.messageconsole.events.MessageUtils;
import org.guvnor.messageconsole.events.PublishBatchMessagesEvent;
import org.guvnor.messageconsole.events.SystemMessage;
import org.guvnor.structure.repositories.RepositoryInfo;
import org.guvnor.structure.repositories.RepositoryService;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AssetMgmtStartEndBaseWorkItemHandler
        implements WorkItemHandler {

    private static final Logger logger = LoggerFactory.getLogger( AssetMgmtStartWorkItemHandler.class );

    @Override
    public void executeWorkItem( WorkItem workItem, WorkItemManager manager ) {

        PublishBatchMessagesEvent publishMessage = new PublishBatchMessagesEvent();
        publishMessage.setCleanExisting( true );
        List<SystemMessage> messageList = new ArrayList<SystemMessage>();

        String _ProcessName = ( String ) workItem.getParameter( "ProcessName" );
        String _Owner = ( String ) workItem.getParameter( "Owner" );
        String user = "system";
        String repositoryURI = null;

        //ConfigureRepository variables
        String _CB_RepositoryName;
        /* don't remove.
        String _CB_SourceBranchName;
        String _CB_DevBranchName;
        String _CB_RelBranchName;
        String _CB_Version;
        */

        //PromoteAssets variables
        String _PA_GitRepositoryName;
        /* don't remove
        String _PA_SourceBranchName;
        String _PA_TargetBranchName;
        String _PA_CommitsToPromote;
        Boolean _PA_Reviewed;
        Boolean _PA_RequiresReview;
        String _PA_ListOfCommits;
        String _PA_ListOfFiles;
        Map _PA_CommitsPerFile;
        List _PA_Commits;
        */

        //BuildProcess variables
        String _BP_ProjectURI;
        String _BP_BranchName;
        String _BP_BuildOutcome;
        List _BP_Errors;
        List _BP_Warnings;
        List _BP_Infos;
        String _BP_GAV;
        String _BP_MavenDeployOutcome;
        String _BP_ExecServerURL;
        String _BP_Username;
        String _BP_Password;
        Boolean _BP_DeployToRuntime;
        Exception _BP_Exception;
        String _BP_DeployOutcome;
        String _BP_Initiator;


        //ReleaseProject variables
        String _RP_RepositoryName;
        String _RP_ToReleaseVersion;
        String _RP_ProjectURI;

        /* don't remove
        String _RP_Version;
        Boolean _RP_ValidForRelease;
        String _RP_DevBranchName;
        String _RP_RelBranchName;
        String _RP_ToReleaseDevBranch;
        String _RP_ToReleaseRelBranch;

        Exception _RP_Exception;
        */

        BeanManager beanManager = null;
        RepositoryService repositoryService = null;
        RepositoryInfo repositoryInfo = null;


        if ( isStart() ) {
            logger.debug( "Start assets management process: " + _ProcessName + "  " + new java.util.Date() );
            System.out.println( "Start assets management process: " + _ProcessName + "  " + new java.util.Date() );
        } else {
            logger.debug( "End assets management process: " + _ProcessName + "  " + new java.util.Date() );
            System.out.println( "End assets management process: " + _ProcessName + "  " + new java.util.Date() );
        }

        try {
            beanManager = CDIUtils.lookUpBeanManager( null );
            repositoryService = CDIUtils.createBean( RepositoryService.class, beanManager );

        } catch ( Exception e ) {
            logger.debug( "BeanManager lookup error.", e );
        }

        if ( beanManager != null && "ConfigureRepository".equals( _ProcessName ) ) {

            _CB_RepositoryName = ( String ) workItem.getParameter( "CB_RepositoryName" );
            repositoryURI = DataUtils.readRepositoryURI( repositoryService, _CB_RepositoryName );

            if ( isStart() ) {
                ProcessStartEvent event = new ProcessStartEvent( _ProcessName, _CB_RepositoryName, repositoryURI, user, System.currentTimeMillis() );
                beanManager.fireEvent( event );
            } else {
                ProcessEndEvent event = new ProcessEndEvent( _ProcessName, _CB_RepositoryName, repositoryURI, user, System.currentTimeMillis() );
                beanManager.fireEvent( event );
            }

        } else if ( beanManager != null && "PromoteAssets".equals( _ProcessName ) ) {

            _PA_GitRepositoryName = ( String ) workItem.getParameter( "PA_GitRepositoryName" );
            repositoryURI = DataUtils.readRepositoryURI( repositoryService, _PA_GitRepositoryName );

            if ( isStart() ) {
                ProcessStartEvent event = new ProcessStartEvent( _ProcessName, _PA_GitRepositoryName, repositoryURI, user, System.currentTimeMillis() );
                beanManager.fireEvent( event );
            } else {
                ProcessEndEvent event = new ProcessEndEvent( _ProcessName, _PA_GitRepositoryName, repositoryURI, user, System.currentTimeMillis() );
                beanManager.fireEvent( event );
            }

        } else if ( beanManager != null && "BuildProject".equals( _ProcessName ) ) {

            _BP_ProjectURI = ( String ) workItem.getParameter( "BP_ProjectURI" );
            _BP_BranchName = ( String ) workItem.getParameter( "BP_BranchName" );

            String _BP_Repository = null;
            String _BP_Project = null;
            if ( _BP_ProjectURI != null && _BP_ProjectURI.indexOf( "/" ) > 0 ) {
                _BP_Repository = _BP_ProjectURI.substring( 0, _BP_ProjectURI.indexOf( "/" ) );
                _BP_Project = _BP_ProjectURI.substring( _BP_ProjectURI.indexOf( "/" )+1, _BP_ProjectURI.length() );

                repositoryURI = DataUtils.readRepositoryURI( repositoryService, _BP_Repository );
            }

            if ( isStart() ) {
                ProcessStartEvent event = new ProcessStartEvent( _ProcessName, _BP_Repository, repositoryURI, user, System.currentTimeMillis() );
                event.addParam( "project", _BP_Project  );
                event.addParam( "branch", _BP_BranchName );

                beanManager.fireEvent( event );
            } else {

                _BP_BuildOutcome = ( String ) workItem.getParameter( "BP_BuildOutcome" );
                _BP_Errors = ( List ) workItem.getParameter( "BP_Errors" );
                _BP_Warnings = ( List ) workItem.getParameter( "BP_Warnings" );
                _BP_Infos = ( List ) workItem.getParameter( "BP_Infos" );
                _BP_GAV = ( String ) workItem.getParameter( "BP_GAV" );
                _BP_MavenDeployOutcome = ( String ) workItem.getParameter( "BP_MavenDeployOutcome" );
                _BP_ExecServerURL = ( String ) workItem.getParameter( "BP_ExecServerURL" );
                _BP_Username = ( String ) workItem.getParameter( "BP_Username" );
                _BP_DeployToRuntime = Boolean.TRUE.equals( workItem.getParameter( "BP_DeployToRuntime" ) );
                _BP_Exception = ( Exception ) workItem.getParameter( "BP_Exception" );
                _BP_DeployOutcome = ( String ) workItem.getParameter( "BP_DeployOutcome" );
                _BP_Initiator = ( String ) workItem.getParameter( "BP_Initiator" );

                ProcessEndEvent event = new ProcessEndEvent( _ProcessName, _BP_Repository, repositoryURI, user, System.currentTimeMillis() );
                event.addParam( "BP_BuildOutcome", _BP_BuildOutcome  );
                event.addParam( "BP_GAV", _BP_GAV );
                event.addParam( "BP_MavenDeployOutcome", _BP_MavenDeployOutcome );
                event.addParam( "BP_ExecServerURL", _BP_ExecServerURL );
                event.addParam( "BP_Username", _BP_Username );
                event.addParam( "BP_DeployToRuntime", _BP_DeployToRuntime.toString() );
                event.addParam( "project", _BP_Project  );
                event.addParam( "branch", _BP_BranchName );

                beanManager.fireEvent( event );

                StringBuffer message = new StringBuffer();
                message.append("Build of project '" + _BP_Project+"' (requested by "+_BP_Initiator + ") completed.\n");
                message.append(" Build: " + _BP_BuildOutcome);
                message.append(" Maven: " + _BP_MavenDeployOutcome);
                if (_BP_DeployOutcome != null) {
                    message.append(" Deploy: " + _BP_DeployOutcome);
                }

                SystemMessage infoMsg = new SystemMessage();
                infoMsg.setLevel(SystemMessage.Level.INFO);
                infoMsg.setText(message.toString());
                infoMsg.setMessageType( MessageUtils.BUILD_SYSTEM_MESSAGE );

                messageList.add(infoMsg);
                publishMessage.setMessagesToPublish(messageList);

                beanManager.fireEvent(publishMessage);

            }

        } else if ( beanManager != null && "ReleaseProject".equals( _ProcessName ) ) {

            _RP_ProjectURI = ( String ) workItem.getParameter( "RP_ProjectURI" );
            _RP_ToReleaseVersion = ( String ) workItem.getParameter( "RP_ToReleaseVersion" );
            String _RP_Repository;

            if ( _RP_ProjectURI != null && _RP_ProjectURI.indexOf( "/" ) > 0 ) {
                //when the release process finishes the ProjectURI has the uri of the last processed
                //project e.g. repo1/project1, so we need to extract the repository name.
                _RP_Repository = _RP_ProjectURI.substring( 0, _RP_ProjectURI.indexOf( "/" ) );
            } else {
                //when the release process starts, the ProjectURI has the repo name
                _RP_Repository = _RP_ProjectURI;
            }

            repositoryURI = DataUtils.readRepositoryURI( repositoryService, _RP_Repository );

            if ( isStart() ) {
                ProcessStartEvent event = new ProcessStartEvent( _ProcessName, _RP_Repository, repositoryURI, user, System.currentTimeMillis() );
                event.addParam( "version", _RP_ToReleaseVersion );

                beanManager.fireEvent( event );
            } else {
                ProcessEndEvent event = new ProcessEndEvent( _ProcessName, _RP_Repository, repositoryURI, user, System.currentTimeMillis() );
                event.addParam( "version", _RP_ToReleaseVersion );

                beanManager.fireEvent( event );
            }
        }


        if ( manager != null ) {
            manager.completeWorkItem( workItem.getId(), null );
        }
    }

    @Override public void abortWorkItem( WorkItem workItem, WorkItemManager manager ) {
        //do nothing
    }

    protected abstract boolean isStart();
}
