/*
 * Copyright 2011 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.guvnor.guided.dtable.client.widget.table;

import com.google.gwt.resources.client.ImageResource;
import org.kie.guvnor.decoratedgrid.client.widget.ResourcesProvider;
import org.kie.guvnor.guided.dtable.client.resources.css.CssResources;
import org.kie.guvnor.guided.dtable.client.resources.images.ImageResources;
import org.kie.guvnor.guided.dtable.model.ActionCol52;
import org.kie.guvnor.guided.dtable.model.BaseColumn;
import org.kie.guvnor.guided.dtable.model.ConditionCol52;

/**
 * A class to provide different resources for Decision Tables
 */
public class DecisionTableResourcesProvider
        implements
        ResourcesProvider<BaseColumn> {

    protected ImageResources images = ImageResources.INSTANCE;
    protected CssResources css = CssResources.INSTANCE;

    public int rowHeight() {
        return css.rowHeight();
    }

    public int rowHeaderHeight() {
        return css.rowHeaderHeight();
    }

    public int rowHeaderSplitterHeight() {
        return css.rowHeaderSplitterHeight();
    }

    public int rowHeaderSorterHeight() {
        return css.rowHeaderSorterHeight();
    }

    public int sidebarWidth() {
        return css.sidebarWidth();
    }

    public int borderWidth() {
        return css.borderWidth();
    }

    public int borderWidthThick() {
        return css.borderWidthThick();
    }

    public String cellTableColumn( BaseColumn column ) {
        if ( column instanceof ConditionCol52 ) {
            return css.conditionColumn();
        } else if ( column instanceof ActionCol52 ) {
            return css.actionColumn();
        }
        return css.metaColumn();
    }

    public String cellTable() {
        return css.cellTable();
    }

    public String cellTableEvenRow() {
        return css.cellTableEvenRow();
    }

    public String cellTableOddRow() {
        return css.cellTableOddRow();
    }

    public String cellTableCell() {
        return css.cellTableCell();
    }

    public String cellTableCellSelected() {
        return css.cellTableCellSelected();
    }

    public String cellTableCellMultipleValues() {
        return css.cellTableCellMultipleValues();
    }

    public String cellTableCellOtherwise() {
        return css.cellTableCellOtherwise();
    }

    public String cellTableCellDiv() {
        return css.cellTableCellDiv();
    }

    public String cellTableGroupDiv() {
        return css.cellTableGroupDiv();
    }

    public String cellTableTextDiv() {
        return css.cellTableTextDiv();
    }

    public String headerRowBottom() {
        return css.headerRowBottom();
    }

    public String headerRowIntermediate() {
        return css.headerRowIntermediate();
    }

    public String headerText() {
        return css.headerText();
    }

    public String headerSplitter() {
        return css.headerSplitter();
    }

    public String headerResizer() {
        return css.headerResizer();
    }

    public String selectorSpacer() {
        return css.selectorSpacer();
    }

    public String selectorSpacerOuterDiv() {
        return css.selectorSpacerOuterDiv();
    }

    public String selectorSpacerInnerDiv() {
        return css.selectorSpacerInnerDiv();
    }

    public String selectorCell() {
        return css.selectorCell();
    }

    public ImageResource arrowSpacerIcon() {
        return images.arrowSpacerIcon();
    }

    public ImageResource downArrowIcon() {
        return images.tableImageResources().downArrow();
    }

    public ImageResource smallDownArrowIcon() {
        return images.tableImageResources().smallDownArrow();
    }

    public ImageResource upArrowIcon() {
        return images.tableImageResources().upArrow();
    }

    public ImageResource smallUpArrowIcon() {
        return images.tableImageResources().smallUpArrow();
    }

    public ImageResource toggleUnmergeIcon() {
        return images.toggleUnmergeIcon();
    }

    public ImageResource toggleMergeIcon() {
        return images.toggleMergeIcon();
    }

    public ImageResource selectorAddIcon() {
        return images.itemImages().newItem();
    }

    public ImageResource selectorDeleteIcon() {
        return images.itemImages().deleteItemSmall();
    }

    public ImageResource collapseCellsIcon() {
        return images.collapseExpand().collapse();
    }

    public ImageResource expandCellsIcon() {
        return images.collapseExpand().expand();
    }

}
