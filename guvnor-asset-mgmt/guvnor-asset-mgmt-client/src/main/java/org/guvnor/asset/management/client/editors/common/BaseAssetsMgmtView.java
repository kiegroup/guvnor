package org.guvnor.asset.management.client.editors.common;

import com.github.gwtbootstrap.client.ui.ListBox;

public interface BaseAssetsMgmtView {

    ListBox getChooseRepositoryBox();

    void displayNotification( String text );

}
