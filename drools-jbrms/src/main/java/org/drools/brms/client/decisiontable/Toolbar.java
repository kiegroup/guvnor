package org.drools.brms.client.decisiontable;
/*
 * Copyright 2005 JBoss Inc
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



import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

/**
 * This shows the widget for moving a row up or down.
 * @author Steven Williams
 */
public class Toolbar extends Composite {

    private HorizontalPanel toolbar = new HorizontalPanel();
    private int row;
    
    public Toolbar() {
    	initWidget(toolbar);
    }
    /**
     * Pass in the click listener delegate for when the respective action is clicked and the
     * direction to move the row
     * @param currentRow
     * @param clickListener
     * @param direction
     */
    public Toolbar(final EditableDTGrid grid) {
		toolbar.add(createInsertRow(grid));
		toolbar.add(createDeleteRow(grid));
		toolbar.add(createMoveUp(grid));
		toolbar.add(createMoveDown(grid));
		toolbar.add(createMergeCol(grid));
		toolbar.add(createSplitCol(grid));
		toolbar.add(createMergeRow(grid));
		toolbar.add(createSplitRow(grid));
		toolbar.setStyleName("dt-editor-Toolbar");
        
        initWidget( toolbar );
    }
	private Image createSplitRow(final EditableDTGrid grid) {
		Image split = new Image("images/split_row.gif");
		split.addClickListener(new ClickListener() {

			public void onClick(Widget w) {
				grid.splitRow();
			}});
		return split;
	}
	private Image createMergeRow(final EditableDTGrid grid) {
		Image merge = new Image("images/merge_row.gif");
		merge.addClickListener(new ClickListener() {
			
			public void onClick(Widget w) {
				grid.mergeRow();
			}});
		return merge;
	}
	private Image createSplitCol(final EditableDTGrid grid) {
		Image split = new Image("images/split_col.gif");
		split.addClickListener(new ClickListener() {
			
			public void onClick(Widget w) {
				grid.splitCol();
			}});
		return split;
	}
	private Image createMergeCol(final EditableDTGrid grid) {
		Image merge = new Image("images/merge_col.gif");
		merge.addClickListener(new ClickListener() {
			
			public void onClick(Widget w) {
				grid.mergeCol();
			}});
		return merge;
	}
	private Image createMoveDown(final EditableDTGrid grid) {
		Image moveDown = new Image("images/shuffle_down.gif");
		moveDown.addClickListener(new ClickListener() {
			
			public void onClick(Widget w) {
				grid.moveDown();
			}});
		return moveDown;
	}
	private Image createMoveUp(final EditableDTGrid grid) {
		Image moveUp = new Image("images/shuffle_up.gif");
		moveUp.addClickListener(new ClickListener() {
			
			public void onClick(Widget w) {
				grid.moveUp();
			}
		});
		return moveUp;
	}
	private Image createDeleteRow(final EditableDTGrid grid) {
		Image delete = new Image("images/clear_item.gif");
        delete.setTitle( "Delete row" );
		delete.addClickListener(new ClickListener() {
			
			public void onClick(Widget w) {
				grid.deleteRow();
			}
		});
		return delete;
	}
	private Image createInsertRow(final EditableDTGrid grid) {
		Image insert = new Image("images/new_item.gif");
		insert.addClickListener(new ClickListener() {

			public void onClick(Widget w) {
				grid.insertRow();
			}
		});
		return insert;
	}
    
    public void setRow(final int row) {
    	this.row = row;
    }
    
    public int getRow() {
    	return row;
    }
    
}