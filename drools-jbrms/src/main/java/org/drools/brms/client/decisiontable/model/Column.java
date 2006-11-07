package org.drools.brms.client.decisiontable.model;

import java.util.ArrayList;
import java.util.List;

public class Column {
	private List cells = new ArrayList();
	private DecisionTable parent;
	
	Column(DecisionTable dt) {
		this.parent = dt;
	}
	void addCell(final Cell cell) {
		cells.add(cell);
	}

	public int getIndex() {
		return parent.getColumnIndex(this);
	}
}
