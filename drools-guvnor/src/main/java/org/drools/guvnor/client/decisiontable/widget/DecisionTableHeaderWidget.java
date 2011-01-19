package org.drools.guvnor.client.decisiontable.widget;

import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.resources.DecisionTableResources;
import org.drools.guvnor.client.resources.DecisionTableResources.DecisionTableStyle;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.HasResizeHandlers;
import com.google.gwt.user.client.ui.CellPanel;
import com.google.gwt.user.client.ui.Panel;

/**
 * A widget used to display the DecisionTable's "header". A
 * VerticalDecisionTable would display this above the table of data whereas a
 * HorizontalDecisionTable would display this to the left of the data.
 * 
 * @author manstis
 * 
 */
public abstract class DecisionTableHeaderWidget extends CellPanel implements
		HasResizeHandlers, HasColumnResizeHandlers {

	protected Panel panel;
	protected DecisionTableWidget dtable;

	// Resources
	protected static final DecisionTableResources resource = GWT
			.create(DecisionTableResources.class);
	protected static final DecisionTableStyle style = resource.cellTableStyle();
	protected static final Constants constants = GWT.create(Constants.class);

	/**
	 * Construct a "Header" for the provided DecisionTable
	 * 
	 * @param decisionTable
	 */
	public DecisionTableHeaderWidget(DecisionTableWidget dtable) {
		this.dtable = dtable;
	}

	/**
	 * Redraw entire header
	 */
	public abstract void redraw();

	/**
	 * Set scroll position to enable some degree of synchronisation between
	 * DecisionTable and DecisionTableHeader
	 * 
	 * @param position
	 */
	public abstract void setScrollPosition(int position);

}
