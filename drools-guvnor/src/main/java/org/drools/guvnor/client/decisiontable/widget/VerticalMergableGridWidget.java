package org.drools.guvnor.client.decisiontable.widget;

import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.TableCellElement;
import com.google.gwt.dom.client.TableRowElement;
import com.google.gwt.dom.client.TableSectionElement;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

/**
 * A Vertical implementation of MergableGridWidget, that renders columns as erm,
 * columns and rows as rows. Supports merging of cells between rows.
 * 
 * @author manstis
 * 
 */
public class VerticalMergableGridWidget extends MergableGridWidget {

	public VerticalMergableGridWidget(DecisionTableWidget dtable) {
		super(dtable);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.drools.guvnor.decisiontable.client.widget.MergableGridWidget#deleteRow
	 * (int)
	 */
	@Override
	public void deleteRow(int index) {
		if (index < 0) {
			throw new IllegalArgumentException(
					"Index cannot be less than zero.");
		}
		if (index > data.size()) {
			throw new IllegalArgumentException(
					"Index cannot be greater than the number of rows.");
		}

		sideBarWidget.deleteSelector(index);
		tbody.deleteRow(index);
		fixRowStyles(index);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.drools.guvnor.decisiontable.client.widget.MergableGridWidget#
	 * insertRowBefore(int)
	 */
	@Override
	public void insertRowBefore(int index, DynamicDataRow rowData) {
		if (index < 0) {
			throw new IllegalArgumentException(
					"Index cannot be less than zero.");
		}
		if (index > data.size()) {
			throw new IllegalArgumentException(
					"Index cannot be greater than the number of rows.");
		}
		if (rowData == null) {
			throw new IllegalArgumentException("Row data cannot be null");
		}

		sideBarWidget.insertSelectorBefore(index);
		TableRowElement newRow = tbody.insertRow(index);
		populateTableRowElement(newRow, rowData);
		fixRowStyles(index);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.drools.guvnor.decisiontable.client.widget.MergableGridWidget#redraw()
	 */
	@Override
	public void redraw() {

		// Prepare sidebar
		sideBarWidget.initialise();

		TableSectionElement nbody = Document.get().createTBodyElement();

		for (int iRow = 0; iRow < data.size(); iRow++) {

			// Add a selector for each row
			sideBarWidget.addSelector();

			TableRowElement tre = Document.get().createTRElement();
			tre.setClassName(getRowStyle(iRow));

			DynamicDataRow rowData = data.get(iRow);
			populateTableRowElement(tre, rowData);
			nbody.appendChild(tre);

		}

		// Update table to DOM
		table.replaceChild(nbody, tbody);
		tbody = nbody;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.drools.guvnor.client.decisiontable.widget.MergableGridWidget#redrawColumn
	 * (int)
	 */
	@Override
	public void redrawColumn(int index) {
		if (index < 0) {
			throw new IllegalArgumentException(
					"Column index cannot be less than zero.");
		}
		if (index > columns.size() - 1) {
			throw new IllegalArgumentException(
					"Column index cannot be greater than the number of defined columns.");
		}

		for (int iRow = 0; iRow < data.size(); iRow++) {
			TableRowElement tre = tbody.getRows().getItem(iRow);
			DynamicDataRow rowData = data.get(iRow);
			redrawTableRowElement(rowData, tre, index, index);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.drools.guvnor.client.decisiontable.widget.MergableGridWidget#
	 * redrawColumns(int, int)
	 */
	@Override
	public void redrawColumns(int startRedrawIndex, int endRedrawIndex) {
		if (startRedrawIndex < 0) {
			throw new IllegalArgumentException(
					"Start Column index cannot be less than zero.");
		}
		if (startRedrawIndex > columns.size() - 1) {
			throw new IllegalArgumentException(
					"Start Column index cannot be greater than the number of defined columns.");
		}
		if (endRedrawIndex < 0) {
			throw new IllegalArgumentException(
					"End Column index cannot be less than zero.");
		}
		if (endRedrawIndex > columns.size() - 1) {
			throw new IllegalArgumentException(
					"End Column index cannot be greater than the number of defined columns.");
		}
		if (startRedrawIndex > endRedrawIndex) {
			throw new IllegalArgumentException(
					"Start Column index cannot be greater than End Column index.");
		}

		for (int iRow = 0; iRow < data.size(); iRow++) {
			TableRowElement tre = tbody.getRows().getItem(iRow);
			DynamicDataRow rowData = data.get(iRow);
			redrawTableRowElement(rowData, tre, startRedrawIndex,
					endRedrawIndex);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.drools.guvnor.decisiontable.client.widget.MergableGridWidget#redrawRows
	 * (int, int)
	 */
	@Override
	public void redrawRows(int startRedrawIndex, int endRedrawIndex) {
		if (startRedrawIndex < 0) {
			throw new IllegalArgumentException(
					"Start Row index cannot be less than zero.");
		}
		if (startRedrawIndex > data.size() - 1) {
			throw new IllegalArgumentException(
					"Start Row index cannot be greater than the number of rows in the table.");
		}
		if (endRedrawIndex < 0) {
			throw new IllegalArgumentException(
					"End Row index cannot be less than zero.");
		}
		if (endRedrawIndex > data.size() - 1) {
			throw new IllegalArgumentException(
					"End Row index cannot be greater than the number of rows in the table.");
		}
		if (endRedrawIndex < startRedrawIndex) {
			throw new IllegalArgumentException(
					"End Row index cannot be greater than Start Row index.");
		}

		for (int iRow = startRedrawIndex; iRow <= endRedrawIndex; iRow++) {
			TableRowElement newRow = Document.get().createTRElement();
			DynamicDataRow rowData = data.get(iRow);
			populateTableRowElement(newRow, rowData);
			tbody.replaceChild(newRow, tbody.getChild(iRow));
		}
		fixRowStyles(startRedrawIndex);
	}

	// Row styles need to be re-applied after inserting and deleting rows
	private void fixRowStyles(int iRow) {
		while (iRow < tbody.getChildCount()) {
			Element e = Element.as(tbody.getChild(iRow));
			TableRowElement tre = TableRowElement.as(e);
			tre.setClassName(getRowStyle(iRow));
			iRow++;
		}
	}

	// Get style applicable to row
	private String getRowStyle(int iRow) {
		String evenRowStyle = style.cellTableEvenRow();
		String oddRowStyle = style.cellTableOddRow();
		boolean isEven = iRow % 2 == 0;
		String trClasses = isEven ? evenRowStyle : oddRowStyle;
		return trClasses;
	}

	// Build a TableCellElement
	private TableCellElement makeTableCellElement(int iCol,
			DynamicDataRow rowData) {

		String cellStyle = style.cellTableCell();
		String divStyle = style.cellTableCellDiv();
		TableCellElement tce = null;

		// Column to render the column
		DynamicColumn column = columns.get(iCol);

		CellValue<? extends Comparable<?>> cellData = rowData.get(iCol);
		int rowSpan = cellData.getRowSpan();
		if (rowSpan > 0) {

			// Use Elements rather than Templates as it's easier to set
			// attributes that need to be dynamic
			tce = Document.get().createTDElement();
			DivElement div = Document.get().createDivElement();
			tce.setClassName(cellStyle);
			div.setClassName(divStyle);

			// Dynamic attributes!
			div.getStyle().setWidth(column.getWidth(), Unit.PX);
			tce.getStyle().setHeight(style.rowHeight() * rowSpan, Unit.PX);
			tce.setRowSpan(rowSpan);

			// Render the cell and set inner HTML
			SafeHtmlBuilder cellBuilder = new SafeHtmlBuilder();
			column.render(rowData, null, cellBuilder);
			div.setInnerHTML(cellBuilder.toSafeHtml().asString());

			// Construct the table
			tce.appendChild(div);
			tce.setTabIndex(0);
		}
		return tce;

	}

	// Populate the content of a TableRowElement. This is used to populate
	// new, empty, TableRowElements with complete rows for insertion into an
	// HTML table based upon visible columns
	private TableRowElement populateTableRowElement(TableRowElement tre,
			DynamicDataRow rowData) {

		for (int iCol = 0; iCol < columns.size(); iCol++) {
			if (columns.get(iCol).getIsVisible()) {
				TableCellElement tce = makeTableCellElement(iCol, rowData);
				if (tce != null) {
					tre.appendChild(tce);
				}
			}
		}

		return tre;

	}

	// Redraw a row adding new cells if necessary. This is used to populate part
	// of a row from the given index onwards, when a new column has been
	// inserted. It is important the indexes on the underlying data have
	// been set correctly before calling as they are used to determine the
	// correct HTML element in which to render a cell.
	private void redrawTableRowElement(DynamicDataRow rowData,
			TableRowElement tre, int startColIndex, int endColIndex) {

		for (int iCol = startColIndex; iCol <= endColIndex; iCol++) {

			// Only redraw visible columns
			DynamicColumn column = columns.get(iCol);
			if (column.getIsVisible()) {

				int maxColumnIndex = tre.getCells().getLength() - 1;
				int requiredColumnIndex = rowData.get(iCol).getHtmlCoordinate()
						.getCol();
				if (requiredColumnIndex > maxColumnIndex) {

					// Make a new TD element
					TableCellElement newCell = makeTableCellElement(iCol,
							rowData);
					if (newCell != null) {
						tre.appendChild(newCell);
					}

				} else {

					// Reuse an existing TD element
					TableCellElement newCell = makeTableCellElement(iCol,
							rowData);
					if (newCell != null) {
						TableCellElement oldCell = tre.getCells().getItem(
								requiredColumnIndex);
						tre.replaceChild(newCell, oldCell);
					}
				}
			}
		}

	}

}
