package org.drools.guvnor.client.common;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.HTML;

public class ClickableLabel extends HTML {

	@Deprecated
	public ClickableLabel(String text, ClickListener event, boolean enabled) {
		super(doText(text, enabled));
		if (enabled) this.addClickListener(event);
	}

	public ClickableLabel(String text, ClickHandler event, boolean enabled) {
		super(doText(text, enabled));
		if (enabled) this.addClickHandler(event);
	}
	
    private static String doText(String text, boolean enabled) {
        if (enabled)
            return "<div class='x-form-field'><span class='selectable-label'>" + text + "</span></div>";
        else
            return "<div class='x-form-field'>" + text + "</div>";            
    }

    @Deprecated
    public ClickableLabel(String text, ClickListener event) {
        this(text, event, true);
	}

    public ClickableLabel(String text, ClickHandler event) {
        this(text, event, true);
	}
}
