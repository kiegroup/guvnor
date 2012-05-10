package org.drools.guvnor.client.layout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.guvnor.client.common.LoadingPopup;
import org.drools.guvnor.client.common.ScrollTabLayoutPanel;

/*import org.drools.guvnor.client.dnd.PickupDragController;
import org.drools.guvnor.client.dnd.drop.HorizontalPanelDropController;
import org.drools.guvnor.client.dnd.drop.VerticalPanelDropController;
*/

/*import com.allen_sauer.gwt.dnd.client.drop.AbsolutePositionDropController;
import com.allen_sauer.gwt.dnd.client.drop.DropController;
import com.allen_sauer.gwt.dnd.client.drop.HorizontalPanelDropController;
import com.allen_sauer.gwt.dnd.client.drop.VerticalPanelDropController;*/

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.allen_sauer.gwt.dnd.client.drop.HorizontalPanelDropController;
import com.allen_sauer.gwt.dnd.client.drop.VerticalPanelDropController;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;

/**
 * This is the tab panel manager.
 */
public class ExplorerViewCenterPanel extends Composite
    /*implements
    TabbedPanel*/ {
    private static PlaceManager placeManager;
    private final ScrollTabLayoutPanel       mainTabLayoutPanel;
    private final ScrollTabLayoutPanel       bottomTabLayoutPanel;

    SplitLayoutPanel splitPanel = new SplitLayoutPanel();
    SplitLayoutInsertPanel splitPanelMain = new SplitLayoutInsertPanel(DockLayoutPanel.Direction.SOUTH);
    SplitLayoutInsertPanel splitPanelBottom = new SplitLayoutInsertPanel();

    HorizontalPanel mainbottom = new HorizontalPanel();
    VerticalPanel mainhp = new VerticalPanel();
    
    private PanelMap                         openedTabs          = new PanelMap();
    PickupDragController dragController; 
    //private final EventBus                   eventBus;

    AbsolutePanel containingPanel;
    
    public ExplorerViewCenterPanel() {
        mainTabLayoutPanel = new ScrollTabLayoutPanel(2, Unit.EM);
        bottomTabLayoutPanel = new ScrollTabLayoutPanel(2, Unit.EM);

        //RootPanel.get().setHeight("100%");
        
        RootPanel.get().setPixelSize(1400, 1800);
        dragController = new PickupDragController(RootPanel.get(), true);
/*        
        VerticalPanelDropController main = new VerticalPanelDropController(mainhp);
        dragController.registerDropController(main);
        HorizontalPanelDropController bottom = new HorizontalPanelDropController(mainbottom);
        dragController.registerDropController(bottom);
        
        mainhp.add(createDraggable());
        mainbottom.add(createDraggable());*/
        //mainbottom.add(createDraggable());
        //mainbottom.add(createDraggable());     
        
        
        SplitLayoutPanelDropController mainDropController = new SplitLayoutPanelDropController(splitPanelMain);
        dragController.registerDropController(mainDropController);

        SplitLayoutPanelDropController bottomDropController = new SplitLayoutPanelDropController(splitPanelBottom);
        dragController.registerDropController(bottomDropController);
        
        splitPanelMain.add(createDraggable());
        splitPanelBottom.add(createDraggable());
        //splitPanelBottom.add(createDraggable());
        splitPanel.addSouth(splitPanelBottom, 200);
        splitPanel.add(splitPanelMain);
        initWidget(splitPanel);
    }
    
    protected Widget createDraggable() {
        Label l = new Label("widget1");
        dragController.makeDraggable(l);
        return l;
    }
    
    public void addTab(final String tabname, IsWidget widget,
            final PlaceRequest place) {
        VerticalPanel vp = new VerticalPanel();
        ScrollPanel localTP = new ScrollPanel();
        localTP.add( widget );
        vp.setWidth("100%");
        //Widget c = newClosableLabel(tabname, place);
        Label c = new Label(tabname);
        vp.add(c);
        vp.add(localTP);
        
        dragController.makeDraggable(vp, c);
        
        splitPanelBottom.add( vp);            
        
/*        bottomTabLayoutPanel.selectTab( vp );

        openedTabs.put( place,
                vp );*/
        
/*        ScrollPanel localTP = new ScrollPanel();
        localTP.add(widget);
        VerticalPanel v = new VerticalPanel();        
        Widget c = newClosableLabel(tabname, place);
        v.add(c );
        v.add(localTP);*/
        //tabLayoutPanel.add(localTP, newClosableLabel(tabname, place));
        //tabLayoutPanel.selectTab(localTP);
        
        //dragController.makeDraggable(v, c);
        //mainbottom.add(v);
             
/*        if(openedTabs.size() == 0) {
            splitPanel.addSouth(localTP, 200);
            splitPanel.add(mainhp);
            openedTabs.put(place, localTP);   
        } else if(openedTabs.size() >= 1){*/
            //splitPanel.clear();
            //splitPanel.addSouth(splitPanelBottom, 200);
            //splitPanel.add(mainhp);
/*            
            westhp.add(openedTabs.get(openedTabs.getKey(0)));
            easthp.add(localTP);
           
            splitPanelBottom.addWest(westhp, 400);
            splitPanelBottom.add(easthp);
            openedTabs.put(place, localTP);   
        }*/

    } 
   
    
/*
    private void addBeforeSelectionHandler() {
        tabLayoutPanel.addBeforeSelectionHandler( new BeforeSelectionHandler<Integer>() {
            public void onBeforeSelection(BeforeSelectionEvent<Integer> integerBeforeSelectionEvent) {
                if ( !tabLayoutPanel.isCanSelectTabToggle() ) {
                    integerBeforeSelectionEvent.cancel();
                    clientFactory.getPlaceController().goTo( openedTabs.getKey( integerBeforeSelectionEvent.getItem() ) );
                }
            }
        } );
    }*/

    public boolean contains(PlaceRequest key) {
        return openedTabs.contains( key );
    }

    public void show(PlaceRequest key) {
        if ( openedTabs.contains( key ) ) {
            LoadingPopup.close();
            mainTabLayoutPanel.selectTab( openedTabs.get( key ) );
        }
    }

    /**
     * Add a new tab. Should only do this if have checked showIfOpen to avoid
     * dupes being opened.
     * 
     * @param tabname
     *            The displayed tab name.
     * @param widget
     *            The contents.
     * @param place
     *            A place which is unique.
     */
/*    public void addTab(final String tabname,
                       IsWidget widget,
                       final PlaceRequest place) {

        ScrollPanel localTP = new ScrollPanel();
        localTP.add( widget );
        tabLayoutPanel.add( localTP,
                            newClosableLabel(
                                              tabname,
                                              place
                            ) );
        tabLayoutPanel.selectTab( localTP );

        openedTabs.put( place,
                        localTP );
    }*/

    private Widget newClosableLabel(final String title,
                                    final PlaceRequest place) {
        ClosableLabel closableLabel = new ClosableLabel( title );

        closableLabel.addCloseHandler( new CloseHandler<ClosableLabel>() {
            public void onClose(CloseEvent<ClosableLabel> event) {
                //TODO
                //eventBus.fireEvent( new ClosePlaceEvent( place ) );
                close(place);
            }

        } );

        return closableLabel;
    }

    public void close(PlaceRequest key) {

        int widgetIndex = openedTabs.getIndex( key );

        PlaceRequest nextPlace = getPlace( widgetIndex );

        mainTabLayoutPanel.remove( openedTabs.get( key ) );
        openedTabs.remove( key );

        if ( nextPlace != null ) {
            goTo( nextPlace );
        } else {
            //TODO
            //goTo( Place.NOWHERE );
        }
    }

    private PlaceRequest getPlace(int widgetIndex) {
        if ( isOnlyOneTabLeft() ) {
            //TODO
            return null;
            //return Place.NOWHERE;
        } else if ( isSelectedTabIndex( widgetIndex ) ) {
            return getNeighbour( widgetIndex );
        } else {
            return null;
        }
    }

    private void goTo(PlaceRequest place) {
        placeManager.revealPlace(place);        
    }

    private PlaceRequest getNeighbour(int widgetIndex) {
        if ( isLeftMost( widgetIndex ) ) {
            return getNextPlace();
        } else {
            return getPreviousPlace();
        }
    }

    private boolean isLeftMost(int widgetIndex) {
        return widgetIndex == 0;
    }

    private boolean isSelectedTabIndex(int widgetIndex) {
        return mainTabLayoutPanel.getSelectedIndex() == widgetIndex;
    }

    private PlaceRequest getPreviousPlace() {
        if ( mainTabLayoutPanel.getSelectedIndex() > 0 ) {
            return openedTabs.getKey( mainTabLayoutPanel.getSelectedIndex() - 1 );
        }
        return null;
    }

    private PlaceRequest getNextPlace() {
        return openedTabs.getKey( mainTabLayoutPanel.getSelectedIndex() + 1 );
    }

    private boolean isOnlyOneTabLeft() {
        return mainTabLayoutPanel.getWidgetCount() == 1;
    }

    private class PanelMap {

        private final Map<PlaceRequest, Panel> keysToPanel = new HashMap<PlaceRequest, Panel>();
        private final List<PlaceRequest>       keys        = new ArrayList<PlaceRequest>();

        Panel get(PlaceRequest key) {
            return keysToPanel.get( key );
        }

        PlaceRequest getKey(int index) {
            return keys.get( index );
        }

        void remove(PlaceRequest key) {
            keys.remove( key );
            keysToPanel.remove( key );
        }

        public boolean contains(PlaceRequest key) {
            return keysToPanel.containsKey( key );
        }

        public void put(PlaceRequest key,
                        Panel panel) {
            keys.add( key );
            keysToPanel.put( key,
                             panel );
        }

        public int getIndex(PlaceRequest key) {
            return keys.indexOf( key );
        }
        
        public int size() {
            return keysToPanel.size();
        }
    }
}
