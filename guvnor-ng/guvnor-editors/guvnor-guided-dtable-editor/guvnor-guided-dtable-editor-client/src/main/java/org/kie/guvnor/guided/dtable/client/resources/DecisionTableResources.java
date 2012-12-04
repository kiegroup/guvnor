package org.kie.guvnor.guided.dtable.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import org.kie.guvnor.commons.ui.client.resources.CollapseExpand;
import org.kie.guvnor.commons.ui.client.resources.ItemImages;
import org.kie.guvnor.decoratedgrid.client.resources.TableImageResources;

/**
 * Resources for the Decision Table.
 */
public interface DecisionTableResources
        extends
        ClientBundle {

    DecisionTableResources INSTANCE = GWT.create( DecisionTableResources.class );


    @Source("emptyArrow.png")
    ImageResource arrowSpacerIcon();

    TableImageResources tableImageResources();

    @Source("icon-unmerge.png")
    ImageResource toggleUnmergeIcon();

    @Source("icon-merge.png")
    ImageResource toggleMergeIcon();

    ItemImages itemImages();

    CollapseExpand collapseExpand();


};
