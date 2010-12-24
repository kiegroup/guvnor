package org.drools.guvnor.client.decisiontable.widget;

import org.drools.guvnor.client.resources.DecisionTableResources;
import org.drools.guvnor.client.resources.DecisionTableResources.DecisionTableStyle;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Composite;

/**
 * A widget used to display the DecisionTable's "sidebar". A
 * VerticalDecisionTable would display this to the left of the table of data
 * whereas a HorizontalDecisionTable would display this above the data. *
 * 
 * @author manstis
 * 
 */
public abstract class DecisionTableSidebarWidget extends Composite {

	protected DecisionTableWidget dtable;

	// Resources
	protected static final DecisionTableResources resource = GWT
			.create(DecisionTableResources.class);
	protected static final DecisionTableStyle style = resource.cellTableStyle();

	// Image resources
	protected static final String TOGGLE_SELECTED = makeImage(resource
			.toggleSelected());
	protected static final String TOGGLE_DESELECTED = makeImage(resource
			.toggleDeselected());

	private static String makeImage(ImageResource resource) {
		AbstractImagePrototype prototype = AbstractImagePrototype
				.create(resource);
		return prototype.getHTML();
	}

	/**
	 * Construct a "Sidebar" for the provided DecisionTable
	 * 
	 * @param decisionTable
	 */
	public DecisionTableSidebarWidget(DecisionTableWidget dtable) {
		this.dtable = dtable;
		style.ensureInjected();
	}

	/**
	 * Add a selector widget to the Sidebar. A selector widget can implement any
	 * row-level operation, such as selecting, inserting new (positional) etc.
	 * It is intended that this is called as each row to MergableGridWidget is
	 * added.
	 */
	public abstract void addSelector();

	/**
	 * Delete a Selector at the given index.
	 * 
	 * @param index
	 */
	public abstract void deleteSelector(int index);

	/**
	 * Initialise the sidebar, this normally involves clearing any content and
	 * setting up any formatting requirements before calls to addSelector are
	 * made. I.E. ensure the sidebar is empty before items are added to it.
	 */
	public abstract void initialise();

	/**
	 * Insert a Selector before the given index.
	 * 
	 * @param index
	 */
	public abstract void insertSelectorBefore(int index);

	/**
	 * Set scroll position to enable some degree of synchronisation between
	 * DecisionTable and DecisionTableSidebar
	 * 
	 * @param position
	 */
	public abstract void setScrollPosition(int position);

}
