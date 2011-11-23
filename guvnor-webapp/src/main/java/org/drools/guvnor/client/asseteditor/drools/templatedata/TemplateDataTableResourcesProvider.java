/*
 * Copyright 2011 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.drools.guvnor.client.asseteditor.drools.templatedata;

import org.drools.guvnor.client.resources.DecisionTableResources;
import org.drools.guvnor.client.widgets.drools.decoratedgrid.ResourcesProvider;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ImageResource;

/**
 * 
 */
public class TemplateDataTableResourcesProvider
    implements
    ResourcesProvider<TemplateDataColumn> {

    protected static final DecisionTableResources resources = GWT.create( DecisionTableResources.class );

    public int rowHeight() {
        return resources.style().rowHeight();
    }

    public int rowHeaderHeight() {
        return resources.style().rowHeaderHeight();
    }

    public int rowHeaderSplitterHeight() {
        return resources.style().rowHeaderSplitterHeight();
    }

    public int rowHeaderSorterHeight() {
        return resources.style().rowHeaderSorterHeight();
    }

    public int sidebarWidth() {
        return resources.style().sidebarWidth();
    }

    public int borderWidth() {
        return resources.style().borderWidth();
    }

    public int borderWidthThick() {
        return resources.style().borderWidthThick();
    }

    public String cellTableColumn(TemplateDataColumn column) {
        return resources.style().templateColumn();
    }

    public String cellTable() {
        return resources.style().cellTable();
    }

    public String cellTableEvenRow() {
        return resources.style().cellTableEvenRow();
    }

    public String cellTableOddRow() {
        return resources.style().cellTableOddRow();
    }

    public String cellTableCell() {
        return resources.style().cellTableCell();
    }

    public String cellTableCellSelected() {
        return resources.style().cellTableCellSelected();
    }

    public String cellTableCellMultipleValues() {
        return resources.style().cellTableCellMultipleValues();
    }

    public String cellTableCellOtherwise() {
        return resources.style().cellTableCellOtherwise();
    }

    public String cellTableCellDiv() {
        return resources.style().cellTableCellDiv();
    }

    public String cellTableGroupDiv() {
        return resources.style().cellTableGroupDiv();
    }

    public String cellTableTextDiv() {
        return resources.style().cellTableTextDiv();
    }

    public String headerRowBottom() {
        return resources.style().headerRowBottom();
    }

    public String headerRowIntermediate() {
        return resources.style().headerRowIntermediate();
    }

    public String headerText() {
        return resources.style().headerText();
    }

    public String headerSplitter() {
        return resources.style().headerSplitter();
    }

    public String headerResizer() {
        return resources.style().headerResizer();
    }

    public String selectorSpacer() {
        return resources.style().selectorSpacer();
    }

    public String selectorSpacerOuterDiv() {
        return resources.style().selectorSpacerOuterDiv();
    }

    public String selectorSpacerInnerDiv() {
        return resources.style().selectorSpacerInnerDiv();
    }

    public String selectorCell() {
        return resources.style().selectorCell();
    }

    public ImageResource arrowSpacerIcon() {
        return resources.arrowSpacerIcon();
    }

    public ImageResource downArrowIcon() {
        return resources.downArrowIcon();
    }

    public ImageResource smallDownArrowIcon() {
        return resources.smallDownArrowIcon();
    }

    public ImageResource upArrowIcon() {
        return resources.upArrowIcon();
    }

    public ImageResource smallUpArrowIcon() {
        return resources.smallUpArrowIcon();
    }

    public ImageResource toggleUnmergeIcon() {
        return resources.toggleUnmergeIcon();
    }

    public ImageResource toggleMergeIcon() {
        return resources.toggleMergeIcon();
    }

    public ImageResource selectorAddIcon() {
        return resources.selectorAddIcon();
    }

    public ImageResource selectorDeleteIcon() {
        return resources.selectorDeleteIcon();
    }

    public ImageResource collapseCellsIcon() {
        return resources.collapseCellsIcon();
    }

    public ImageResource expandCellsIcon() {
        return resources.expandCellsIcon();
    }

}
