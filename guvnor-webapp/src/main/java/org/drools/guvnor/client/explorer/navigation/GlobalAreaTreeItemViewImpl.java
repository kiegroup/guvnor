package org.drools.guvnor.client.explorer.navigation;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.Widget;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.resources.Images;
import org.drools.guvnor.client.util.Util;

public class GlobalAreaTreeItemViewImpl implements GlobalAreaTreeItemView {

    private static Constants constants = GWT.create( Constants.class );
    private static Images images = GWT.create( Images.class );

    public Widget asWidget() {
        Tree tree = new Tree();
        tree.setStyleName( "guvnor-Tree" );
        tree.setAnimationEnabled( true );
        tree.addItem( Util.getHeader( images.packages(), constants.GlobalArea() ) );
        return tree;
    }
}
