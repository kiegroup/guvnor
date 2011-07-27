package org.drools.guvnor.client.util;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.layout.client.Layout.Alignment;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import org.drools.guvnor.client.resources.Images;

public class ScrollTabLayoutPanel extends Composite {
    private static Images images = GWT.create( Images.class );


    private final double barHeight = 2;
    private final Unit barUnit = Unit.EM;

    private final CustomTabLayoutPanel layout = new CustomTabLayoutPanel( barHeight,
            barUnit );

    private Image scrollLeft;
    private Image scrollRight;
    private FlowPanel tabBar = null;
    private LayoutPanel panel;

    private static final int ScrollLengh = 100;
    private boolean isScrollingEnabled = false;

    private HandlerRegistration windowResizeHandler;

    private boolean canSelectTabToggle = false;

    public ScrollTabLayoutPanel() {
        initWidget( layout );
        this.panel = layout.getLayoutPanel();

        for (int i = 0; i < panel.getWidgetCount(); ++i) {
            Widget widget = panel.getWidget( i );
            if ( widget instanceof FlowPanel ) {
                tabBar = (FlowPanel) widget;
                break;
            }
        }
    }

    public HandlerRegistration addBeforeSelectionHandler(
            BeforeSelectionHandler<Integer> handler) {
        return layout.addBeforeSelectionHandler( handler );
    }

    public boolean isCanSelectTabToggle() {
        return canSelectTabToggle;
    }


    public void selectTab(Widget child) {
        canSelectTabToggle = true;
        layout.selectTab( child );
        canSelectTabToggle = false;
    }

    public void add(Widget child,
                    Widget tab) {
        canSelectTabToggle = true;
        layout.add( child,
                tab );
        checkIfScrollButtonsNecessary();
        canSelectTabToggle = false;
    }

    public boolean remove(Widget w) {
        boolean b = layout.remove( w );
        checkIfScrollButtonsNecessary();
        return b;
    }

    @Override
    protected void onLoad() {
        super.onLoad();

        if ( windowResizeHandler == null ) {
            windowResizeHandler = Window.addResizeHandler( new ResizeHandler() {
                public void onResize(ResizeEvent event) {
                    checkIfScrollButtonsNecessary();
                }
            } );
        }
    }

    @Override
    protected void onUnload() {
        super.onUnload();

        if ( windowResizeHandler != null ) {
            windowResizeHandler.removeHandler();
            windowResizeHandler = null;
        }
    }

    private void enableScroll() {
        if ( !isScrollingEnabled ) {
            scrollRight = new Image( images.scrollRight() );
            scrollLeft = new Image( images.scrollLeft() );
            scrollLeft.addClickHandler( createScrollClickHandler( ScrollLengh ) );
            scrollRight.addClickHandler( createScrollClickHandler( -ScrollLengh ) );

            panel.setWidgetLeftRight( tabBar,
                    30,
                    Unit.PX,
                    30,
                    Unit.PX );
            panel.setWidgetTopHeight( tabBar,
                    0,
                    Unit.PX,
                    barHeight,
                    barUnit );
            panel.setWidgetVerticalPosition( tabBar,
                    Alignment.END );

            panel.add( scrollRight );
            panel.setWidgetRightWidth( scrollRight,
                    0,
                    Unit.PX,
                    27,
                    Unit.PX );
            panel.setWidgetTopHeight( scrollRight,
                    0,
                    Unit.PX,
                    barHeight,
                    barUnit );
            panel.setWidgetVerticalPosition( scrollRight,
                    Alignment.END );

            panel.add( scrollLeft );
            panel.setWidgetLeftWidth( scrollLeft,
                    0,
                    Unit.PX,
                    27,
                    Unit.PX );
            panel.setWidgetTopHeight( scrollLeft,
                    0,
                    Unit.PX,
                    barHeight,
                    barUnit );
            panel.setWidgetVerticalPosition( scrollLeft,
                    Alignment.END );

            isScrollingEnabled = true;
        }
    }

    private void disableScroll() {
        if ( isScrollingEnabled ) {
            panel.remove( scrollRight );
            panel.remove( scrollLeft );

            panel.setWidgetLeftRight( tabBar,
                    0,
                    Unit.PX,
                    0,
                    Unit.PX );
            panel.setWidgetTopHeight( tabBar,
                    0,
                    Unit.PX,
                    barHeight,
                    barUnit );
            panel.setWidgetVerticalPosition( tabBar,
                    Alignment.END );

            isScrollingEnabled = false;
        }
    }

    private void checkIfScrollButtonsNecessary() {
        // Defer size calculations until sizes are available, when calculating
        // immediately after add(), all size methods return zero
        Scheduler.get().scheduleDeferred( new Scheduler.ScheduledCommand() {
            public void execute() {
                boolean isScrolling = isScrollingNecessary();
                if ( isScrolling ) {
                    enableScroll();
                } else {
                    disableScroll();
                }
            }
        } );
    }

    private boolean isScrollingNecessary() {
        Widget lastTab = getLastTab();
        if ( lastTab == null ) return false;

        return getRightOfWidget( lastTab ) > getTabBarWidth();
    }

    private ClickHandler createScrollClickHandler(final int diff) {
        return new ClickHandler() {
            public void onClick(ClickEvent event) {
                Widget lastTab = getLastTab();
                if ( lastTab == null ) return;

                int newLeft = parsePosition( tabBar.getElement().getStyle().getLeft() ) + diff;
                int rightOfLastTab = getRightOfWidget( lastTab );

                // Prevent scrolling the last tab too far away form the right
                // border, or the first tab further than the left border position
                if ( newLeft <= 0 && (getTabBarWidth() - newLeft < (rightOfLastTab + ScrollLengh)) ) {
                    scrollTo( newLeft );
                }
            }
        };
    }

    private void scrollTo(int pos) {
        tabBar.getElement().getStyle().setLeft( pos,
                Unit.PX );
    }

    private int getRightOfWidget(Widget widget) {
        return widget.getElement().getOffsetLeft() + widget.getElement().getOffsetWidth();
    }

    private int getTabBarWidth() {
        return tabBar.getElement().getParentElement().getClientWidth();
    }

    private Widget getLastTab() {
        if ( tabBar.getWidgetCount() == 0 ) return null;

        return tabBar.getWidget( tabBar.getWidgetCount() - 1 );
    }

    private static int parsePosition(String positionString) {
        int position;
        try {
            for (int i = 0; i < positionString.length(); i++) {
                char c = positionString.charAt( i );
                if ( c != '-' && !(c >= '0' && c <= '9') ) {
                    positionString = positionString.substring( 0,
                            i );
                }
            }

            position = Integer.parseInt( positionString );
        } catch (NumberFormatException ex) {
            position = 0;
        }
        return position;
    }

    public int getSelectedIndex() {
        return layout.getSelectedIndex();
    }

    public int getWidgetCount() {
        return layout.getWidgetCount();
    }

    class CustomTabLayoutPanel extends TabLayoutPanel {

        public CustomTabLayoutPanel(double barHeight, Unit barUnit) {
            super( barHeight, barUnit );
        }

        LayoutPanel getLayoutPanel() {
            return (LayoutPanel) getWidget();
        }
    }
}
