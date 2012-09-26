package org.drools.guvnor.client.explorer.navigation.admin.widget;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.PopupPanel;
import org.drools.guvnor.client.common.FormStylePopup;
import org.drools.guvnor.client.common.SmallLabel;
import org.drools.guvnor.client.messages.ConstantsCore;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchPopup;

@WorkbenchPopup(identifier = "aboutPopup")
public class AboutPopup {

    @WorkbenchPartView
    public PopupPanel getView() {
        Frame aboutInfoFrame = new Frame("../AboutInfo.html"); // NON-NLS

        FormStylePopup aboutPop = new FormStylePopup();
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
