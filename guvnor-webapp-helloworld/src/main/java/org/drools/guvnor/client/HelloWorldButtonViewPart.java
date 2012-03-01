package org.drools.guvnor.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Widget;

public class HelloWorldButtonViewPart implements ViewPart {

    private final EventBus eventBus;

    public HelloWorldButtonViewPart(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @Override
    public Widget asWidget() {
        Button button = new Button("add hello world");
        button.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                eventBus.fireEvent(new AddViewPartEvent("helloworld"));
            }
        });
        return button;
    }
}
