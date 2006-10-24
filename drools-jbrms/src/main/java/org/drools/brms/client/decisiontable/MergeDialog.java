package org.drools.brms.client.decisiontable;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
/**
 * Dialog to handle merging cells
 * @author Steven Williams
 *
 */
class MergeDialog extends DialogBox {
	MergeDialog(final EditableDTGrid dt, final int row) {
		setText("Merge cells");
		VerticalPanel form = new VerticalPanel();
		form.setSpacing(4);
		Grid grid = new Grid(2, 2);
		grid.setCellSpacing(4);
		grid.setWidget(0, 0, new Label("Start column"));
		final TextBox startColumn = new TextBox();
		grid.setWidget(0, 1, startColumn);
		grid.setWidget(1, 0, new Label("End column"));
		final TextBox endColumn = new TextBox();
		grid.setWidget(1, 1, endColumn);
		form.add(grid);
		Button okay = new Button("OK");
		okay.addClickListener(new ClickListener() {

			public void onClick(Widget arg0) {
				dt.merge(row, startColumn.getText(), endColumn.getText());
				hide();
			}
			
		});
		Button cancel = new Button("Cancel");
		cancel.addClickListener(new ClickListener() {
			
			public void onClick(Widget arg0) {
				hide();
			}
			
		});
		form.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		HorizontalPanel buttons = new HorizontalPanel();
		buttons.setSpacing(4);
		buttons.add(okay);
		buttons.add(cancel);
		form.add(buttons);
		setWidget(form);
		
	}
}