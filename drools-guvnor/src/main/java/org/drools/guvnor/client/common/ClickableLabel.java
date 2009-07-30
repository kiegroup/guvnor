package org.drools.guvnor.client.common;

import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.HTML;

public class ClickableLabel extends HTML {

	public ClickableLabel(String text, ClickListener event, boolean enabled) {
		super(doText(text, enabled));
		if (enabled) this.addClickListener(event);
	}

    private static String doText(String text, boolean enabled) {
        if (enabled)
            return "<div class='x-form-field'><span class='selectable-label'>" + text + "</span></div>";
        else
            return "<div class='x-form-field'>" + text + "</div>";            
    }

    public ClickableLabel(String text, ClickListener event) {
        this(text, event, true);
	}


}
