package org.drools.guvnor.client.decisiontable.widget;

import org.drools.guvnor.client.resources.DecisionTableResources;
import org.drools.guvnor.client.resources.DecisionTableResources.DecisionTableStyle;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.HasResizeHandlers;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
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

	// Image resources
	protected static final String DOWN_ARROW = makeImage(resource.downArrow());
	protected static final String SMALL_DOWN_ARROW = makeImage(resource
			.smallDownArrow());
	protected static final String UP_ARROW = makeImage(resource.upArrow());
	protected static final String SMALL_UP_ARROW = makeImage(resource
			.smallUpArrow());

	private static String makeImage(ImageResource resource) {
		AbstractImagePrototype prototype = AbstractImagePrototype
				.create(resource);
		return prototype.getHTML();
	}

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
