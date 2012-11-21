package org.drools.guvnor.client.asseteditor.drools.serviceconfig;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiBinderUtil;
import com.google.gwt.user.client.ui.Widget;

public class KBaseConfigPanel_KBaseConfigEditorBinderImpl implements UiBinder<com.google.gwt.user.client.ui.Widget, org.drools.guvnor.client.asseteditor.drools.serviceconfig.KBaseConfigPanel>, org.drools.guvnor.client.asseteditor.drools.serviceconfig.KBaseConfigPanel.KBaseConfigEditorBinder {

  public com.google.gwt.user.client.ui.Widget createAndBindUi(final org.drools.guvnor.client.asseteditor.drools.serviceconfig.KBaseConfigPanel owner) {

    org.drools.guvnor.client.asseteditor.drools.serviceconfig.KBaseConfigPanel_KBaseConfigEditorBinderImpl_GenBundle clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay = (org.drools.guvnor.client.asseteditor.drools.serviceconfig.KBaseConfigPanel_KBaseConfigEditorBinderImpl_GenBundle) GWT.create(org.drools.guvnor.client.asseteditor.drools.serviceconfig.KBaseConfigPanel_KBaseConfigEditorBinderImpl_GenBundle.class);
    org.drools.guvnor.client.messages.Constants i18n = (org.drools.guvnor.client.messages.Constants) GWT.create(org.drools.guvnor.client.messages.Constants.class);
    org.drools.guvnor.client.asseteditor.drools.serviceconfig.KBaseConfigPanel_KBaseConfigEditorBinderImpl_GenCss_style style = clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay.style();
    com.google.gwt.user.client.ui.Label f_Label3 = (com.google.gwt.user.client.ui.Label) GWT.create(com.google.gwt.user.client.ui.Label.class);
    com.google.gwt.user.client.ui.Button btnAssetResource = (com.google.gwt.user.client.ui.Button) GWT.create(com.google.gwt.user.client.ui.Button.class);
    com.google.gwt.user.client.ui.Button btnRemoveSelected = (com.google.gwt.user.client.ui.Button) GWT.create(com.google.gwt.user.client.ui.Button.class);
    com.google.gwt.user.client.ui.Button btnRename = (com.google.gwt.user.client.ui.Button) GWT.create(com.google.gwt.user.client.ui.Button.class);
    com.google.gwt.user.client.ui.Button btnAdvancedOptions = (com.google.gwt.user.client.ui.Button) GWT.create(com.google.gwt.user.client.ui.Button.class);
    com.google.gwt.user.client.ui.VerticalPanel f_VerticalPanel2 = (com.google.gwt.user.client.ui.VerticalPanel) GWT.create(com.google.gwt.user.client.ui.VerticalPanel.class);
    com.google.gwt.user.client.ui.Label f_Label5 = (com.google.gwt.user.client.ui.Label) GWT.create(com.google.gwt.user.client.ui.Label.class);
    com.google.gwt.user.client.ui.Tree resourceTree = (com.google.gwt.user.client.ui.Tree) GWT.create(com.google.gwt.user.client.ui.Tree.class);
    com.google.gwt.user.client.ui.ScrollPanel f_ScrollPanel6 = (com.google.gwt.user.client.ui.ScrollPanel) GWT.create(com.google.gwt.user.client.ui.ScrollPanel.class);
    com.google.gwt.user.client.ui.FlowPanel f_FlowPanel4 = (com.google.gwt.user.client.ui.FlowPanel) GWT.create(com.google.gwt.user.client.ui.FlowPanel.class);
    com.google.gwt.user.client.ui.Label f_Label9 = (com.google.gwt.user.client.ui.Label) GWT.create(com.google.gwt.user.client.ui.Label.class);
    com.google.gwt.user.cellview.client.CellTable cellTable = owner.cellTable;
    com.google.gwt.user.cellview.client.SimplePager pager = owner.pager;
    com.google.gwt.user.client.ui.FlowPanel f_FlowPanel8 = (com.google.gwt.user.client.ui.FlowPanel) GWT.create(com.google.gwt.user.client.ui.FlowPanel.class);
    com.google.gwt.user.client.ui.FlowPanel f_FlowPanel7 = (com.google.gwt.user.client.ui.FlowPanel) GWT.create(com.google.gwt.user.client.ui.FlowPanel.class);
    com.google.gwt.user.client.ui.FlowPanel f_FlowPanel1 = (com.google.gwt.user.client.ui.FlowPanel) GWT.create(com.google.gwt.user.client.ui.FlowPanel.class);

    f_Label3.setStyleName("" + style.palleteLabel() + "");
    f_Label3.setText("" + i18n.Pallete() + "");
    f_VerticalPanel2.add(f_Label3);
    btnAssetResource.setText("" + i18n.AddAssetEllipsis() + "");
    btnAssetResource.setWidth("150px");
    f_VerticalPanel2.add(btnAssetResource);
    btnRemoveSelected.setText("" + i18n.RemoveSelected() + "");
    btnRemoveSelected.setWidth("150px");
    f_VerticalPanel2.add(btnRemoveSelected);
    btnRename.setText("" + i18n.RenameKBaseEllipsis() + "");
    btnRename.setWidth("150px");
    f_VerticalPanel2.add(btnRename);
    btnAdvancedOptions.setText("" + i18n.AdvancedOptionsEllipsis() + "");
    btnAdvancedOptions.setWidth("150px");
    f_VerticalPanel2.add(btnAdvancedOptions);
    f_VerticalPanel2.setStyleName("" + style.horizontalChild() + "");
    f_VerticalPanel2.setWidth("20%");
    f_VerticalPanel2.setSpacing(10);
    f_FlowPanel1.add(f_VerticalPanel2);
    f_Label5.setStyleName("" + style.palleteLabel() + "");
    f_Label5.setText("" + i18n.Assets() + "");
    f_FlowPanel4.add(f_Label5);
    f_ScrollPanel6.add(resourceTree);
    f_ScrollPanel6.setHeight("95%");
    f_ScrollPanel6.setWidth("100%");
    f_FlowPanel4.add(f_ScrollPanel6);
    f_FlowPanel4.setStyleName("" + style.horizontalChild() + "");
    f_FlowPanel4.setHeight("92%");
    f_FlowPanel4.setWidth("40%");
    f_FlowPanel1.add(f_FlowPanel4);
    f_Label9.setStyleName("" + style.palleteLabel() + "");
    f_Label9.setText("" + i18n.Sessions() + "");
    f_FlowPanel8.add(f_Label9);
    cellTable.addStyleName("" + style.cellTable() + "");
    cellTable.setPageSize(9);
    f_FlowPanel8.add(cellTable);
    f_FlowPanel8.add(pager);
    f_FlowPanel8.setHeight("100%");
    f_FlowPanel8.setWidth("100%");
    f_FlowPanel7.add(f_FlowPanel8);
    f_FlowPanel7.setStyleName("" + style.horizontalChild() + "");
    f_FlowPanel7.setHeight("100%");
    f_FlowPanel7.setWidth("40%");
    f_FlowPanel1.add(f_FlowPanel7);
    f_FlowPanel1.setHeight("100%");
    f_FlowPanel1.setWidth("100%");



    final com.google.gwt.event.dom.client.ClickHandler handlerMethodWithNameVeryUnlikelyToCollideWithUserFieldNames1 = new com.google.gwt.event.dom.client.ClickHandler() {
      public void onClick(com.google.gwt.event.dom.client.ClickEvent event) {
        owner.removeSelectedElements(event);
      }
    };
    btnRemoveSelected.addClickHandler(handlerMethodWithNameVeryUnlikelyToCollideWithUserFieldNames1);

    final com.google.gwt.event.dom.client.ClickHandler handlerMethodWithNameVeryUnlikelyToCollideWithUserFieldNames2 = new com.google.gwt.event.dom.client.ClickHandler() {
      public void onClick(com.google.gwt.event.dom.client.ClickEvent event) {
        owner.addNewAssetResource(event);
      }
    };
    btnAssetResource.addClickHandler(handlerMethodWithNameVeryUnlikelyToCollideWithUserFieldNames2);

    final com.google.gwt.event.dom.client.ClickHandler handlerMethodWithNameVeryUnlikelyToCollideWithUserFieldNames3 = new com.google.gwt.event.dom.client.ClickHandler() {
      public void onClick(com.google.gwt.event.dom.client.ClickEvent event) {
        owner.doRename(event);
      }
    };
    btnRename.addClickHandler(handlerMethodWithNameVeryUnlikelyToCollideWithUserFieldNames3);

    final com.google.gwt.event.dom.client.ClickHandler handlerMethodWithNameVeryUnlikelyToCollideWithUserFieldNames4 = new com.google.gwt.event.dom.client.ClickHandler() {
      public void onClick(com.google.gwt.event.dom.client.ClickEvent event) {
        owner.advancedOptions(event);
      }
    };
    btnAdvancedOptions.addClickHandler(handlerMethodWithNameVeryUnlikelyToCollideWithUserFieldNames4);

    owner.btnAdvancedOptions = btnAdvancedOptions;
    owner.btnAssetResource = btnAssetResource;
    owner.btnRemoveSelected = btnRemoveSelected;
    owner.btnRename = btnRename;
    owner.resourceTree = resourceTree;
    clientBundleFieldNameUnlikelyToCollideWithUserSpecifiedFieldOkay.style().ensureInjected();

    return f_FlowPanel1;
  }
}
