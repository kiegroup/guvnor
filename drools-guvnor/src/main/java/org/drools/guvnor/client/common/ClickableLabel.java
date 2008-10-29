package org.drools.guvnor.client.common;

import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.HTML;

public class ClickableLabel extends HTML {

	public ClickableLabel(String text, ClickListener event) {
		//super("<small>" + text + "</small>");
		super("<div class='x-form-field'><span class='selectable-label'>" + text + "</span></div>");
		this.addClickListener(event);
	}


}
