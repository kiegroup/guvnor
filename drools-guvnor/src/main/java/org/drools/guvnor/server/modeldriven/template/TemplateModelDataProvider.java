package org.drools.guvnor.server.modeldriven.template;

import org.drools.guvnor.client.modeldriven.dt.TemplateModel;
import org.drools.template.DataProvider;

public class TemplateModelDataProvider implements DataProvider {

	private String[][] rows;
	private int rowsCount;
	private int currRow = 0;

	public TemplateModelDataProvider(TemplateModel model) {
		this.rows = model.getTableAsArray();
		rowsCount = model.getRowsCount();
	}
	
	public boolean hasNext() {
		return rowsCount != -1 && currRow < rowsCount;
	}

	public String[] next() {
		return rows[currRow++];
	}
}
