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
		
		int spacerHeight();
		
		int spacerWidth();

		String cellTable();

		String cellTableEvenRow();

		String cellTableOddRow();

		String cellTableCell();

		String cellTableCellDiv();

		String headerTable();

		String headerRow();

		String headerCellPrimary();

		String headerCellSecondary();

		String headerContainer();

		String headerText();

		String headerWidget();

		String spacer();

		String selectorAddCell();

		String selectorDeleteCell();

		String selectorControl();

		String selectorAddImage();

		String selectorDeleteImage();

		String selectorToggle();

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

	@Source("../resources/images/selectorAdd.png")
	ImageResource selectorAdd();

	@Source("../resources/images/selectorAddHover.png")
	ImageResource selectorAddHover();
	
	@Source("../resources/images/selectorDelete.png")
	ImageResource selectorDelete();

	@Source("../resources/images/selectorDeleteHover.png")
	ImageResource selectorDeleteHover();

	@Source({ "css/DecisionTable.css" })
	DecisionTableStyle cellTableStyle();

};