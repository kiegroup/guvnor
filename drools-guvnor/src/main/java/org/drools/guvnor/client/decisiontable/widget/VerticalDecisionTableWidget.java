package org.drools.guvnor.client.decisiontable.widget;

import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;

import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Vertical implementation of a Decision Table where rules are represented as
 * rows and the definition (meta, conditions and actions are represented as
 * columns.
 * 
 * @author manstis
 * 
 */
public class VerticalDecisionTableWidget extends DecisionTableWidget {

	public VerticalDecisionTableWidget(SuggestionCompletionEngine sce) {
		super(sce);
	}
	
	@Override
	protected Panel getBodyPanel() {
		if (this.bodyPanel == null) {
			this.bodyPanel = new VerticalPanel();
		}
		return this.bodyPanel;
	}

	@Override
	protected MergableGridWidget getGridWidget() {
		if (this.gridWidget == null) {
			this.gridWidget = new VerticalMergableGridWidget(this);
		}
		return this.gridWidget;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.drools.guvnor.decisiontable.client.widget.DecisionTableWidget#
	 * getHeaderWidget()
	 */
	@Override
	protected DecisionTableHeaderWidget getHeaderWidget() {
		if (this.headerWidget == null) {
			this.headerWidget = new VerticalDecisionTableHeaderWidget(this);
		}
		return this.headerWidget;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.drools.guvnor.decisiontable.client.widget.DecisionTableWidget#
	 * getMainPanel()
	 */
	@Override
	protected Panel getMainPanel() {
		if (this.mainPanel == null) {
			this.mainPanel = new HorizontalPanel();
		}
		return this.mainPanel;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.drools.guvnor.decisiontable.client.widget.DecisionTableWidget#
	 * getScrollHandler()
	 */
	@Override
	protected ScrollHandler getScrollHandler() {
		return new ScrollHandler() {

			public void onScroll(ScrollEvent event) {
				headerWidget.setScrollPosition(scrollPanel
						.getHorizontalScrollPosition());
				sidebarWidget
						.setScrollPosition(scrollPanel.getScrollPosition());
			}

		};
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.drools.guvnor.decisiontable.client.widget.DecisionTableWidget#
	 * getSidebarWidget()
	 */
	@Override
	protected DecisionTableSidebarWidget getSidebarWidget() {
		if (this.sidebarWidget == null) {
			this.sidebarWidget = new VerticalDecisionTableSidebarWidget(this);
		}
		return this.sidebarWidget;
	}

}
