package org.drools.brms.client.common;

import com.google.gwt.user.client.ui.HTML;

public class SmallLabel extends HTML {

	public SmallLabel(String text) {
		//super("<small>" + text + "</small>");
		super("<div class='x-form-field'>" + text + "</div>");
	}

	public SmallLabel() {
		super();
	}

	public void setText(String t) {
		//setHTML("<small>" + t + "</small>");
		setHTML("<div class='x-form-field'>" + t + "</div>");
	}

}
