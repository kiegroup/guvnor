package org.kie.guvnor.commons.service.metadata.model;

import java.util.Iterator;

import org.jboss.errai.common.client.api.annotations.Portable;

/**
 *
 */
@Portable
public class Categories
        extends CategoryItem
        implements Iterable<CategoryItem> {

    public Categories() {
    }

    public int size() {
        return getChildren().size();
    }

    public Iterator<CategoryItem> iterator() {
        return getChildren().iterator();
    }
}