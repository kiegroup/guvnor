package org.kie.guvnor.guided.dtable.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import org.kie.guvnor.guided.dtable.client.resources.css.CssResources;

/**
 * General Decision Table resources.
 */
public interface Resources
        extends
        ClientBundle {

    Resources INSTANCE = GWT.create( Resources.class );

    @Source("images/emptyArrow.png")
    ImageResource arrowSpacerIcon();

    @Source("images/icon-unmerge.png")
    ImageResource toggleUnmergeIcon();

    @Source("images/icon-merge.png")
    ImageResource toggleMergeIcon();

    @Source("css/DecisionTable.css")
    CssResources css();

};
