package org.drools.guvnor.client.explorer;

import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;

import org.drools.guvnor.client.common.Util;
import org.drools.guvnor.client.ruleeditor.MultiViewRow;
import org.drools.guvnor.client.ruleeditor.NewAssetWizard;
import org.drools.guvnor.client.rulelist.EditItemEvent;

public abstract class AbstractTree implements SelectionHandler<TreeItem> {

	protected String name;
	protected ImageResource image;

	protected ExplorerViewCenterPanel centertabbedPanel;
	protected Tree mainTree;

	/**
	 * Constructor.
	 * 
	 * @param ExplorerViewCenterPanel
	 *            the centertabbedPanel
	 */
	public AbstractTree(ExplorerViewCenterPanel centertabbedPanel) {
		this.centertabbedPanel = centertabbedPanel;
	}

	/**
	 * Get a string representation of the header that includes an image and some
	 * text.
	 * @return the header as a string
	 */
	public HTML getHeaderHTML() {
		return Util.getHeaderHTML(image, name);
	}

	protected void launchWizard(String format, String title, boolean showCats) {

		NewAssetWizard pop = new NewAssetWizard(new EditItemEvent() {
			public void open(String key) {
				centertabbedPanel.openAsset(key);
			}

			public void open(MultiViewRow[] rows) {
				for (MultiViewRow row : rows) {
					centertabbedPanel.openAsset(row.uuid);
				}
			}
		}, showCats, format, title);

		pop.show();
	}	
    
    public Tree getTree() {
    	return mainTree;
    }
}
