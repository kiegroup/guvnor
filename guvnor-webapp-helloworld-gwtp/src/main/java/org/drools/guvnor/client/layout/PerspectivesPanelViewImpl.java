package org.drools.guvnor.client.layout;


import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewImpl;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;


public class PerspectivesPanelViewImpl extends ViewImpl implements PerspectivesPanelPresenter.MyView {

    interface PerspectivesPanelViewImplBinder
            extends
            UiBinder<Widget, PerspectivesPanelViewImpl> {
    }

    private static PerspectivesPanelViewImplBinder uiBinder  = GWT.create( PerspectivesPanelViewImplBinder.class );

/*    @UiField()
    LayoutPanel                                    perspective;*/


    @UiField
    ExplorerViewCenterPanel                        tabbedPanel;

    @UiField
    SpanElement                                    userName;

    @UiField
    HTMLPanel                                      titlePanel;
    
    @UiField
    HTMLPanel 									   footerPanel;

    public final Widget widget;
    private final PlaceManager placeManager;
    
    @Inject
    public PerspectivesPanelViewImpl(PlaceManager placeManager) {
        this.placeManager = placeManager;
        widget = uiBinder.createAndBindUi(this);
    }

    public void setUserName(String userName) {
        this.userName.setInnerText( userName );
    }

   
    @Override
    public void setInSlot(Object slot, Widget content) {
        if (slot == PerspectivesPanelPresenter.TYPE_MainContent) {
            if(content!=null) {
                PlaceRequest placeRequest = placeManager.getCurrentPlaceRequest();
                if (!tabbedPanel.contains(placeRequest)) {
                    String tabName = placeRequest.getParameter("tabName", "unknown");
                    tabbedPanel.addTab(tabName, content, placeRequest);
                } else {
                    tabbedPanel.show(placeRequest);
                }
            }
        }
    }
    
	@Override
	public Widget asWidget() {
		return widget;
	}
}
