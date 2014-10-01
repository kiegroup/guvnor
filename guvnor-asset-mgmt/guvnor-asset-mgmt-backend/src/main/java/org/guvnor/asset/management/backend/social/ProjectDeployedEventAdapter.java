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
import org.guvnor.asset.management.social.ProjectDeployedEvent;
import org.kie.uberfire.social.activities.model.SocialActivitiesEvent;
import org.kie.uberfire.social.activities.model.SocialEventType;
import org.kie.uberfire.social.activities.repository.SocialUserRepository;
import org.kie.uberfire.social.activities.service.SocialAdapter;
import org.kie.uberfire.social.activities.service.SocialCommandTypeFilter;

public class ProjectDeployedEventAdapter implements SocialAdapter<ProjectDeployedEvent> {

    @Inject
    private SocialUserRepository socialUserRepository;

    @Inject
    private Constants constants;

    @Override
    public Class<ProjectDeployedEvent> eventToIntercept() {
        return ProjectDeployedEvent.class;
    }

    @Override
    public SocialEventType socialEventType() {
        return AssetManagementEventTypes.PROJECT_DEPLOYED;
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
        ProjectDeployedEvent event = ( ProjectDeployedEvent ) object;

        return new SocialActivitiesEvent(
                socialUserRepository.systemUser(),
                AssetManagementEventTypes.PROJECT_DEPLOYED.name(),
                new Date( event.getTimestamp() )
        )
                .withLink( event.getRepositoryAlias() != null ? event.getRepositoryAlias() : "<unknown>",
                        event.getRootURI() != null ? event.getRootURI() : "<unknown>" )
                .withAdicionalInfo( getAdditionalInfo( event ) );
    }

    @Override
    public List<SocialCommandTypeFilter> getTimelineFilters() {
        return new ArrayList<SocialCommandTypeFilter>();
    }

    @Override
    public List<String> getTimelineFiltersNames() {
        return new ArrayList<String>();
    }

    private String getAdditionalInfo( ProjectDeployedEvent event ) {

        StringBuilder info = new StringBuilder();

        if ( event.getDeployType() == ProjectDeployedEvent.DeployType.MAVEN ) {
            if ( !event.hasErrors() ) {
                info.append( constants.build_project_deploy_maven_success( event.getProjectName() ) );
            } else {
                info.append( constants.build_project_deploy_maven_failed( event.getProjectName() ) );
            }
        } else if ( event.getDeployType() == ProjectDeployedEvent.DeployType.RUNTIME ) {
            //TODO COMPLETE this info.
        }

        return info.toString();
    }
}