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

import org.guvnor.asset.management.backend.social.i18n.Constants;
import org.guvnor.asset.management.social.AssetManagementEventTypes;
import org.guvnor.asset.management.social.ProcessStartEvent;
import org.kie.uberfire.social.activities.model.SocialActivitiesEvent;
import org.kie.uberfire.social.activities.model.SocialEventType;
import org.kie.uberfire.social.activities.repository.SocialUserRepository;
import org.kie.uberfire.social.activities.service.SocialAdapter;
import org.kie.uberfire.social.activities.service.SocialCommandTypeFilter;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class ProcessStartEventAdapter implements SocialAdapter<ProcessStartEvent> {

    @Inject
    private SocialUserRepository socialUserRepository;

    @Inject
    private Constants constants;

    @Override
    public Class<ProcessStartEvent> eventToIntercept() {
        return ProcessStartEvent.class;
    }

    @Override
    public SocialEventType socialEventType() {
        return AssetManagementEventTypes.PROCESS_START;
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
        ProcessStartEvent event = ( ProcessStartEvent ) object;

        return new SocialActivitiesEvent(
                socialUserRepository.systemUser(),
                AssetManagementEventTypes.PROCESS_START.name(),
                new Date( event.getTimestamp() )
        )
                .withLink( event.getRepositoryAlias() != null ? event.getRepositoryAlias() : "<unknown>",
                        event.getRootURI() != null ? event.getRootURI() : "<unknown>" )
                .withAdicionalInfo( getAdditionalInfo( event.getProcessName(), event.getRepositoryAlias(), event.getParams() ) );
    }

    @Override
    public List<SocialCommandTypeFilter> getTimelineFilters() {
        return new ArrayList<SocialCommandTypeFilter>();
    }

    @Override
    public List<String> getTimelineFiltersNames() {
        return new ArrayList<String>();
    }

    private String getAdditionalInfo( String process, String repo, Map<String, String> params ) {
        if ( Constants.CONFIGURE_REPOSITORY.equals( process ) ) {
            return constants.configure_repository_start( repo );
        }

        if ( Constants.PROMOTE_ASSETS.equals( process ) ) {
            return constants.promote_assets_start( repo );
        }

        if ( Constants.BUILD_PROJECT.equals( process ) ) {
            return constants.build_project_start( params.get("project"), params.get( "branch" ), repo );
        }

        if ( Constants.RELEASE_PROJECT.equals( process ) ) {
            return constants.release_project_start( repo );
        }

        return "";
    }
}
