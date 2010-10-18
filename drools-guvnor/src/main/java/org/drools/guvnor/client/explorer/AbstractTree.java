package org.drools.guvnor.client.explorer;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;

import org.drools.guvnor.client.ruleeditor.MultiViewRow;
import org.drools.guvnor.client.ruleeditor.NewAssetWizard;
import org.drools.guvnor.client.rulelist.EditItemEvent;
import org.drools.guvnor.client.util.TabOpener;
import org.drools.guvnor.client.util.Util;

public abstract class AbstractTree extends Composite
    implements
    SelectionHandler<TreeItem> {
    protected String                name;
    protected ImageResource         image;

    protected final Tree            mainTree;

    protected Map<TreeItem, String> itemWidgets = new HashMap<TreeItem, String>();

    /**
     * Constructor.
     * 
     * @param ExplorerViewCenterPanel
     *            the centertabbedPanel
     */
    public AbstractTree() {

        mainTree = getTree();

        initWidget( mainTree );
    }

    abstract Tree getTree();

    /**
     * Get a string representation of the header that includes an image and some
     * text.
     * @return the header as a string
     */
    public HTML getHeaderHTML() {
        return Util.getHeaderHTML( image,
                                   name );
    }

    public void refreshTree() {

    }

    protected void launchWizard(String format,
                                String title,
                                boolean showCats) {
        final TabOpener tabOpener = TabOpener.getInstance();

        NewAssetWizard pop = new NewAssetWizard( new EditItemEvent() {
                                                     public void open(String key) {
                                                         tabOpener.openAsset( key );
                                                     }

                                                     public void open(MultiViewRow[] rows) {
                                                         for ( MultiViewRow row : rows ) {
                                                             tabOpener.openAsset( row.uuid );
                                                         }
                                                     }
                                                 },
                                                 showCats,
                                                 format,
                                                 title );

        pop.show();
    }

}
