package org.drools.guvnor.client.resources;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;

/**
 * Resources for the Decision Table.
 * 
 * @author manstis
 * 
 */
public interface DecisionTableResources extends ClientBundle {

	public interface DecisionTableStyle extends CssResource {

		int rowHeight();

		int rowHeaderHeight();

		int rowHeaderSplitterHeight();

		int rowHeaderSorterHeight();

		int sidebarWidth();
		
		String cellTable();

		String cellTableEvenRow();

		String cellTableOddRow();

		String cellTableCell();

		String cellTableCellSelected();

		String cellTableCellDiv();

		String headerRowBottom();

		String headerRowIntermediate();

		String headerText();

		String headerSplitter();
		
		String headerResizer();

		String selectorSpacer();

		String selectorCell();

	};

	@Source("../resources/images/downArrow.png")
	ImageResource downArrow();

	@Source("../resources/images/smallDownArrow.png")
	ImageResource smallDownArrow();

	@Source("../resources/images/upArrow.png")
	ImageResource upArrow();

	@Source("../resources/images/smallUpArrow.png")
	ImageResource smallUpArrow();

	@Source("../resources/images/toggleSelected.png")
	ImageResource toggleSelected();

	@Source("../resources/images/toggleDeselected.png")
	ImageResource toggleDeselected();

	@Source("../resources/images/new_item.gif")
	ImageResource selectorAdd();

	@Source("../resources/images/delete_item_small.gif")
	ImageResource selectorDelete();

	@Source({ "css/DecisionTable.css" })
	DecisionTableStyle cellTableStyle();

};