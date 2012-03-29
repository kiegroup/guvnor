package org.drools.guvnor.client.explorer;

import java.util.Map;

import org.drools.guvnor.client.common.Util;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.place.NameTokens;
import org.drools.guvnor.client.resources.Images;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;


public class KnowledgeTree extends AbstractTree {
    @Inject
    private static PlaceManager placeManager;
    
	private static Constants constants = GWT.create(Constants.class);
	private static Images images = (Images) GWT.create(Images.class);
    public static final String PACKAGE_ID = "Package";
	public static final String ADMIN_ID = "Admin";

	
    public KnowledgeTree() {
        this.name = constants.Browse();
        this.image = images.ruleAsset();

        mainTree.setAnimationEnabled(true);
        setupTree(mainTree, itemWidgets);
        mainTree.addSelectionHandler(this);
    }

    @Override
    protected Tree createTree() {
        return new Tree();
    }

    public void refreshTree() {
        mainTree.clear();
        itemWidgets.clear();
        setupTree(mainTree, itemWidgets);
    }

	public static void setupTree(Tree tree, Map<TreeItem, String> itemWidgets) {
		TreeItem find = tree.addItem(Util.getHeader(images.packageImage(),
				constants.Package()));
		itemWidgets.put(find, PACKAGE_ID);
		
		TreeItem admin = tree.addItem(Util.getHeader(images.information(),
				"admin"));
		itemWidgets.put(admin, ADMIN_ID);
	}

    public void onSelection(SelectionEvent<TreeItem> event) {
        TreeItem item = event.getSelectedItem();
        String widgetID = itemWidgets.get(item);

        if (widgetID != null) {
            if (widgetID.equals(PACKAGE_ID)) {
                PlaceRequest placeRequest = new PlaceRequest(NameTokens.packageEditor);
                placeRequest = placeRequest.with("tabName", constants.Package());
                placeManager.revealPlace(placeRequest);
            } if (widgetID.equals(ADMIN_ID)) {
                PlaceRequest placeRequest = new PlaceRequest(NameTokens.helloWorld);
                placeRequest = placeRequest.with("tabName", "Hello World!");
                placeManager.revealPlace(placeRequest);
            } 
        }
    }
}
