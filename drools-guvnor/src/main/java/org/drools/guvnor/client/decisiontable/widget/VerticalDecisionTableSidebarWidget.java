package org.drools.guvnor.client.decisiontable.widget;

import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.TableCellElement;
import com.google.gwt.dom.client.TableElement;
import com.google.gwt.dom.client.TableRowElement;
import com.google.gwt.dom.client.TableSectionElement;
import com.google.gwt.user.client.Event;
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
	 * 
	 * @author manstis
	 * 
	 */
	private class VerticalSelectorWidget extends Widget {

		private TableElement table;
		private TableSectionElement tbody;
		private int rows;

		private VerticalSelectorWidget() {
			this.table = Document.get().createTableElement();
			this.table.setCellPadding(0);
			this.table.setCellSpacing(0);
			this.tbody = Document.get().createTBodyElement();
			this.table.appendChild(tbody);
			setElement(table);
			rows = 0;

			// Mouseover and Mouseevents are not sunk to handle the image
			// rollover because Mouseout events are not fired when the mouse
			// moves quickly from the Element. Rollover is therefore handled
			// by CSS. See
			// https://groups.google.com/group/google-web-toolkit/browse_thread/thread/3bd429624341035e?hl=en
			sinkEvents(Event.getTypeInt("click"));
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.google.gwt.user.client.ui.Widget#onBrowserEvent(com.google.gwt
		 * .user.client.Event)
		 */
		@Override
		public void onBrowserEvent(Event event) {
			// Get the event target
			String eventType = event.getType();
			EventTarget eventTarget = event.getEventTarget();
			if (!Element.is(eventTarget)) {
				return;
			}
			Element target = event.getEventTarget().cast();

			// Find the cell where the event occurred
			TableCellElement tdElement = findNearestParentTableCellElement(target);
			if (tdElement == null) {
				return;
			}
			int iCol = tdElement.getCellIndex();

			Element element = tdElement.getParentElement();
			if (element == null) {
				return;
			}
			TableRowElement trElement = TableRowElement.as(element);
			int iRow = trElement.getSectionRowIndex();

			// Perform action
			boolean isClick = eventType.equals("click");
			if (isClick) {
				switch (iCol) {
				case 0:
					dtable.insertRowBefore(iRow);
					break;
				case 1:
					dtable.deleteRow(iRow);
				}
			}

		}

		// Add a new row
		private void add() {
			TableRowElement tre = Document.get().createTRElement();
			tre.setClassName(getRowStyle(rows));
			tre.getStyle().setHeight(style.rowHeight(), Unit.PX);
			populateTableRowElement(tre);
			tbody.appendChild(tre);
			rows++;
		}

		// Reset the widget to an empty TBODY
		private void clear() {
			this.table.removeChild(table.getFirstChild());
			this.tbody = Document.get().createTBodyElement();
			this.table.appendChild(tbody);
			rows = 0;
		}

		// Delete a row at the given index
		private void deleteSelector(int index) {
			tbody.deleteRow(index);
			fixStyles(index);
		}

		// Find the TD that contains the element
		private TableCellElement findNearestParentTableCellElement(Element elem) {
			while ((elem != null) && (elem != table)) {
				String tagName = elem.getTagName();
				if ("td".equalsIgnoreCase(tagName)) {
					return elem.cast();
				}
				elem = elem.getParentElement();
			}
			return null;
		}

		// Row styles need to be re-applied after inserting and deleting rows
		private void fixStyles(int iRow) {
			while (iRow < tbody.getChildCount()) {
				Element e = Element.as(tbody.getChild(iRow));
				TableRowElement tre = TableRowElement.as(e);
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

		// Insert a new row before the given index
		private void insertBefore(int iRow) {
			TableRowElement newRow = tbody.insertRow(iRow);
			newRow.setClassName(getRowStyle(iRow));
			newRow.getStyle().setHeight(style.rowHeight(), Unit.PX);
			populateTableRowElement(newRow);
			fixStyles(iRow);
		}

		// Create a row for the sidebar
		private void populateTableRowElement(TableRowElement tre) {

			String tdAddClasses = style.selectorAddCell();
			String tdDeleteClasses = style.selectorDeleteCell();

			TableCellElement tce1 = Document.get().createTDElement();
			tce1.setClassName(tdAddClasses);
			DivElement divAdd = Document.get().createDivElement();
			divAdd.setClassName(style.selectorAddImage());
			divAdd.setInnerHTML("&nbsp");
			tce1.appendChild(divAdd);

			TableCellElement tce2 = Document.get().createTDElement();
			tce2.setClassName(tdDeleteClasses);
			DivElement divDelete = Document.get().createDivElement();
			divDelete.setClassName(style.selectorDeleteImage());
			divDelete.setInnerHTML("&nbsp;");
			tce2.appendChild(divDelete);

			tre.appendChild(tce1);
			tre.appendChild(tce2);

		}

	}

	/**
	 * Simple spacer to ensure scrollable part of sidebar aligns with grid. This
	 * might be a good place to put the "toggle merging" button.
	 * 
	 * @author manstis
	 * 
	 */
	private class VerticalSideBarSpacerWidget extends Widget {

		// Use a TABLE to ensure alignment with header and grid
		private TableElement table = Document.get().createTableElement();

		private VerticalSideBarSpacerWidget() {

			table.setClassName(style.spacer());
			table.setCellPadding(0);
			table.setCellSpacing(0);

			TableSectionElement tbody = Document.get().createTBodyElement();
			TableRowElement tre = Document.get().createTRElement();
			TableCellElement tce = Document.get().createTDElement();
			DivElement div = Document.get().createDivElement();
			div.setInnerHTML(getImageHtml(dtable.isMerged));
			div.setClassName(style.selectorToggle());

			tce.appendChild(div);
			tre.appendChild(tce);
			tbody.appendChild(tre);
			table.appendChild(tbody);

			setElement(table);
			sinkEvents(Event.getTypeInt("click"));

		}

		@Override
		public void onBrowserEvent(Event event) {
			String eventType = event.getType();
			if (eventType.equals("click")) {
				EventTarget eventTarget = event.getEventTarget();
				if (!Element.is(eventTarget)) {
					return;
				}
				Element target = event.getEventTarget().cast();
				if (target == null) {
					return;
				}
				DivElement div = findNearestParentDivElement(target);
				if (div == null) {
					return;
				}
				div.setInnerHTML(getImageHtml(dtable.toggleMerging()));
			}
		}

		// Find the DIV containing the image
		private DivElement findNearestParentDivElement(Element elem) {
			while ((elem != null) && (elem != table)) {
				String tagName = elem.getTagName();
				if ("div".equalsIgnoreCase(tagName)) {
					return elem.cast();
				}
				elem = elem.getParentElement();
			}
			return null;
		}

		// Get the image for the current state
		private String getImageHtml(boolean isMerged) {
			if (isMerged) {
				return TOGGLE_SELECTED;
			} else {
				return TOGGLE_DESELECTED;
			}
		}

	}

	private ScrollPanel scrollPanel;

	private VerticalPanel container;

	private VerticalSelectorWidget selectors;

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

		container.add(new VerticalSideBarSpacerWidget());
		container.add(scrollPanel);
		scrollPanel.add(selectors);

		// We don't want scroll bars on the Sidebar
		scrollPanel.getElement().getStyle().setOverflow(Overflow.HIDDEN);

		initWidget(container);

	}

	@Override
	public void addSelector() {
		selectors.add();
	}

	@Override
	public void deleteSelector(int index) {
		selectors.deleteSelector(index);
	}

	@Override
	public void initialise() {
		selectors.clear();
	}

	@Override
	public void insertSelectorBefore(int index) {
		selectors.insertBefore(index);
	}

	@Override
	public void setHeight(String height) {
		this.scrollPanel.setHeight(height);
	}

	@Override
	public void setScrollPosition(int position) {
		this.scrollPanel.setScrollPosition(position);
	}

}
