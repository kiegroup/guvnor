package org.drools.brms.client.common;

import com.google.gwt.user.client.ui.HTML;

public class SmallLabel extends HTML {

	public SmallLabel(String text) {
		super("<small>" + text + "</small>");
	}

	public SmallLabel() {
		super();
	}

	public void setText(String t) {
		setHTML("<small>" + t + "</small>");
	}

}
