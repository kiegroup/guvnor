package org.kie.guvnor.explorer.model;

import org.jboss.errai.common.client.api.annotations.Portable;

/**
 * Types of Item shown in the Explorer widget. Members ordinal controls natural sort order of items.
 */
@Portable
public enum ItemType {
    PARENT_FOLDER,
    REPOSITORY,
    PROJECT,
    FOLDER,
    FILE
}
