package org.drools.guvnor.client.explorer.navigation.qa.testscenarios;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiBinderUtil;
import com.google.gwt.user.client.ui.Widget;

public class FactFieldSelector_FactFieldSelectorUiBinderImpl implements UiBinder<com.google.gwt.user.client.ui.Widget, org.drools.guvnor.client.explorer.navigation.qa.testscenarios.FactFieldSelector>, org.drools.guvnor.client.explorer.navigation.qa.testscenarios.FactFieldSelector.FactFieldSelectorUiBinder {

  public com.google.gwt.user.client.ui.Widget createAndBindUi(final org.drools.guvnor.client.explorer.navigation.qa.testscenarios.FactFieldSelector owner) {

    org.drools.guvnor.client.explorer.navigation.qa.testscenarios.FactFieldSelector_FactFieldSelectorUiBinderImpl_GenBundle clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay = (org.drools.guvnor.client.explorer.navigation.qa.testscenarios.FactFieldSelector_FactFieldSelectorUiBinderImpl_GenBundle) GWT.create(org.drools.guvnor.client.explorer.navigation.qa.testscenarios.FactFieldSelector_FactFieldSelectorUiBinderImpl_GenBundle.class);
    org.drools.guvnor.client.messages.Constants i18n = (org.drools.guvnor.client.messages.Constants) GWT.create(org.drools.guvnor.client.messages.Constants.class);
    com.google.gwt.user.client.ui.ListBox fieldsListBox = (com.google.gwt.user.client.ui.ListBox) GWT.create(com.google.gwt.user.client.ui.ListBox.class);
    com.google.gwt.user.client.ui.Button ok = (com.google.gwt.user.client.ui.Button) GWT.create(com.google.gwt.user.client.ui.Button.class);
    com.google.gwt.user.client.ui.HorizontalPanel f_HorizontalPanel1 = (com.google.gwt.user.client.ui.HorizontalPanel) GWT.create(com.google.gwt.user.client.ui.HorizontalPanel.class);

    f_HorizontalPanel1.add(fieldsListBox);
    ok.setText("" + i18n.OK() + "");
    f_HorizontalPanel1.add(ok);



    final com.google.gwt.event.dom.client.ClickHandler handlerMethodWithNameVeryUnlikelyToCollideWithUserFieldNames1 = new com.google.gwt.event.dom.client.ClickHandler() {
      public void onClick(com.google.gwt.event.dom.client.ClickEvent event) {
        owner.handleClick(event);
      }
    };
    ok.addClickHandler(handlerMethodWithNameVeryUnlikelyToCollideWithUserFieldNames1);

    owner.fieldsListBox = fieldsListBox;
    owner.ok = ok;

    return f_HorizontalPanel1;
  }
}
