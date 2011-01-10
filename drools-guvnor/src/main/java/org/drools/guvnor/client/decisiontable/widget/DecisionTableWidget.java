package org.drools.guvnor.client.decisiontable.widget;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

import org.drools.guvnor.client.decisiontable.cells.CellFactory;
import org.drools.guvnor.client.decisiontable.cells.CellValueFactory;
import org.drools.guvnor.client.decisiontable.widget.MergableGridWidget.CellExtents;
import org.drools.guvnor.client.modeldriven.ui.RuleAttributeWidget;
import org.drools.guvnor.client.resources.DecisionTableResources;
import org.drools.guvnor.client.resources.DecisionTableResources.DecisionTableStyle;
import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.ide.common.client.modeldriven.dt.ActionCol;
import org.drools.ide.common.client.modeldriven.dt.AttributeCol;
import org.drools.ide.common.client.modeldriven.dt.ConditionCol;
import org.drools.ide.common.client.modeldriven.dt.DTColumnConfig;
import org.drools.ide.common.client.modeldriven.dt.DescriptionCol;
import org.drools.ide.common.client.modeldriven.dt.GuidedDecisionTable;
import org.drools.ide.common.client.modeldriven.dt.MetadataCol;
import org.drools.ide.common.client.modeldriven.dt.RowNumberCol;

import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ScrollPanel;

/**
 * Abstract Decision Table encapsulating basic operation.
 * 
 * @author manstis
 * 
 */
public abstract class DecisionTableWidget extends Composite implements
		ValueUpdater<Object> {

	public enum MOVE_DIRECTION {
		LEFT, RIGHT, UP, DOWN
	}

	// Widgets for UI
	protected Panel mainPanel;
	protected Panel bodyPanel;
	protected FocusPanel mainFocusPanel;
	protected ScrollPanel scrollPanel;
	protected MergableGridWidget gridWidget;
	protected DecisionTableHeaderWidget headerWidget;

	protected DecisionTableSidebarWidget sidebarWidget;
	protected boolean isMerged = false;

	protected SuggestionCompletionEngine sce;

	// Decision Table data
	protected DynamicData data;
	protected GuidedDecisionTable model;

	// Resources
	protected static final DecisionTableResources resource = GWT
			.create(DecisionTableResources.class);
	protected static final DecisionTableStyle style = resource.cellTableStyle();

	// Selections store the actual grid data selected (irrespective of
	// merged cells). So a merged cell spanning 2 rows is stored as 2
	// selections. Selections are ordered by row number so we can
	// iterate top to bottom.
	private TreeSet<CellValue<? extends Comparable<?>>> selections = new TreeSet<CellValue<? extends Comparable<?>>>(
			new Comparator<CellValue<? extends Comparable<?>>>() {

				public int compare(CellValue<? extends Comparable<?>> o1,
						CellValue<? extends Comparable<?>> o2) {
					return o1.getPhysicalCoordinate().getRow()
							- o2.getPhysicalCoordinate().getRow();
				}

			});

	/**
	 * Construct at empty Decision Table
	 */
	public DecisionTableWidget(SuggestionCompletionEngine sce) {

		this.sce = sce;

		mainPanel = getMainPanel();
		bodyPanel = getBodyPanel();
		gridWidget = getGridWidget();
		headerWidget = getHeaderWidget();
		sidebarWidget = getSidebarWidget();

		scrollPanel = new ScrollPanel();
		scrollPanel.add(gridWidget);
		scrollPanel.addScrollHandler(getScrollHandler());

		bodyPanel.add(headerWidget);
		bodyPanel.add(scrollPanel);
		mainPanel.add(sidebarWidget);
		mainPanel.add(bodyPanel);

		mainFocusPanel = new FocusPanel(mainPanel);
		initWidget(mainFocusPanel);
	}

	/**
	 * Add a new column to the right of the table
	 * 
	 * @param title
	 *            A title for the column
	 */
	public void addColumn(DTColumnConfig modelColumn) {
		int index = 0;
		if (modelColumn instanceof MetadataCol) {
			index = findMetadataColumnIndex();
		} else if (modelColumn instanceof AttributeCol) {
			index = findAttributeColumnIndex();
		} else if (modelColumn instanceof ConditionCol) {
			index = findConditionColumnIndex();
		} else if (modelColumn instanceof ActionCol) {
			index = findActionColumnIndex();
		}
		insertColumnBefore(modelColumn, index);
	}

	/**
	 * Add a new row to the bottom of the table
	 */
	public void addRow() {
		insertRowBefore(data.size());
	}

	/**
	 * Clear and selection.
	 */
	public void clearSelection() {
		// De-select any previously selected cells
		for (CellValue<? extends Comparable<?>> cell : this.selections) {
			cell.setSelected(false);
			gridWidget.deselectCell(cell);
		}

		// Clear collection
		selections.clear();
	}

	/**
	 * Delete a row at the specified index.
	 * 
	 * @param index
	 */
	public void deleteRow(int index) {
		if (index < 0) {
			throw new IllegalArgumentException(
					"Row number cannot be less than zero.");
		}
		if (index > data.size() - 1) {
			throw new IllegalArgumentException(
					"Row number cannot be greater than the number of rows.");
		}

		data.remove(index);
		assertModelIndexes();

		// Partial redraw
		if (!isMerged) {
			// Single row when not merged
			gridWidget.deleteRow(index);
		} else {
			// Affected rows when merged
			gridWidget.deleteRow(index);

			if (data.size() > 0) {
				updateStaticColumnValues();
				assertModelMerging();
				int minRedrawRow = findMinRedrawRow(index - 1);
				int maxRedrawRow = findMaxRedrawRow(index - 1) + 1;
				if (maxRedrawRow > data.size() - 1) {
					maxRedrawRow = data.size() - 1;
				}
				gridWidget.redrawRows(minRedrawRow, maxRedrawRow);
			}
		}

		redrawStaticColumns();
		assertDimensions();

	}

	/**
	 * Return the model
	 * 
	 * @return The DecisionTable data model
	 */
	public GuidedDecisionTable getModel() {
		return this.model;
	}

	/**
	 * Retrieve the selected cells
	 * 
	 * @return The selected cells
	 */
	public TreeSet<CellValue<? extends Comparable<?>>> getSelections() {
		return this.selections;
	}

	/**
	 * Hide a column
	 * 
	 * @param column
	 *            The Model column to hide
	 */
	public void hideColumn(DTColumnConfig column) {
		List<DynamicColumn> columns = gridWidget.getColumns();
		for (int iCol = 0; iCol < columns.size(); iCol++) {
			if (columns.get(iCol).getModelColumn().equals(column)) {
				if (columns.get(iCol).getIsVisible()) {
					hideColumn(iCol);
					break;
				}
			}
		}
	}

	/**
	 * Hide a column
	 * 
	 * @param index
	 *            The index of the column to hide
	 */
	public void hideColumn(int index) {
		if (index < 0 || index > gridWidget.getColumns().size() - 1) {
			throw new IllegalArgumentException(
					"Column index must be greater than zero and less than then number of declared columns.");
		}

		gridWidget.setColumnVisibility(index, false);
		assertModelIndexes();
		gridWidget.hideColumn(index);
		headerWidget.redraw();
	}

	/**
	 * Insert a new row at the specified index.
	 * 
	 * @param index
	 *            The (zero-based) index of the row
	 */
	public void insertRowBefore(int index) {
		if (index < 0) {
			throw new IllegalArgumentException(
					"Row index cannot be less than zero.");
		}
		if (index > data.size()) {
			throw new IllegalArgumentException(
					"Row index cannot exceed size of table.");
		}

		// Find rows that need to be (re)drawn
		int minRedrawRow = index;
		int maxRedrawRow = index;
		if (index < data.size()) {
			minRedrawRow = findMinRedrawRow(index);
			maxRedrawRow = findMaxRedrawRow(index) + 1;
		}

		// Add row to data
		DynamicDataRow row = new DynamicDataRow();
		for (int iCol = 0; iCol < gridWidget.getColumns().size(); iCol++) {
			DTColumnConfig column = gridWidget.getColumns().get(iCol)
					.getModelColumn();
			CellValue<? extends Comparable<?>> data = CellValueFactory
					.getInstance().getCellValue(column, index, iCol,
							column.getDefaultValue());
			row.add(data);
		}
		data.add(index, row);
		assertModelIndexes();
		updateStaticColumnValues();

		// Partial redraw
		if (!isMerged) {
			// Only new row when not merged
			gridWidget.insertRowBefore(index, row);
		} else {
			// Affected rows when merged
			assertModelMerging();

			// This row is overwritten by the call to redrawRows()
			gridWidget.insertRowBefore(index, row);
			gridWidget.redrawRows(minRedrawRow, maxRedrawRow);
		}

		redrawStaticColumns();
		assertDimensions();
	}

	/**
	 * Move the selected cell
	 * 
	 * @param dir
	 *            Direction to move the selection
	 */
	public void moveSelection(MOVE_DIRECTION dir) {
		if (selections.size() > 0) {
			int step = 0;
			Coordinate nc = null;
			CellExtents ce = null;
			Coordinate c = selections.first().getCoordinate();
			switch (dir) {
			case LEFT:

				// Move left
				step = c.getCol() > 0 ? 1 : 0;
				if (step > 0) {
					nc = new Coordinate(c.getRow(), c.getCol() - step);

					// Skip hidden columns
					while (nc.getCol() > 0
							&& !gridWidget.getColumns().get(nc.getCol())
									.getIsVisible()) {
						nc = new Coordinate(c.getRow(), nc.getCol() - step);
					}
					startSelecting(nc);

					// Ensure cell is visible
					ce = gridWidget.getSelectedCellExtents(selections.first());
					if (ce.getOffsetX() < scrollPanel.getHorizontalScrollPosition()) {
						scrollPanel.setHorizontalScrollPosition(ce.getOffsetX());
					}
				}
				break;
			case RIGHT:

				// Move right
				step = c.getCol() < gridWidget.getColumns().size() - 1 ? 1 : 0;
				if (step > 0) {
					nc = new Coordinate(c.getRow(), c.getCol() + step);

					// Skip hidden columns
					while (nc.getCol() < gridWidget.getColumns().size() - 2
							&& !gridWidget.getColumns().get(nc.getCol())
									.getIsVisible()) {
						nc = new Coordinate(c.getRow(), nc.getCol() + step);
					}
					startSelecting(nc);

					// Ensure cell is visible
					ce = gridWidget.getSelectedCellExtents(selections.first());
					int scrollWidth = scrollPanel.getElement().getClientWidth();
					if (ce.getOffsetX() + ce.getWidth() > scrollWidth
							+ scrollPanel.getHorizontalScrollPosition()) {
						int delta = ce.getOffsetX() + ce.getWidth()
								- scrollPanel.getHorizontalScrollPosition()
								- scrollWidth;
						scrollPanel.setHorizontalScrollPosition(scrollPanel
								.getHorizontalScrollPosition() + delta);
					}
				}
				break;
			case UP:

				// Move up
				step = c.getRow() > 0 ? 1 : 0;
				if (step > 0) {
					nc = new Coordinate(c.getRow() - step, c.getCol());
					startSelecting(nc);

					// Ensure cell is visible
					ce = gridWidget.getSelectedCellExtents(selections.first());
					if (ce.getOffsetY() < scrollPanel.getScrollPosition()) {
						scrollPanel.setScrollPosition(ce.getOffsetY());
					}
				}
				break;
			case DOWN:

				// Move down
				step = c.getRow() < data.size() - 1 ? 1 : 0;
				if (step > 0) {
					nc = new Coordinate(c.getRow() + step, c.getCol());
					startSelecting(nc);

					// Ensure cell is visible
					ce = gridWidget.getSelectedCellExtents(selections.first());
					int scrollHeight = scrollPanel.getElement()
							.getClientHeight();
					if (ce.getOffsetY() + ce.getHeight() > scrollHeight
							+ scrollPanel.getScrollPosition()) {
						int delta = ce.getOffsetY() + ce.getHeight()
								- scrollPanel.getScrollPosition()
								- scrollHeight;
						scrollPanel.setScrollPosition(scrollPanel
								.getScrollPosition() + delta);
					}
				}
			}
		}
	}

	/**
	 * Redraw "static" columns (e.g. row number and salience)
	 */
	public void redrawStaticColumns() {

		for (DynamicColumn col : gridWidget.getColumns()) {

			// Redraw if applicable
			if (col.getRequiresFullRedraw()) {
				gridWidget.redrawColumn(col.getColumnIndex());
			}
		}
	}

	/**
	 * Set the Decision Table's data. This removes all existing columns from the
	 * Decision Table and re-creates them based upon the provided data.
	 * 
	 * @param data
	 */
	public void setModel(GuidedDecisionTable model) {

		this.model = model;

		final int dataSize = model.getData().length;

		if (dataSize > 0) {

			gridWidget.removeAllColumns();
			headerWidget.removeAllColumns();

			// Static columns, Row#
			int iCol = 0;
			DTColumnConfig colStatic;
			DynamicColumn columnStatic;
			colStatic = new RowNumberCol();
			columnStatic = new DynamicColumn(colStatic, CellFactory
					.getInstance().getCell(colStatic, this, sce), iCol, true,
					false);
			gridWidget.addColumn(columnStatic);
			iCol++;

			// Static columns, Description
			colStatic = new DescriptionCol();
			columnStatic = new DynamicColumn(colStatic, CellFactory
					.getInstance().getCell(colStatic, this, sce), iCol);
			gridWidget.addColumn(columnStatic);
			iCol++;

			// Initialise CellTable's Metadata columns
			for (DTColumnConfig col : model.getMetadataCols()) {
				DynamicColumn column = new DynamicColumn(col, CellFactory
						.getInstance().getCell(col, this, sce), iCol);
				column.setIsVisible(!col.isHideColumn());
				gridWidget.addColumn(column);
				iCol++;
			}

			// Initialise CellTable's Attribute columns
			for (DTColumnConfig col : model.getAttributeCols()) {
				DynamicColumn column = new DynamicColumn(col, CellFactory
						.getInstance().getCell(col, this, sce), iCol);
				column.setIsVisible(!col.isHideColumn());
				gridWidget.addColumn(column);
				iCol++;
			}

			// Initialise CellTable's Condition columns
			for (DTColumnConfig col : model.getConditionCols()) {
				DynamicColumn column = new DynamicColumn(col, CellFactory
						.getInstance().getCell(col, this, sce), iCol);
				column.setIsVisible(!col.isHideColumn());
				gridWidget.addColumn(column);
				iCol++;
			}

			// Initialise CellTable's Action columns
			for (DTColumnConfig col : model.getActionCols()) {
				DynamicColumn column = new DynamicColumn(col, CellFactory
						.getInstance().getCell(col, this, sce), iCol);
				column.setIsVisible(!col.isHideColumn());
				gridWidget.addColumn(column);
				iCol++;
			}

			// Setup data
			this.data = new DynamicData();
			List<DynamicColumn> columns = gridWidget.getColumns();
			for (int iRow = 0; iRow < dataSize; iRow++) {
				String[] row = model.getData()[iRow];
				DynamicDataRow cellRow = new DynamicDataRow();
				for (iCol = 0; iCol < columns.size(); iCol++) {
					DTColumnConfig column = columns.get(iCol).getModelColumn();
					CellValue<? extends Comparable<?>> cv = CellValueFactory
							.getInstance().getCellValue(column, iRow, iCol,
									row[iCol]);
					cellRow.add(cv);
				}
				this.data.add(cellRow);
			}
		}
		gridWidget.setRowData(data);
		gridWidget.redraw();
		headerWidget.redraw();

	}

	/**
	 * This should be used instead of setHeight(String) and setWidth(String) as
	 * various child Widgets of the DecisionTable need to have their sizes set
	 * relative to the outermost Widget (i.e. this).
	 */
	@Override
	public void setPixelSize(int width, int height) {
		super.setPixelSize(width, height);
		setHeight(height);
		setWidth(width);
	}

	/**
	 * Show a column
	 * 
	 * @param column
	 *            The Model column to show
	 */
	public void showColumn(DTColumnConfig column) {
		List<DynamicColumn> columns = gridWidget.getColumns();
		for (int iCol = 0; iCol < columns.size(); iCol++) {
			if (columns.get(iCol).getModelColumn().equals(column)) {
				if (!columns.get(iCol).getIsVisible()) {
					showColumn(iCol);
					break;
				}
			}
		}
	}

	/**
	 * Show a column
	 * 
	 * @param index
	 *            The index of the column to show
	 */
	public void showColumn(int index) {
		if (index < 0 || index > gridWidget.getColumns().size() - 1) {
			throw new IllegalArgumentException(
					"Column index must be greater than zero and less than then number of declared columns.");
		}

		gridWidget.setColumnVisibility(index, true);
		assertModelIndexes();
		gridWidget.showColumn(index);
		headerWidget.redraw();
	}

	/**
	 * Sort data based upon information stored in Columns
	 */
	public void sort() {
		final DynamicColumn[] sortOrderList = new DynamicColumn[gridWidget
				.getColumns().size()];
		int index = 0;
		for (DynamicColumn column : gridWidget.getColumns()) {
			int sortIndex = column.getSortIndex();
			if (sortIndex != -1) {
				sortOrderList[sortIndex] = column;
				index++;
			}
		}
		final int sortedColumnCount = index;

		Collections.sort(data, new Comparator<DynamicDataRow>() {

			@SuppressWarnings({ "rawtypes", "unchecked" })
			public int compare(DynamicDataRow leftRow, DynamicDataRow rightRow) {
				int comparison = 0;
				for (int index = 0; index < sortedColumnCount; index++) {
					DynamicColumn sortableHeader = sortOrderList[index];
					Comparable leftColumnValue = leftRow.get(sortableHeader
							.getColumnIndex());
					Comparable rightColumnValue = rightRow.get(sortableHeader
							.getColumnIndex());
					comparison = (leftColumnValue == rightColumnValue) ? 0
							: (leftColumnValue == null) ? -1
									: (rightColumnValue == null) ? 1
											: leftColumnValue
													.compareTo(rightColumnValue);
					if (comparison != 0) {
						switch (sortableHeader.getSortDirection()) {
						case ASCENDING:
							break;
						case DESCENDING:
							comparison = -comparison;
							break;
						default:
							throw new IllegalStateException(
									"Sorting can only be enabled for ASCENDING or"
											+ " DESCENDING, not sortDirection ("
											+ sortableHeader.getSortDirection()
											+ ") .");
						}
						return comparison;
					}
				}
				return comparison;
			}
		});

		removeModelMerging();
		assertModelMerging();
		updateStaticColumnValues();
		gridWidget.setRowData(data);
		gridWidget.redraw();

	}

	/**
	 * Select a single cell. If the cell is merged the selection is extended to
	 * include all merged cells.
	 * 
	 * @param start
	 *            The physical coordinate of the cell
	 */
	public void startSelecting(Coordinate start) {
		clearSelection();
		CellValue<?> startCell = data.get(start);
		extendSelection(startCell.getCoordinate());
	}

	/**
	 * Toggle the state of Decision Table merging.
	 * 
	 * @return The state of merging after completing this call
	 */
	public boolean toggleMerging() {
		if (!isMerged) {
			isMerged = true;
			clearSelection();
			assertModelMerging();
			gridWidget.redraw();
		} else {
			isMerged = false;
			clearSelection();
			removeModelMerging();
			gridWidget.redraw();
		}
		return isMerged;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.cell.client.ValueUpdater#update(java.lang.Object)
	 */
	public void update(Object value) {

		// Update underlying data
		for (CellValue<? extends Comparable<?>> cell : this.selections) {
			Coordinate c = cell.getCoordinate();
			data.set(c, value);
		}

		// Partial redraw
		assertModelMerging();
		int baseRowIndex = this.selections.first().getPhysicalCoordinate()
				.getRow();
		int minRedrawRow = findMinRedrawRow(baseRowIndex);
		int maxRedrawRow = findMaxRedrawRow(baseRowIndex);

		// When merged cells become unmerged (if their value is
		// cleared need to ensure the re-draw range is at least
		// as large as the selection range
		if (maxRedrawRow < this.selections.last().getPhysicalCoordinate()
				.getRow()) {
			maxRedrawRow = this.selections.last().getPhysicalCoordinate()
					.getRow();
		}
		gridWidget.redrawRows(minRedrawRow, maxRedrawRow);
		gridWidget.selectCell(selections.first());
	}

	/**
	 * Update the Decision Table model with the data contained in the grid
	 */
	public void updateModel() {

		// Clear existing definition
		model.getMetadataCols().clear();
		model.getAttributeCols().clear();
		model.getConditionCols().clear();
		model.getActionCols().clear();

		// TODO Move into GuidedDecisionTable model
		RowNumberCol rnCol = null;
		DescriptionCol descCol = null;

		// Extract column information
		for (DynamicColumn column : gridWidget.getColumns()) {
			DTColumnConfig modelCol = column.getModelColumn();
			if (modelCol instanceof RowNumberCol) {
				rnCol = (RowNumberCol) modelCol;

			} else if (modelCol instanceof DescriptionCol) {
				descCol = (DescriptionCol) modelCol;

			} else if (modelCol instanceof MetadataCol) {
				MetadataCol tc = (MetadataCol) modelCol;
				model.getMetadataCols().add(tc);

			} else if (modelCol instanceof AttributeCol) {
				AttributeCol tc = (AttributeCol) modelCol;
				model.getAttributeCols().add(tc);

			} else if (modelCol instanceof ConditionCol) {
				ConditionCol tc = (ConditionCol) modelCol;
				model.getConditionCols().add(tc);

			} else if (modelCol instanceof ActionCol) {
				ActionCol tc = (ActionCol) modelCol;
				model.getActionCols().add(tc);

			}
		}

		// Copy data
		final int GRID_ROWS = gridWidget.data.size();
		String[][] data = new String[GRID_ROWS][];
		for (int iRow = 0; iRow < GRID_ROWS; iRow++) {
			DynamicDataRow dataRow = gridWidget.data.get(iRow);
			String[] row = new String[dataRow.size()];
			for (int iCol = 0; iCol < dataRow.size(); iCol++) {
				Object value = dataRow.get(iCol).getValue();
				row[iCol] = (value == null ? null : value.toString());
			}
			data[iRow] = row;
		}
		this.model.setData(data);
	}

	/**
	 * Update values of "static" columns (e.g. row number and salience)
	 */
	public void updateStaticColumnValues() {

		for (DynamicColumn col : gridWidget.getColumns()) {

			DTColumnConfig modelColumn = col.getModelColumn();

			if (modelColumn instanceof RowNumberCol) {

				// Update Row Number column values
				for (int iRow = 0; iRow < data.size(); iRow++) {
					data.get(iRow).get(col.getColumnIndex()).setValue(iRow + 1);
				}

			} else if (modelColumn instanceof AttributeCol) {

				// Update Salience values
				AttributeCol attrCol = (AttributeCol) modelColumn;
				if (attrCol.attr.equals(RuleAttributeWidget.SALIENCE_ATTR)) {
					if (attrCol.isUseRowNumber()) {
						final int MAX_ROWS = data.size();
						for (int iRow = 0; iRow < data.size(); iRow++) {
							int salience = iRow + 1;
							if (attrCol.isReverseOrder()) {
								salience = Math.abs(iRow - MAX_ROWS);
							}
							data.get(iRow).get(col.getColumnIndex())
									.setValue(salience);
						}
					}
					// Ensure Salience cells are rendered with the correct Cell
					col.setCell(CellFactory.getInstance().getCell(attrCol,
							this, sce));
					col.setRequiresFullRedraw(attrCol.isUseRowNumber());
				}
			}
		}
	}

	// The DecisionTableHeaderWidget and DecisionTableSidebarWidget need to be
	// resized when MergableGridWidget has scrollbars
	private void assertDimensions() {
		headerWidget.setWidth(scrollPanel.getElement().getClientWidth() + "px");
		sidebarWidget.setHeight(scrollPanel.getElement().getClientHeight()
				+ "px");
	}

	// Here lays a can of worms! Each cell in the Decision Table
	// has three coordinates: (1) The physical coordinate, (2) The
	// coordinate relating to the HTML table element and (3) The
	// coordinate mapping a HTML table element back to the physical
	// coordinate. For example a cell could have the (1) physical
	// coordinate (0,0) which equates to (2) HTML element (0,1) in
	// which case the cell at physical coordinate (0,1) would
	// have a (3) mapping back to (0,0).
	private void assertModelIndexes() {

		for (int iRow = 0; iRow < data.size(); iRow++) {
			DynamicDataRow row = data.get(iRow);
			int colCount = 0;
			for (int iCol = 0; iCol < row.size(); iCol++) {

				int newRow = iRow;
				int newCol = colCount;
				CellValue<? extends Comparable<?>> indexCell = row.get(iCol);

				// Don't index hidden columns; indexing is used to
				// map between HTML elements and the data behind
				DynamicColumn column = gridWidget.getColumns().get(iCol);
				if (column.getIsVisible()) {

					if (indexCell.getRowSpan() != 0) {
						newRow = iRow;
						newCol = colCount++;

						CellValue<? extends Comparable<?>> cell = data.get(
								newRow).get(newCol);
						cell.setPhysicalCoordinate(new Coordinate(iRow, iCol));

					} else {
						DynamicDataRow priorRow = data.get(iRow - 1);
						CellValue<? extends Comparable<?>> priorCell = priorRow
								.get(iCol);
						Coordinate priorHtmlCoordinate = priorCell
								.getHtmlCoordinate();
						newRow = priorHtmlCoordinate.getRow();
						newCol = priorHtmlCoordinate.getCol();
					}
				}
				indexCell.setCoordinate(new Coordinate(iRow, iCol));
				indexCell.setHtmlCoordinate(new Coordinate(newRow, newCol));
			}
		}
	}

	// Ensure merging is reflected in the entire model
	private void assertModelMerging() {

		final int minRowIndex = 0;
		final int maxRowIndex = data.size() - 1;

		for (int iCol = 0; iCol < gridWidget.getColumns().size(); iCol++) {
			for (int iRow = minRowIndex; iRow <= maxRowIndex; iRow++) {

				int rowSpan = 1;
				CellValue<?> cell1 = data.get(iRow).get(iCol);
				if (iRow + rowSpan < data.size()) {

					CellValue<?> cell2 = data.get(iRow + rowSpan).get(iCol);

					// Don't merge empty cells
					if (isMerged && !cell1.isEmpty() && !cell2.isEmpty()) {
						while (cell1.getValue().equals(cell2.getValue())
								&& iRow + rowSpan < maxRowIndex) {
							cell2.setRowSpan(0);
							rowSpan++;
							cell2 = data.get(iRow + rowSpan).get(iCol);
						}
						if (cell1.getValue().equals(cell2.getValue())) {
							cell2.setRowSpan(0);
							rowSpan++;
						}
					}
					cell1.setRowSpan(rowSpan);
					iRow = iRow + rowSpan - 1;
				} else {
					cell1.setRowSpan(rowSpan);
				}
			}
		}

		// Set indexes after merging has been corrected
		// TODO Could this be incorporated into here?
		assertModelIndexes();

	}

	// ************** DEBUG
	@SuppressWarnings("unused")
	private void dumpIndexes() {
		System.out.println("coordinates");
		System.out.println("-----------");
		for (int iRow = 0; iRow < data.size(); iRow++) {
			DynamicDataRow row = data.get(iRow);
			for (int iCol = 0; iCol < row.size(); iCol++) {
				CellValue<? extends Comparable<?>> cell = row.get(iCol);

				Coordinate c = cell.getCoordinate();
				int rowSpan = cell.getRowSpan();

				System.out.print(c.toString());
				System.out.print("-S" + rowSpan + " ");
			}
			System.out.print("\n");
		}

		System.out.println();
		System.out.println("htmlToDataMap");
		System.out.println("-------------");
		for (int iRow = 0; iRow < data.size(); iRow++) {
			DynamicDataRow row = data.get(iRow);
			for (int iCol = 0; iCol < row.size(); iCol++) {
				CellValue<? extends Comparable<?>> cell = row.get(iCol);

				Coordinate c = cell.getPhysicalCoordinate();
				int rowSpan = cell.getRowSpan();

				System.out.print(c.toString());
				System.out.print("-S" + rowSpan + " ");
			}
			System.out.print("\n");
		}

		System.out.println();
		System.out.println("dataToHtmlMap");
		System.out.println("-------------");
		for (int iRow = 0; iRow < data.size(); iRow++) {
			DynamicDataRow row = data.get(iRow);
			for (int iCol = 0; iCol < row.size(); iCol++) {
				CellValue<? extends Comparable<?>> cell = row.get(iCol);

				Coordinate c = cell.getHtmlCoordinate();
				int rowSpan = cell.getRowSpan();

				System.out.print(c.toString());
				System.out.print("-S" + rowSpan + " ");
			}
			System.out.print("\n");
		}

	}

	// ************** DEBUG
	@SuppressWarnings("unused")
	private void dumpSelections(String title) {
		System.out.println(title);
		System.out.println();
		for (CellValue<? extends Comparable<?>> cell : selections) {
			System.out.println(cell.getCoordinate().toString());
		}
		System.out.println();
	}

	// Ensure Coordinates are the extents of merged cell
	private void extendSelection(Coordinate coordinate) {
		CellValue<?> startCell = data.get(coordinate);
		CellValue<?> endCell = startCell;
		while (startCell.getRowSpan() == 0) {
			startCell = data.get(startCell.getCoordinate().getRow() - 1).get(
					startCell.getCoordinate().getCol());
		}

		if (startCell.getRowSpan() > 1) {
			endCell = data
					.get(startCell.getCoordinate().getRow()
							+ startCell.getRowSpan() - 1).get(
							startCell.getCoordinate().getCol());
		}
		selectRange(startCell, endCell);
	}

	// Find the right-most index for an Action column
	private int findActionColumnIndex() {
		int index = gridWidget.getColumns().size();
		return index;
	}

	// Find the right-most index for a Attribute column
	private int findAttributeColumnIndex() {
		int index = 0;
		for (int iCol = 0; iCol < gridWidget.getColumns().size(); iCol++) {
			DynamicColumn column = gridWidget.getColumns().get(iCol);
			DTColumnConfig modelColumn = column.getModelColumn();
			if (modelColumn instanceof MetadataCol) {
				index = iCol;
			} else if (modelColumn instanceof AttributeCol) {
				index = iCol;
			}
		}
		return index + 1;
	}

	// Find the right-most index for a Condition column
	private int findConditionColumnIndex() {
		int index = 0;
		for (int iCol = 0; iCol < gridWidget.getColumns().size(); iCol++) {
			DynamicColumn column = gridWidget.getColumns().get(iCol);
			DTColumnConfig modelColumn = column.getModelColumn();
			if (modelColumn instanceof MetadataCol) {
				index = iCol;
			} else if (modelColumn instanceof AttributeCol) {
				index = iCol;
			} else if (modelColumn instanceof ConditionCol) {
				index = iCol;
			}
		}
		return index + 1;
	}

	// Given a base row find the maximum row that needs to be re-rendered based
	// upon each columns merged cells; where each merged cell passes through the
	// base row
	private int findMaxRedrawRow(int baseRowIndex) {

		int maxRedrawRow = baseRowIndex;
		DynamicDataRow baseRow = data.get(baseRowIndex);
		for (int iCol = 0; iCol < baseRow.size(); iCol++) {
			int iRow = baseRowIndex;
			CellValue<? extends Comparable<?>> cell = baseRow.get(iCol);
			while (cell.getRowSpan() != 1 && iRow < data.size() - 1) {
				iRow++;
				DynamicDataRow row = data.get(iRow);
				cell = row.get(iCol);
			}
			maxRedrawRow = (iRow > maxRedrawRow ? iRow : maxRedrawRow);
		}
		return maxRedrawRow;
	}

	// Find the right-most index for a Metadata column
	private int findMetadataColumnIndex() {
		int index = 0;
		for (int iCol = 0; iCol < gridWidget.getColumns().size(); iCol++) {
			DynamicColumn column = gridWidget.getColumns().get(iCol);
			DTColumnConfig modelColumn = column.getModelColumn();
			if (modelColumn instanceof MetadataCol) {
				index = iCol;
			}
		}
		return index + 1;
	}

	// Given a base row find the minimum row that needs to be re-rendered based
	// upon each columns merged cells; where each merged cell passes through the
	// base row
	private int findMinRedrawRow(int baseRowIndex) {

		int minRedrawRow = baseRowIndex;
		DynamicDataRow baseRow = data.get(baseRowIndex);
		for (int iCol = 0; iCol < baseRow.size(); iCol++) {
			int iRow = baseRowIndex;
			CellValue<? extends Comparable<?>> cell = baseRow.get(iCol);
			while (cell.getRowSpan() != 1 && iRow > 0) {
				iRow--;
				DynamicDataRow row = data.get(iRow);
				cell = row.get(iCol);
			}
			minRedrawRow = (iRow < minRedrawRow ? iRow : minRedrawRow);
		}
		return minRedrawRow;
	}

	// Insert a new column at the specified index.
	private void insertColumnBefore(DTColumnConfig modelColumn, int index) {

		// Add column to data
		for (int iRow = 0; iRow < data.size(); iRow++) {
			CellValue<?> cell = CellValueFactory.getInstance().getCellValue(
					modelColumn, iRow, index, modelColumn.getDefaultValue());
			data.get(iRow).add(index, cell);
		}

		// Create new column for grid
		DynamicColumn column = new DynamicColumn(modelColumn, CellFactory
				.getInstance().getCell(modelColumn, this, sce), index);

		// Redraw
		gridWidget.insertColumnBefore(index, column);
		assertModelIndexes();
		gridWidget.redrawColumns(index, gridWidget.getColumns().size() - 1);

		assertDimensions();
	}

	// Remove merging from model
	private void removeModelMerging() {

		for (int iCol = 0; iCol < gridWidget.getColumns().size(); iCol++) {
			for (int iRow = 0; iRow < data.size(); iRow++) {
				CellValue<?> cell = data.get(iRow).get(iCol);
				Coordinate c = new Coordinate(iRow, iCol);
				cell.setCoordinate(c);
				cell.setHtmlCoordinate(c);
				cell.setPhysicalCoordinate(c);
				cell.setRowSpan(1);
			}
		}

		// Set indexes after merging has been corrected
		// TODO Could this be incorporated into here?
		assertModelIndexes();
	}

	// Select a range of cells between the two coordinates.
	private void selectRange(CellValue<?> startCell, CellValue<?> endCell) {
		int col = startCell.getCoordinate().getCol();
		for (int iRow = startCell.getCoordinate().getRow(); iRow <= endCell
				.getCoordinate().getRow(); iRow++) {
			CellValue<?> cell = data.get(iRow).get(col);
			selections.add(cell);

			// Redraw selected cell
			cell.setSelected(true);
			gridWidget.selectCell(cell);
		}
	}

	// Set height of outer most Widget and related children
	private void setHeight(int height) {
		mainPanel.setHeight(height + "px");
		scrollPanel.setHeight((height - style.spacerHeight()) + "px");
		mainFocusPanel.setHeight(height + "px");

		// The Sidebar and Header sizes are derived from the ScrollPanel
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {

			public void execute() {
				assertDimensions();
			}

		});
	}

	// Set width of outer most Widget and related children
	private void setWidth(int width) {
		mainPanel.setWidth(width + "px");
		scrollPanel.setWidth((width - style.spacerWidth()) + "px");
		mainFocusPanel.setWidth(width + "px");

		// The Sidebar and Header sizes are derived from the ScrollPanel
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {

			public void execute() {
				assertDimensions();
			}

		});
	}

	/**
	 * Gets the Widgets inner panel to which the DecisionTable and Header will
	 * be added. This allows subclasses to have some control over the internal
	 * layout of the Decision Table.
	 * 
	 * @return
	 */
	protected abstract Panel getBodyPanel();

	/**
	 * Gets the Widget responsible for rendering the DecisionTables "grid".
	 * 
	 * @return
	 */
	protected abstract MergableGridWidget getGridWidget();

	/**
	 * Gets the Widget responsible for rendering the DecisionTables "header".
	 * 
	 * @return
	 */
	protected abstract DecisionTableHeaderWidget getHeaderWidget();

	/**
	 * Gets the Widget's outer most panel to which other content will be added.
	 * This allows subclasses to have some control over the general layout of
	 * the Decision Table.
	 * 
	 * @return
	 */
	protected abstract Panel getMainPanel();

	/**
	 * The DecisionTable is nested inside a ScrollPanel. This allows
	 * ScrollEvents to be hooked up to other defendant controls (e.g. the
	 * Header).
	 * 
	 * @return
	 */
	protected abstract ScrollHandler getScrollHandler();

	/**
	 * Gets the Widget responsible for rendering the DecisionTables "side-bar".
	 * 
	 * @return
	 */
	protected abstract DecisionTableSidebarWidget getSidebarWidget();

}
