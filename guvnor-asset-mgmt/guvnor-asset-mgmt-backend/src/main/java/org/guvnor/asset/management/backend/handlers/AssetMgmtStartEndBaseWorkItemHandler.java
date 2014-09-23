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

import javax.enterprise.inject.spi.BeanManager;

import org.guvnor.asset.management.backend.utils.CDIUtils;
import org.guvnor.asset.management.social.ProcessEndEvent;
import org.guvnor.asset.management.social.ProcessStartEvent;
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

        String _ProcessName = ( String ) workItem.getParameter( "ProcessName" );
        String _Owner = ( String ) workItem.getParameter( "Owner" );

        String _CB_RepositoryName;
        String _CB_SourceBranchName;
        String _CB_DevBranchName;
        String _CB_RelBranchName;
        String _CB_Version;

        String _BP_ProjectURI;
        String _BP_BranchName;
        String _BP_Username;
        String _BP_Password;
        String _BP_ExecServerURL;
        String _BP_DeployToRuntime;

        String _PC_GitRepositoryName;
        String _PC_SourceBranchName;
        String _PC_TargetBranchName;

        BeanManager beanManager = null;

        if ( isStart() ) {
            logger.debug( "Start process: " + _ProcessName + "  " + new java.util.Date() );
            System.out.println( "Start process: " + _ProcessName + "  " + new java.util.Date() );
        } else {
            logger.debug( "End process: " + _ProcessName + "  " + new java.util.Date() );
            System.out.println( "End process: " + _ProcessName + "  " + new java.util.Date() );
        }

        try {
            beanManager = CDIUtils.lookUpBeanManager( null );
        } catch ( Exception e ) {
            logger.debug( "BeanManager lookup error.", e );
        }

        if ( beanManager != null && "ConfigureRepository".equals( _ProcessName ) ) {
            _CB_RepositoryName = ( String ) workItem.getParameter( "CB_RepositoryName" );
            _CB_SourceBranchName = ( String ) workItem.getParameter( "CB_SourceBranchName" );
            _CB_DevBranchName = ( String ) workItem.getParameter( "CB_DevBranchName" );
            _CB_RelBranchName = ( String ) workItem.getParameter( "CB_RelBranchName" );
            _CB_Version = ( String ) workItem.getParameter( "CB_Version" );

            if ( isStart() ) {
                ProcessStartEvent event = new ProcessStartEvent( _ProcessName, _CB_RepositoryName, null, System.currentTimeMillis() );
                beanManager.fireEvent( event );
            } else {
                ProcessEndEvent event = new ProcessEndEvent( _ProcessName, _CB_RepositoryName, null, System.currentTimeMillis() );
                beanManager.fireEvent( event );
            }
        }

        //TODO, configure the other processes.

        if ( manager != null ) {
            manager.completeWorkItem( workItem.getId(), null );
        }
    }

    @Override public void abortWorkItem( WorkItem workItem, WorkItemManager manager ) {
        //do nothing
    }

    protected abstract boolean isStart();
}
