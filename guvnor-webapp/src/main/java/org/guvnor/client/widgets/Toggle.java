package org.guvnor.client.widgets;

import com.github.gwtbootstrap.client.ui.Button;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class Toggle
        extends Composite
        implements HasValueChangeHandlers<Boolean> {

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Boolean> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    interface Style
            extends CssResource {

        String red();

        String blue();

    }

    interface Binder
            extends
            UiBinder<Widget, Toggle> {

    }

    private static Binder uiBinder = GWT.create(Binder.class);

    @UiField
    Style style;

    @UiField
    Button on;

    @UiField
    Button off;

    public Toggle() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    /**
     * @param enabled If true, first option is selected
     */
    public void setValue(boolean enabled) {
        if (enabled) {
            makeActive(on, style.blue());
            reset(off);
        } else {
            makeActive(off, style.red());
            reset(on);
        }
    }

    private void reset(Button button) {
        button.setActive(false);
        button.removeStyleName(style.red());
        button.removeStyleName(style.blue());
    }

    private void makeActive(Button button, String style1) {
        button.setActive(true);
        button.addStyleName(style1);
    }

    @UiHandler("on")
    public void onOnClick(ClickEvent e) {
        setValue(true);
        ValueChangeEvent.fire(this, true);
    }

    @UiHandler("off")
    public void onOffClick(ClickEvent e) {
        setValue(false);
        ValueChangeEvent.fire(this, false);
    }
}
