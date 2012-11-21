package org.drools.guvnor.client.widgets.drools.workitems;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiBinderUtil;
import com.google.gwt.user.client.ui.HorizontalPanel;

public class WorkItemEnumParameterWidget_WorkItemEnumParameterWidgetBinderImpl implements UiBinder<com.google.gwt.user.client.ui.HorizontalPanel, org.drools.guvnor.client.widgets.drools.workitems.WorkItemEnumParameterWidget>, org.drools.guvnor.client.widgets.drools.workitems.WorkItemEnumParameterWidget.WorkItemEnumParameterWidgetBinder {

  public com.google.gwt.user.client.ui.HorizontalPanel createAndBindUi(final org.drools.guvnor.client.widgets.drools.workitems.WorkItemEnumParameterWidget owner) {

    org.drools.guvnor.client.widgets.drools.workitems.WorkItemEnumParameterWidget_WorkItemEnumParameterWidgetBinderImpl_GenBundle clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay = (org.drools.guvnor.client.widgets.drools.workitems.WorkItemEnumParameterWidget_WorkItemEnumParameterWidgetBinderImpl_GenBundle) GWT.create(org.drools.guvnor.client.widgets.drools.workitems.WorkItemEnumParameterWidget_WorkItemEnumParameterWidgetBinderImpl_GenBundle.class);
    org.drools.guvnor.client.messages.Constants i18n = (org.drools.guvnor.client.messages.Constants) GWT.create(org.drools.guvnor.client.messages.Constants.class);
    org.drools.guvnor.client.resources.DroolsGuvnorResources resources = (org.drools.guvnor.client.resources.DroolsGuvnorResources) GWT.create(org.drools.guvnor.client.resources.DroolsGuvnorResources.class);
    com.google.gwt.user.client.ui.Label parameterName = (com.google.gwt.user.client.ui.Label) GWT.create(com.google.gwt.user.client.ui.Label.class);
    com.google.gwt.user.client.ui.ListBox parameterValues = (com.google.gwt.user.client.ui.ListBox) GWT.create(com.google.gwt.user.client.ui.ListBox.class);
    com.google.gwt.user.client.ui.HorizontalPanel f_HorizontalPanel1 = (com.google.gwt.user.client.ui.HorizontalPanel) GWT.create(com.google.gwt.user.client.ui.HorizontalPanel.class);

    parameterName.setStyleName("" + resources.droolsGuvnorCss().workItemParameter() + "");
    f_HorizontalPanel1.add(parameterName);
    parameterValues.setStyleName("" + resources.droolsGuvnorCss().workItemParameter() + "");
    f_HorizontalPanel1.add(parameterValues);
    f_HorizontalPanel1.setSpacing(2);



    final com.google.gwt.event.dom.client.ChangeHandler handlerMethodWithNameVeryUnlikelyToCollideWithUserFieldNames1 = new com.google.gwt.event.dom.client.ChangeHandler() {
      public void onChange(com.google.gwt.event.dom.client.ChangeEvent event) {
        owner.parameterValuesOnChange(event);
      }
    };
    parameterValues.addChangeHandler(handlerMethodWithNameVeryUnlikelyToCollideWithUserFieldNames1);

    owner.parameterName = parameterName;
    owner.parameterValues = parameterValues;

    return f_HorizontalPanel1;
  }
}
