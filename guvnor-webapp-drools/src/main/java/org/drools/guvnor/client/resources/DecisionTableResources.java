package org.drools.guvnor.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;

/**
 * Resources for the Decision Table.
 */
public interface DecisionTableResources
    extends
    ClientBundle {

    DecisionTableResources INSTANCE = GWT.create( DecisionTableResources.class );

    public interface DecisionTableStyle
        extends
        CssResource {

        int rowHeight();

        int rowHeaderHeight();

        int rowHeaderSplitterHeight();

        int rowHeaderSorterHeight();

        int sidebarWidth();

        int borderWidth();

        int borderWidthThick();

        String cellTable();

        String cellTableEvenRow();

        String cellTableOddRow();

        String cellTableCell();

        String cellTableCellSelected();

        String cellTableCellMultipleValues();

        String cellTableCellOtherwise();

        String cellTableCellDiv();

        String cellTableGroupDiv();

        String cellTableTextDiv();

        String headerRowBottom();

        String headerRowIntermediate();

        String headerText();

        String headerSplitter();

        String headerResizer();

        String selectorSpacer();

        String selectorSpacerOuterDiv();

        String selectorSpacerInnerDiv();

        String selectorCell();

        String metaColumn();

        String conditionColumn();

        String actionColumn();
        
        String templateColumn();
        
        String patternSectionHeader();
        
        String patternConditionSectionHeader();

        String columnLabelHidden();

        String contextMenu();

        String contextMenuItem();

        String contextMenuItemEnabled();

        String contextMenuItemDisabled();
        
    };

    @Source("../resources/images/emptyArrow.png")
    ImageResource arrowSpacerIcon();

    @Source("../resources/images/downArrow.png")
    ImageResource downArrowIcon();

    @Source("../resources/images/smallDownArrow.png")
    ImageResource smallDownArrowIcon();

    @Source("../resources/images/upArrow.png")
    ImageResource upArrowIcon();

    @Source("../resources/images/smallUpArrow.png")
    ImageResource smallUpArrowIcon();

    @Source("../resources/images/icon-unmerge.png")
    ImageResource toggleUnmergeIcon();

    @Source("../resources/images/icon-merge.png")
    ImageResource toggleMergeIcon();

    @Source("../resources/images/new_item.gif")
    ImageResource selectorAddIcon();

    @Source("../resources/images/delete_item_small.gif")
    ImageResource selectorDeleteIcon();

    @Source("../resources/images/collapse.gif")
    ImageResource collapseCellsIcon();

    @Source("../resources/images/expand.gif")
    ImageResource expandCellsIcon();

    @Source({"css/DecisionTable.css"})
    DecisionTableStyle style();

};
