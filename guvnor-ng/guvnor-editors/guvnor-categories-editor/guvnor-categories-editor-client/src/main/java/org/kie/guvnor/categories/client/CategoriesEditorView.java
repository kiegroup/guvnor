package org.kie.guvnor.categories.client;

import com.google.gwt.user.client.ui.IsWidget;
import org.kie.guvnor.services.metadata.model.Categories;

/**
 * Categories Editor View definition.
 */
public interface CategoriesEditorView extends IsWidget {

    void setContent( final Categories categories );

    Categories getContent();

    boolean isDirty();

    void setNotDirty();

    boolean confirmClose();

    void showBusyIndicator( final String message );

    void hideBusyIndicator();

}
