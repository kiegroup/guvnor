package org.drools.guvnor.client.layout;

import java.util.Iterator;

import org.drools.guvnor.client.common.ScrollTabLayoutPanel;

import com.google.gwt.user.client.ui.InsertPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.Panel;

public class TabLayoutInsertPanel extends Panel implements InsertPanel {

    private TabLayoutPanel tabLayoutPanel = null;
    
    public TabLayoutInsertPanel(TabLayoutPanel tabLayoutPanel) {
        tabLayoutPanel = tabLayoutPanel;
    }
    
    @Override
    public Widget getWidget(int arg0) {
        // TODO Auto-generated method stub
        return tabLayoutPanel.getWidget(arg0);
    }

    @Override
    public int getWidgetCount() {
        // TODO Auto-generated method stub
        return tabLayoutPanel.getWidgetCount();
    }

    @Override
    public int getWidgetIndex(Widget arg0) {
        // TODO Auto-generated method stub
        return tabLayoutPanel.getWidgetIndex(arg0);
    }

    @Override
    public boolean remove(int arg0) {
        // TODO Auto-generated method stub
        return tabLayoutPanel.remove(arg0);
    }

    @Override
    public void add(Widget arg0) {
        tabLayoutPanel.add(arg0);
    }

    @Override
    public void insert(Widget arg0, int arg1) {
        tabLayoutPanel.insert(arg0, arg1);
    }

    @Override
    public Iterator<Widget> iterator() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean remove(Widget arg0) {
        // TODO Auto-generated method stub
        return false;
    }

}
