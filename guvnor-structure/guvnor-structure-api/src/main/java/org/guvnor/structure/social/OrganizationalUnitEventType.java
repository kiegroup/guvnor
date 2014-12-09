package org.guvnor.structure.social;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.uberfire.social.activities.model.SocialEventType;

@Portable
public enum OrganizationalUnitEventType implements SocialEventType {

    NEW_ORGANIZATIONAL_UNIT,
    REPO_ADDED_TO_ORGANIZATIONAL_UNIT,
    REPO_REMOVED_FROM_ORGANIZATIONAL_UNIT,
    ORGANIZATIONAL_UNIT_UPDATED
}
