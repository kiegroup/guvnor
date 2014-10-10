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

package org.guvnor.asset.management.backend.social;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.inject.Inject;

import org.guvnor.asset.management.backend.social.i18n.Constants;
import org.guvnor.asset.management.social.AssetManagementEventTypes;
import org.guvnor.asset.management.social.ProcessEndEvent;
import org.kie.uberfire.social.activities.model.SocialActivitiesEvent;
import org.kie.uberfire.social.activities.model.SocialEventType;
import org.kie.uberfire.social.activities.repository.SocialUserRepository;
import org.kie.uberfire.social.activities.service.SocialAdapter;
import org.kie.uberfire.social.activities.service.SocialCommandTypeFilter;

public class ProcessEndEventAdapter implements SocialAdapter<ProcessEndEvent> {

    @Inject
    private SocialUserRepository socialUserRepository;

    @Inject
    private Constants constants;

    @Override
    public Class<ProcessEndEvent> eventToIntercept() {
        return ProcessEndEvent.class;
    }

    @Override
    public SocialEventType socialEventType() {
        return AssetManagementEventTypes.PROCESS_END;
    }

    @Override
    public boolean shouldInterceptThisEvent( Object event ) {
        if ( event.getClass().getSimpleName().equals( eventToIntercept().getSimpleName() ) ) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public SocialActivitiesEvent toSocial( Object object ) {
        ProcessEndEvent event = ( ProcessEndEvent ) object;

        return new SocialActivitiesEvent(
                socialUserRepository.systemUser(),
                AssetManagementEventTypes.PROCESS_END.name(),
                new Date( event.getTimestamp() )
        )
                .withLink( event.getRepositoryAlias() != null ? event.getRepositoryAlias() : "<unknown>",
                        event.getRootURI() != null ? event.getRootURI() : "<unknown>" )
                .withAdicionalInfo( getAdditionalInfo( event.getProcessName(), event.getRepositoryAlias(), event ) );

    }

    @Override
    public List<SocialCommandTypeFilter> getTimelineFilters() {
        return new ArrayList<SocialCommandTypeFilter>();
    }

    @Override
    public List<String> getTimelineFiltersNames() {
        return new ArrayList<String>();
    }

    private String getAdditionalInfo( String process, String repo, ProcessEndEvent event ) {

        if ( Constants.CONFIGURE_REPOSITORY.equals( process ) ) {
            return constants.configure_repository_end( repo );
        }

        if ( Constants.PROMOTE_ASSETS.equals( process ) ) {
            return constants.promote_assets_end( repo );
        }

        if ( Constants.BUILD_PROJECT.equals( process ) ) {

            String _BP_BuildOutcome = event.getParams().get( "BP_BuildOutcome" );
            String _BP_GAV = event.getParams().get( "BP_GAV" );
            String _BP_MavenDeployOutcome = event.getParams().get( "BP_MavenDeployOutcome" );
            String _BP_ExecServerURL = event.getParams().get( "BP_ExecServerURL" );
            String _BP_Username = event.getParams().get( "BP_Username" );
            String _BP_DeployToRuntime = event.getParams().get( "BP_DeployToRuntime" );
            boolean deploySelected = _BP_DeployToRuntime != null && Boolean.parseBoolean( _BP_DeployToRuntime );

            String project = event.getParams().get( "project" );
            String branch = event.getParams().get( "branch" );

            if ( "FAILURE".equals( _BP_MavenDeployOutcome ) ) {
                return constants.build_project_end_with_errors( project );
            }

            if ( "SUCCESSFUL".equals( _BP_MavenDeployOutcome ) && deploySelected ) {
                return constants.build_project_deploy_runtime_success( project );
            }

            if ( "SUCCESSFUL".equals( _BP_MavenDeployOutcome ) && !deploySelected ) {
                return constants.build_project_deploy_runtime_skipped( project );
            }

            //unexpected case
            return constants.build_project_end( project );

        }

        if ( Constants.RELEASE_PROJECT.equals( process ) ) {
            return constants.release_project_end( repo );
        }

        return "";
    }
}