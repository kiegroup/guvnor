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

import org.guvnor.asset.management.social.AssetManagementEventTypes;
import org.guvnor.asset.management.social.AssetsPromotedEvent;
import org.kie.uberfire.social.activities.model.SocialActivitiesEvent;
import org.kie.uberfire.social.activities.model.SocialEventType;
import org.kie.uberfire.social.activities.model.SocialUser;
import org.kie.uberfire.social.activities.repository.SocialUserRepository;
import org.kie.uberfire.social.activities.service.SocialAdapter;
import org.kie.uberfire.social.activities.service.SocialCommandTypeFilter;

import javax.inject.Inject;

public class AssetsPromotedEventAdapter implements SocialAdapter<AssetsPromotedEvent> {

    @Inject
    private SocialUserRepository socialUserRepository;

    @Override
    public Class<AssetsPromotedEvent> eventToIntercept() {
        return AssetsPromotedEvent.class;
    }

    @Override
    public SocialEventType socialEventType() {
        return AssetManagementEventTypes.ASSETS_PROMOTED;
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
        AssetsPromotedEvent event = ( AssetsPromotedEvent ) object;

        return new SocialActivitiesEvent(
                socialUserRepository.systemUser(),
                AssetManagementEventTypes.ASSETS_PROMOTED.name(),
                new Date( event.getTimestamp() )
        )
        .withLink( event.getRepositoryAlias() != null ? event.getRepositoryAlias() : "<unknown>",
                event.getRootURI() != null ? event.getRootURI() : "<unknown>")
        .withAdicionalInfo( createAdditionalInfo( event ) );
    }

    @Override
    public List<SocialCommandTypeFilter> getTimelineFilters() {
        return new ArrayList<SocialCommandTypeFilter>();
    }

    @Override
    public List<String> getTimelineFiltersNames() {
        return new ArrayList<String>();
    }

    private String createAdditionalInfo( AssetsPromotedEvent event ) {

        StringBuilder info = new StringBuilder();

        info.append( "Process: " + event.getProcessName() + " promoted the following assets.\n" );
        info.append( "From repository: " + event.getRepositoryAlias() + "\n" );
        info.append( "origin branch: " + event.getSourceBranch() + " destination branch: " + event.getTargetBranch() + "\n\n" );

        List<String> assets = event.getAssets();
        if ( assets != null ) {
            for ( String asset : assets ) {
                info.append( asset + "\n" );
            }
        }

        return info.toString();
    }
}
