package org.drools.guvnor.client.decisiontable.widget;

import java.util.ArrayList;

/**
 * A simple container for rows of data.
 * 
 * @author manstis
 * 
 */
public class DynamicData extends ArrayList<DynamicDataRow> {

	private static final long serialVersionUID = -3710491920672816057L;

	public CellValue<? extends Comparable<?>> get(Coordinate c) {
		return this.get(c.getRow()).get(c.getCol());
	}

	public void set(Coordinate c, Object value) {
		this.get(c.getRow()).get(c.getCol()).setValue(value);
	}

}
