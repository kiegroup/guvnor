package org.drools.guvnor.client.explorer.navigation.admin.widget;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.IsWidget;
import org.kie.uberfirebootstrap.client.widgets.SmallLabel;
import org.drools.guvnor.client.messages.ConstantsCore;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.common.FormStyleLayout;

@WorkbenchScreen(identifier = "aboutPopup")
public class AboutPopup {

    @WorkbenchPartTitle
    public String getTitle() {
        return ConstantsCore.INSTANCE.About();
    }

    @WorkbenchPartView
    public IsWidget getView() {
        Frame aboutInfoFrame = new Frame("../AboutInfo.html"); // NON-NLS


        FormStyleLayout aboutPop = new FormStyleLayout();
        aboutPop.setWidth(600 + "px");
        aboutPop.setTitle(ConstantsCore.INSTANCE.About());
        String hhurl = GWT.getModuleBaseURL()
                + "webdav";
        aboutPop.addAttribute(ConstantsCore.INSTANCE.WebDAVURL()
                + ":",
                new SmallLabel("<b>"
                        + hhurl
                        + "</b>"));
        aboutPop.addAttribute(ConstantsCore.INSTANCE.Version()
                + ":",
                aboutInfoFrame);

        return aboutPop;
    }

}
