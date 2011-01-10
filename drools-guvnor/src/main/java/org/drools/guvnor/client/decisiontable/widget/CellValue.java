package org.drools.guvnor.client.decisiontable.widget;

/**
 * This is a wrapper around a value (generics yet to be implemented). The
 * wrapper provides additional information required to use the vanilla value in
 * a Decision Table with merge capabilities.
 * 
 * One coordinate is maintained and two indexes to map to and from HTML table
 * coordinates. The indexes used to be maintained in SelectionManager however it
 * required two more N x N collections of "mapping" objects in addition to that
 * containing the actual data.
 * 
 * The coordinate represents the physical location of the cell on an (R, C)
 * grid. One index maps the physical coordinate of the cell to the logical
 * coordinate of the HTML table whilst the other index maps from the logical
 * coordinate to the physical cell.
 * 
 * For example, given data (0,0), (0,1), (1,0) and (1,1) with cell at (0,0)
 * merged into (1,0) only the HTML coordinates (0,0), (0,1) and (1,0) exist;
 * with physical coordinates (0,0) and (1,0) relating to HTML coordinate (0,0)
 * which has a row span of 2. Therefore physical cells (0,0) and (1,0) have a
 * <code>mapDataToHtml</code> coordinate of (0,0) whilst physical cell (1,0) has
 * a <code>mapHtmlToData</code> coordinate of (1,1).
 * 
 * @author manstis
 * 
 */
public class CellValue<T extends Comparable<T>> implements
		Comparable<CellValue<T>> {
	private T value;
	private int rowSpan = 1;
	private Coordinate coordinate;
	private Coordinate mapHtmlToData;
	private Coordinate mapDataToHtml;
	private boolean isSelected;

	public CellValue(T value, int row, int col) {
		this.value = value;
		this.coordinate = new Coordinate(row, col);
		this.mapHtmlToData = new Coordinate(row, col);
		this.mapDataToHtml = new Coordinate(row, col);
	}

	public int compareTo(CellValue<T> cv) {
		if (this.value == null) {
			if (cv.value == null) {
				return 0;
			}
			return 1;
		} else {
			if (cv.value == null) {
				return -1;
			}
		}
		return this.value.compareTo(cv.value);
	}

	public T getValue() {
		return this.value;
	}

	public void setHtmlCoordinate(Coordinate c) {
		this.mapDataToHtml = c;
	}

	@SuppressWarnings("unchecked")
	public void setValue(Object value) {
		this.value = (T) value;
	}

	Coordinate getCoordinate() {
		return this.coordinate;
	}

	Coordinate getHtmlCoordinate() {
		return new Coordinate(this.mapDataToHtml);
	}

	Coordinate getPhysicalCoordinate() {
		return new Coordinate(this.mapHtmlToData);
	}

	int getRowSpan() {
		return this.rowSpan;
	}

	boolean isEmpty() {
		return this.value == null;
	}

	void setCoordinate(Coordinate coordinate) {
		this.coordinate = coordinate;
	}

	void setPhysicalCoordinate(Coordinate c) {
		this.mapHtmlToData = c;
	}

	void setRowSpan(int rowSpan) {
		this.rowSpan = rowSpan;
	}

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

}