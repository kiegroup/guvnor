package org.kie.guvnor.commons.ui.client.handlers;

import com.google.gwt.user.client.ui.IsWidget;
import org.uberfire.backend.vfs.Path;

/**
 * Definition of Handler to support creation of new resources
 */
public interface NewResourceHandler {

    /**
     * The file type of the new resource
     * @return
     */
    public String getFileType();

    /**
     * A description of the new resource type
     * @return
     */
    public String getDescription();

    /**
     * An icon representing the new resource type
     * @return
     */
    public IsWidget getIcon();

    /**
     * An entry-point for the creation of the new resource
     * @param path
     */
    public void create( final Path path );

    /**
     * Return a Widget that the NewResourceHandler can use to gather additional parameters for the new resource
     * @return null if no extension is provided
     */
    public IsWidget getExtension();

    /**
     * Provide NewResourceHandlers with the ability to validate additional parameters before the creation of the new resource
     * @return true if validation is successful
     */
    public boolean validate();

}
