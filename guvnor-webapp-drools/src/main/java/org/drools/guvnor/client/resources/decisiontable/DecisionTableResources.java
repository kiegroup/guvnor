package org.drools.guvnor.client.resources.decisiontable;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import org.drools.guvnor.client.resources.CollapseExpand;
import org.drools.guvnor.client.resources.ItemImages;
import org.drools.guvnor.client.resources.TableImageResources;

/**
 * Resources for the Decision Table.
 */
public interface DecisionTableResources
        extends
        ClientBundle {

    DecisionTableResources INSTANCE = GWT.create(DecisionTableResources.class);

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

    @Source("emptyArrow.png")
    ImageResource arrowSpacerIcon();

    TableImageResources tableImageResources();

    @Source("icon-unmerge.png")
    ImageResource toggleUnmergeIcon();

    @Source("icon-merge.png")
    ImageResource toggleMergeIcon();

    ItemImages itemImages();

    CollapseExpand collapseExpand();

    @Source({"DecisionTable.css"})
    DecisionTableStyle style();

};
