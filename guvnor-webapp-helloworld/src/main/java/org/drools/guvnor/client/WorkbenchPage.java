package org.drools.guvnor.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

public class WorkbenchPage extends Composite {

    @UiField(provided = true)
    WorkbenchPart workbenchPart;

    interface WorkbenchPageBinder
            extends
            UiBinder<Widget, WorkbenchPage> {

    }

    private static WorkbenchPageBinder uiBinder = GWT.create(WorkbenchPageBinder.class);

    public WorkbenchPage(EventBus eventBus) {
        workbenchPart = new WorkbenchPart(eventBus);
        workbenchPart.add("button", new HelloWorldButtonViewPart(eventBus));

        initWidget(uiBinder.createAndBindUi(this));
    }

    public void add(String title, IsWidget widget) {
        workbenchPart.add(title, widget);
    }
}
