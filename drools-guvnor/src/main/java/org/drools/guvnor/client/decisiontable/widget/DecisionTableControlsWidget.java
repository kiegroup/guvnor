package org.drools.guvnor.client.decisiontable.widget;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Panel;

/**
 * Simple container for controls to manipulate a Decision Table
 * 
 * @author manstis
 * 
 */
public class DecisionTableControlsWidget extends Composite {

	private Panel panel = new HorizontalPanel();

	public DecisionTableControlsWidget(final DecisionTableWidget dtable) {

		// Add row button
		Button btnAddRow = new Button("Add Row", new ClickHandler() {

			public void onClick(ClickEvent event) {
				dtable.appendRow();
			}
		});

		panel.add(btnAddRow);
		initWidget(panel);

	}

}
