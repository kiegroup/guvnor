package org.drools.guvnor.client;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.TabPanel;

public class WorkbenchPart extends Composite {


    private final TabPanel panel;

    public WorkbenchPart(EventBus eventBus) {
        panel = new TabPanel();
        initWidget(panel);

    }

    public void add(String title, IsWidget widget) {
        panel.add(widget, title);
        panel.selectTab(panel.getWidgetCount() - 1);
    }
}