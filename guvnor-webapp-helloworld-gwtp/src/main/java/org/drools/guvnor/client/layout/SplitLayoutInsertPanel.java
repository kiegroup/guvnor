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
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;


public class SplitLayoutInsertPanel extends SplitLayoutPanel implements InsertPanel {
    private List<Widget>                         openedPanels         = new ArrayList<Widget>();

    DockLayoutPanel.Direction defaultDirection = DockLayoutPanel.Direction.EAST;

    public SplitLayoutInsertPanel() {
        super();
    }
    
    public SplitLayoutInsertPanel(DockLayoutPanel.Direction direction) {
        super();
        defaultDirection = direction;
    }   
    
    @Override
    public void add(Widget widget) {
        if(getWidgetCount() == 0) {
            super.add(widget);
            return;
        }        
        
        //Work around "No widget may be added after the CENTER widget"
        Widget center = this.getCenter();
        if(center != null) {
            super.remove(center);
        }
        insert(widget, defaultDirection, 200, null);
        super.add(center);
/*        
        

        
        List<Widget>                         openedPanels         = new ArrayList<Widget>();
        for ( int i = 0; i < getWidgetCount(); i++ ) {
            Widget w = getWidget( i );
            openedPanels.add(w);
        }
        this.clear();
        for ( int i = 0; i < openedPanels.size(); i++ ) {
            Widget w = openedPanels.get( i );
            super.insert(w, defaultDirection,
                    200,
                    null); 
        }       
  
        super.add(widget);  */ 
/*        
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
        
        insert(widget, defaultDirection,
                200,
                null);  
        //super.add(widget);         
*/    }
    
    @Override
    public void insert(Widget widget, int index) {
        if(getWidgetCount() == 0) {
            super.add(widget);
            return;
        }        
        
        Widget beforeWidget = null;       
        
        //gwt-dnd can pass in an index=4 when there r only 4 widgets in total, which means append to the last one. 
       if (index >= getWidgetCount()) {
            beforeWidget = null; //append
        } else {        
            beforeWidget = this.getWidget(index);
        }
       
        //Work around "No widget may be added after the CENTER widget"       
       List<Widget>                         openedPanels         = new ArrayList<Widget>();
       int realIndex = -1;
       for ( int i = 0; i < getWidgetCount(); i++ ) {
           Widget w = getWidget( i );
           //Hack, need to access SplitLayoutPanel$Splitter
           if(w instanceof SimplePanel || w instanceof VerticalPanel || w instanceof Label) {
               openedPanels.add(w);               
               if(w == beforeWidget) {
                   realIndex = i;
               }
           }
       }
       if(realIndex == -1) {
           realIndex = openedPanels.size();
       }
       
       //Clean up the SplitLayoutPanel
       this.clear();
       
       openedPanels.add(realIndex, widget);
       
       for ( int i = 0; i < openedPanels.size() -1; i++ ) {
           Widget w = openedPanels.get( i );           
           //the last panel needs to be the center

           super.insert(w, defaultDirection, 200, null); 
       }       
       
       super.add(openedPanels.get(openedPanels.size()-1));

/*
           
        Widget center = this.getCenter();
        if(center != null) {
            this.remove(center);
            
            if(beforeWidget == center) {
                beforeWidget = this.getWidget(index-1);
            }
            //Incase the index point to the center, we need to get the widget before the center widget.
            if (index >= getWidgetCount()) {
                index = getWidgetCount() - 1;
            }
            //beforeWidget = this.getWidget(index);
        }
        insert(widget, defaultDirection, 200, beforeWidget);
        //center can be null if the dragging target is the center
        if(center != null) {
            super.add(center);
        }*/

    }

    @Override
    public boolean remove(Widget child) {
      boolean result = super.remove(child);
/*      
      Widget center = this.getCenter();
      if (center == null) {
          if(getWidgetCount() > 0) {
          Widget nextCenter = getWidget(getWidgetCount()-1);
          this.remove(getWidgetCount()-1);
          super.add(nextCenter);
          }
      }*/
      
      //redraw splitPanel so that center wont be null
      List<Widget>                         openedPanels         = new ArrayList<Widget>();

      for ( int i = 0; i < getWidgetCount(); i++ ) {
          Widget w = getWidget( i );
          //Hack, need to access SplitLayoutPanel$Splitter
          if(w instanceof SimplePanel || w instanceof VerticalPanel || w instanceof Label) {
              openedPanels.add(w);               
          }
      }
      
      //Clean up the SplitLayoutPanel
      this.clear();
      
      for ( int i = 0; i < openedPanels.size() -1; i++ ) {
          Widget w = openedPanels.get( i );           
          //the last panel needs to be the center
          super.insert(w, defaultDirection, 200, null); 
      }     
      super.add(openedPanels.get(openedPanels.size()));
      
      return result;
    }
}
