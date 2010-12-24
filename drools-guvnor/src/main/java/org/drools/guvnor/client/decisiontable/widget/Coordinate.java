package org.drools.guvnor.client.decisiontable.widget;

/**
 * A coordinate
 * 
 * @author manstis
 * 
 */
public class Coordinate {
	private int row;
	private int col;
	private String displayString;

	Coordinate(Coordinate c) {
		this.row = c.row;
		this.col = c.col;
		this.displayString = "(R" + c.row + ",C" + c.col + ")";
	}

	Coordinate(int row, int col) {
		this.row = row;
		this.col = col;
		this.displayString = "(R" + row + ",C" + col + ")";
	}

	@Override
	public boolean equals(Object o) {
		if (o == null) {
			return false;
		}
		Coordinate c = (Coordinate) o;
		return c.col == col && c.row == row;
	}

	public int getCol() {
		return this.col;
	}

	public int getRow() {
		return this.row;
	}

	@Override
	public int hashCode() {
		int hash = row;
		hash = 31 * hash + col;
		return hash;
	}

	@Override
	public String toString() {
		return displayString;
	}

}