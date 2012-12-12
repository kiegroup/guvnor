package org.kie.guvnor.commons.ui.client.handlers;

import java.util.List;

import com.google.gwt.user.client.ui.IsWidget;
import org.kie.commons.data.Pair;

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
     * @param name Name of the new resource including file extension
     */
    public void create( final String name );

    /**
     * Return a List of Widgets that the NewResourceHandler can use to gather additional parameters for the
     * new resource. The List is of Pairs, where each Pair consists of a String caption and IsWidget editor.
     * @return null if no extension is provided
     */
    public List<Pair<String, IsWidget>> getExtensions();

    /**
     * Provide NewResourceHandlers with the ability to validate additional parameters before the creation of the new resource
     * @return true if validation is successful
     */
    public boolean validate();

}
