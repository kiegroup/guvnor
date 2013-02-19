package org.kie.guvnor.commons.ui.client.resources.i18n;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.Messages;

public interface TableConstants
        extends Messages {

    public static final TableConstants INSTANCE = GWT.create( TableConstants.class );

    String Open();

    String PleaseSelectAnItemToDelete();

    String refreshList();

    String openSelected();

    String fileURI();
}
