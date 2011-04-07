package org.drools.guvnor.client.resources;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;

/**
 * Resources for the Decision Table.
 */
public interface DecisionTableResources
    extends
    ClientBundle {

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

    };

    @Source("../resources/images/emptyArrow.png")
    ImageResource emptyArrow();
    
    @Source("../resources/images/downArrow.png")
    ImageResource downArrow();

    @Source("../resources/images/smallDownArrow.png")
    ImageResource smallDownArrow();

    @Source("../resources/images/upArrow.png")
    ImageResource upArrow();

    @Source("../resources/images/smallUpArrow.png")
    ImageResource smallUpArrow();

    @Source("../resources/images/icon-unmerge.png")
    ImageResource toggleUnmerge();

    @Source("../resources/images/icon-merge.png")
    ImageResource toggleMerge();

    @Source("../resources/images/new_item.gif")
    ImageResource selectorAdd();

    @Source("../resources/images/delete_item_small.gif")
    ImageResource selectorDelete();

    @Source("../resources/images/collapse.gif")
    ImageResource collapse();

    @Source("../resources/images/expand.gif")
    ImageResource expand();
    
    @Source({"css/DecisionTable.css"})
    DecisionTableStyle cellTableStyle();

};
