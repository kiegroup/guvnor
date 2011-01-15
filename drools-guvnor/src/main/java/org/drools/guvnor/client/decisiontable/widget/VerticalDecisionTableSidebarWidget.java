package org.drools.guvnor.client.decisiontable.widget;

import java.util.ArrayList;

import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.TableElement;
import com.google.gwt.dom.client.TableRowElement;
import com.google.gwt.dom.client.TableSectionElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.CellPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * A sidebar for a VericalDecisionTable. This provides a vertical list of
 * controls to add and remove the associated row from the DecisionTable.
 * 
 * @author manstis
 * 
 */
public class VerticalDecisionTableSidebarWidget extends
		DecisionTableSidebarWidget {

	/**
	 * Widget to render selectors beside rows. Two selectors are provided per
	 * row: (1) A "add new row (above selected)" and (2) "delete row".
	 */
	private class VerticalSelectorWidget extends CellPanel {

		// Widgets (selectors) created (so they can be removed later)
		private ArrayList<Widget> widgets = new ArrayList<Widget>();

		private VerticalSelectorWidget() {
			getBody().getParentElement().<TableElement> cast()
					.setCellSpacing(0);
			getBody().getParentElement().<TableElement> cast()
					.setCellPadding(0);
			sinkEvents(Event.getTypeInt("click"));
		}

		// Add a new row
		private void appendSelector(DynamicDataRow row) {
			insertSelectorBefore(row, widgets.size());
		}

		// Delete a row at the given index
		private void deleteSelector(int index) {
			Widget widget = widgets.get(index);
			remove(widget);
			getBody().<TableSectionElement> cast().deleteRow(index);
			widgets.remove(index);
			fixStyles(index);
		}

		// Row styles need to be re-applied after inserting and deleting rows
		private void fixStyles(int iRow) {
			while (iRow < getBody().getChildCount()) {
				TableRowElement tre = getBody().getChild(iRow)
						.<TableRowElement> cast();
				tre.setClassName(getRowStyle(iRow));
				iRow++;
			}
		}

		// Get style applicable to row
		private String getRowStyle(int iRow) {
			boolean isEven = iRow % 2 == 0;
			String trClasses = isEven ? style.cellTableEvenRow() : style
					.cellTableOddRow();
			return trClasses;
		}

		// Initialise for a complete redraw
		private void initialise() {
			int totalRows = widgets.size();
			for (int iRow = 0; iRow < totalRows; iRow++) {
				deleteSelector(0);
			}
		}

		// Insert a new row before the given index
		private void insertSelectorBefore(DynamicDataRow row, int index) {
			Element tre = DOM.createTR();
			Element tce = DOM.createTD();
			tre.setClassName(getRowStyle(widgets.size()));
			tre.getStyle().setHeight(style.rowHeight(), Unit.PX);
			tce.addClassName(style.selectorCell());
			DOM.insertChild(getBody(), tre, index);
			tre.appendChild(tce);

			Widget widget = makeRowWidget(row);
			add(widget, tce);

			widgets.add(index, widget);
			fixStyles(index);
		}

		// Make the selector Widget
		private Widget makeRowWidget(final DynamicDataRow row) {

			HorizontalPanel hp = new HorizontalPanel();
			hp.setVerticalAlignment(VerticalPanel.ALIGN_MIDDLE);
			hp.setHorizontalAlignment(HorizontalPanel.ALIGN_CENTER);
			hp.setWidth("100%");

			FocusPanel fp;
			fp = new FocusPanel();
			fp.setHeight("100%");
			fp.setWidth("50%");
			fp.add(new Image(resource.selectorAdd()));
			fp.addClickHandler(new ClickHandler() {

				public void onClick(ClickEvent event) {
					dtable.insertRowBefore(row);
				}

			});
			hp.add(fp);

			fp = new FocusPanel();
			fp.setHeight("100%");
			fp.setWidth("50%");
			fp.add(new Image(resource.selectorDelete()));
			fp.addClickHandler(new ClickHandler() {

				public void onClick(ClickEvent event) {
					dtable.deleteRow(row);
				}

			});
			hp.add(fp);
			return hp;
		}

	}

	/**
	 * Simple spacer to ensure scrollable part of sidebar aligns with grid.
	 * 
	 * @author manstis
	 * 
	 */
	private class VerticalSideBarSpacerWidget extends FocusPanel {

		private final HorizontalPanel hp = new HorizontalPanel();
		private Image icon = new Image();

		private VerticalSideBarSpacerWidget() {
			hp.setHorizontalAlignment(HorizontalPanel.ALIGN_CENTER);
			hp.setVerticalAlignment(VerticalPanel.ALIGN_MIDDLE);
			hp.addStyleName(style.selectorSpacer());
			setIconImage(dtable.isMerged);
			hp.add(icon);
			hp.setWidth("100%");
			hp.setHeight("100%");
			add(hp);

			addClickHandler(new ClickHandler() {

				public void onClick(ClickEvent event) {
					setIconImage(dtable.toggleMerging());
				}

			});
		}

		// Set the icon's image accordingly
		private void setIconImage(boolean isMerged) {
			if (isMerged) {
				icon.setResource(resource.toggleSelected());
			} else {
				icon.setResource(resource.toggleDeselected());
			}
		}
	}

	// UI Elements
	private ScrollPanel scrollPanel;
	private VerticalPanel container;
	private VerticalSelectorWidget selectors;
	private final VerticalSideBarSpacerWidget spacer = new VerticalSideBarSpacerWidget();

	/**
	 * Construct a "Sidebar" for the provided DecisionTable
	 * 
	 * @param decisionTable
	 */
	public VerticalDecisionTableSidebarWidget(DecisionTableWidget dtable) {
		super(dtable);

		// Construct the Widget
		scrollPanel = new ScrollPanel();
		container = new VerticalPanel();
		selectors = new VerticalSelectorWidget();

		container.add(spacer);
		container.add(scrollPanel);
		scrollPanel.add(selectors);

		// We don't want scroll bars on the Sidebar
		scrollPanel.getElement().getStyle().setOverflow(Overflow.HIDDEN);

		// Resize spacer to match Header height
		dtable.headerWidget.addResizeHandler(new ResizeHandler() {

			public void onResize(ResizeEvent event) {
				spacer.setHeight(event.getHeight() + "px");
			}

		});

		initWidget(container);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.drools.guvnor.client.decisiontable.widget.DecisionTableSidebarWidget
	 * #appendSelector
	 * (org.drools.guvnor.client.decisiontable.widget.DynamicDataRow)
	 */
	@Override
	public void appendSelector(DynamicDataRow row) {
		selectors.appendSelector(row);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.drools.guvnor.client.decisiontable.widget.DecisionTableSidebarWidget
	 * #deleteSelector(int)
	 */
	@Override
	public void deleteSelector(int index) {
		selectors.deleteSelector(index);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.drools.guvnor.client.decisiontable.widget.DecisionTableSidebarWidget
	 * #initialise()
	 */
	@Override
	public void initialise() {
		selectors.initialise();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.drools.guvnor.client.decisiontable.widget.DecisionTableSidebarWidget
	 * #insertSelectorBefore
	 * (org.drools.guvnor.client.decisiontable.widget.DynamicDataRow, int)
	 */
	@Override
	public void insertSelectorBefore(DynamicDataRow row, int index) {
		selectors.insertSelectorBefore(row, index);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.user.client.ui.UIObject#setHeight(java.lang.String)
	 */
	@Override
	public void setHeight(String height) {
		this.scrollPanel.setHeight(height);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.drools.guvnor.client.decisiontable.widget.DecisionTableSidebarWidget
	 * #setScrollPosition(int)
	 */
	@Override
	public void setScrollPosition(int position) {
		this.scrollPanel.setScrollPosition(position);
	}

}
