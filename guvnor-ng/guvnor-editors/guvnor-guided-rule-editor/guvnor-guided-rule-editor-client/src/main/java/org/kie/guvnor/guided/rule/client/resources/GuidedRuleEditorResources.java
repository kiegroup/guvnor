package org.kie.guvnor.guided.rule.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import org.kie.guvnor.commons.ui.client.resources.CollapseExpand;
import org.kie.guvnor.commons.ui.client.resources.ItemImages;
import org.kie.guvnor.decoratedgrid.client.resources.TableImageResources;
import org.kie.guvnor.guided.rule.client.resources.css.GuidedRuleEditorCss;
import org.kie.guvnor.guided.rule.client.resources.images.GuidedRuleEditorImages;
import org.kie.guvnor.guided.rule.client.resources.images.GuidedRuleEditorImages508;

/**
 * Resources for the Guided Rule Editor
 */
public interface GuidedRuleEditorResources extends ClientBundle {

    GuidedRuleEditorResources INSTANCE = GWT.create( GuidedRuleEditorResources.class );

    TableImageResources tableImageResources();

    CollapseExpand collapseExpand();

    ItemImages itemImages();

    @Source("css/GuidedRuleEditor.css")
    GuidedRuleEditorCss css();

    GuidedRuleEditorImages images();

}
