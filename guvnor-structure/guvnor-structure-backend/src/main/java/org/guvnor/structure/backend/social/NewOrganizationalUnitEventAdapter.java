package org.guvnor.structure.backend.social;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.guvnor.structure.organizationalunit.NewOrganizationalUnitEvent;
import org.guvnor.structure.social.OrganizationalUnitEventType;
import org.kie.uberfire.social.activities.model.SocialActivitiesEvent;
import org.kie.uberfire.social.activities.model.SocialEventType;
import org.kie.uberfire.social.activities.repository.SocialUserRepository;
import org.kie.uberfire.social.activities.service.SocialAdapter;
import org.kie.uberfire.social.activities.service.SocialCommandTypeFilter;

@ApplicationScoped
public class NewOrganizationalUnitEventAdapter
        implements SocialAdapter<NewOrganizationalUnitEvent> {

    @Inject
    private SocialUserRepository socialUserRepository;

    @Override
    public Class<NewOrganizationalUnitEvent> eventToIntercept() {
        return NewOrganizationalUnitEvent.class;
    }

    @Override
    public SocialEventType socialEventType() {
        return OrganizationalUnitEventType.NEW_ORGANIZATIONAL_UNIT;
    }

    @Override
    public boolean shouldInterceptThisEvent( Object event ) {
        return event.getClass().getSimpleName().equals( eventToIntercept().getSimpleName() );
    }

    @Override
    public SocialActivitiesEvent toSocial( Object object ) {
        NewOrganizationalUnitEvent event = ( NewOrganizationalUnitEvent ) object;

        return new SocialActivitiesEvent(
                socialUserRepository.findSocialUser( event.getSessionInfo().getIdentity().getIdentifier() ),
                socialEventType().name(),
                new Date()
        )
        .withDescription( event.getOrganizationalUnit().getName() )
        .withLink( event.getOrganizationalUnit().getName(), event.getOrganizationalUnit().getName(), SocialActivitiesEvent.LINK_TYPE.CUSTOM )
        .withAdicionalInfo( getAdditionalInfo( event ) )
        .withParam( "param1", "el valor 1" )
        .withParam( "param2", "el valor 2" );
    }

    @Override
    public List<SocialCommandTypeFilter> getTimelineFilters() {
        return new ArrayList<SocialCommandTypeFilter>();
    }

    @Override
    public List<String> getTimelineFiltersNames() {
        return new ArrayList<String>();
    }

    private String getAdditionalInfo( NewOrganizationalUnitEvent event ) {
        return "added";
    }
}
