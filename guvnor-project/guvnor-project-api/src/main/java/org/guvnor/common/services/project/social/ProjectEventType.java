package org.guvnor.common.services.project.social;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.uberfire.social.activities.model.SocialEventType;

@Portable
public enum ProjectEventType implements SocialEventType {

    NEW_PROJECT

}
