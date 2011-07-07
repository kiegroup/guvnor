package org.drools.guvnor.client.explorer;

import com.google.gwt.core.client.GWT;
import org.drools.guvnor.client.common.LoadingPopup;
import org.drools.guvnor.client.messages.Constants;

public class ModuleEditorActivityViewImpl implements ModuleEditorActivityView {

    private Constants constants = GWT.create( Constants.class );

    public void showLoadingPackageInformationMessage() {
        LoadingPopup.showMessage( constants.LoadingPackageInformation() );
    }
}
