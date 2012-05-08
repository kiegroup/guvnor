package org.drools.guvnor.client.layout;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.drools.guvnor.client.resources.Images;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.layout.client.Layout.Alignment;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InsertPanel;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;


public class SplitLayoutInsertPanel extends SplitLayoutPanel implements InsertPanel {
    private List<Widget>                         openedPanels         = new ArrayList<Widget>();

    DockLayoutPanel.Direction defaultDirection = DockLayoutPanel.Direction.WEST;

    public SplitLayoutInsertPanel() {
        super();
    }
    
    public SplitLayoutInsertPanel(DockLayoutPanel.Direction direction) {
        super();
        defaultDirection = direction;
    }   
    
    @Override
    public void add(Widget widget) {
        if(openedPanels.size() >0) {
            //Hack. may not be an efficient way to do. 
            this.clear();
            for(Widget w : openedPanels) {
                insert(w, defaultDirection,
                    200,
                    null);                  
            }
          
        }
        openedPanels.add(widget);
        
        super.add(widget);         
    }
    
    @Override
    public void insert(Widget widget, int index) {
        Widget beforeWidget = null;
        if(openedPanels.size() >0) {
            //Hack. may not be an efficient way to do. 
            this.clear();
            for(int i=0; i< openedPanels.size(); i++) {
                Widget w = openedPanels.get(i);
                
                if(index == i) {
                    beforeWidget = w;
                }
                insert(w, defaultDirection,
                    200,
                    null);                  
            }
          
        }
        if(! (widget instanceof SimplePanel)) {
           openedPanels.add(widget);
                   
        }
        super.add(widget);  
        //insert(widget, defaultDirection, 200, beforeWidget);
    }
}
