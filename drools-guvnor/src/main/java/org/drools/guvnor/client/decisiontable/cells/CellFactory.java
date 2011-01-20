/*
 * Copyright 2011 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.guvnor.client.decisiontable.cells;

import java.util.Date;

import org.drools.guvnor.client.decisiontable.widget.DecisionTableWidget;
import org.drools.guvnor.client.modeldriven.ui.RuleAttributeWidget;
import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.ide.common.client.modeldriven.dt.ActionInsertFactCol;
import org.drools.ide.common.client.modeldriven.dt.ActionSetFieldCol;
import org.drools.ide.common.client.modeldriven.dt.AttributeCol;
import org.drools.ide.common.client.modeldriven.dt.ConditionCol;
import org.drools.ide.common.client.modeldriven.dt.DTColumnConfig;
import org.drools.ide.common.client.modeldriven.dt.GuidedDecisionTable;
import org.drools.ide.common.client.modeldriven.dt.RowNumberCol;

import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;

/**
 * A Factory to provide the Cell specific to a given coordinate.
 * 
 * @author manstis
 * 
 */
public class CellFactory {

	private static String[] DIALECTS = { "java", "mvel" };

	// The Singleton
	private static CellFactory instance;

	// Singleton constructor
	public static synchronized CellFactory getInstance() {
		if (instance == null) {
			instance = new CellFactory();
		}
		return instance;
	}

	private CellFactory() {
	}

	/**
	 * Create a Cell for the given DTColumnConfig
	 * 
	 * @param column
	 *            The Decision Table model column
	 * @param dtable
	 *            The Decision Table
	 * @return A Cell
	 */
	public DecisionTableCellValueAdaptor<? extends Comparable<?>> getCell(
			DTColumnConfig column, DecisionTableWidget dtable) {

		DecisionTableCellValueAdaptor<? extends Comparable<?>> cell = makeTextCell();

		if (column instanceof RowNumberCol) {
			cell = makeRowNumberCell();

		} else if (column instanceof AttributeCol) {
			AttributeCol attrCol = (AttributeCol) column;
			String attrName = attrCol.attr;
			if (attrName.equals(RuleAttributeWidget.SALIENCE_ATTR)) {
				if (attrCol.isUseRowNumber()) {
					cell = makeRowNumberCell();
				} else {
					cell = makeNumericCell();
				}
			} else if (attrName.equals(RuleAttributeWidget.ENABLED_ATTR)) {
				cell = makeBooleanCell();
			} else if (attrName.equals(RuleAttributeWidget.NO_LOOP_ATTR)) {
				cell = makeBooleanCell();
			} else if (attrName.equals(RuleAttributeWidget.DURATION_ATTR)) {
				cell = makeNumericCell();
			} else if (attrName.equals(RuleAttributeWidget.AUTO_FOCUS_ATTR)) {
				cell = makeBooleanCell();
			} else if (attrName.equals(RuleAttributeWidget.LOCK_ON_ACTIVE_ATTR)) {
				cell = makeBooleanCell();
			} else if (attrName.equals(RuleAttributeWidget.DATE_EFFECTIVE_ATTR)) {
				cell = makeDateCell();
			} else if (attrName.equals(RuleAttributeWidget.DATE_EXPIRES_ATTR)) {
				cell = makeDateCell();
			} else if (attrName.equals(RuleAttributeWidget.DIALECT_ATTR)) {
				cell = makeDialectCell();
			}

		} else if (column instanceof ConditionCol) {
			cell = makeNewCell(column, dtable);

		} else if (column instanceof ActionSetFieldCol) {
			cell = makeNewCell(column, dtable);

		} else if (column instanceof ActionInsertFactCol) {
			cell = makeNewCell(column, dtable);

		}

		cell.setDecisionTableWidget(dtable);
		return cell;

	}

	// Make a new Cell for Boolean columns
	private DecisionTableCellValueAdaptor<? extends Comparable<?>> makeBooleanCell() {
		return new DecisionTableCellValueAdaptor<Boolean>(new CheckboxCell());
	}

	// Make a new Cell for Date columns
	private DecisionTableCellValueAdaptor<? extends Comparable<?>> makeDateCell() {
		return new DecisionTableCellValueAdaptor<Date>(new PopupDateEditCell(
				DateTimeFormat.getFormat(PredefinedFormat.DATE_SHORT)));
	}

	// Make a new Cell for Dialect columns
	private DecisionTableCellValueAdaptor<? extends Comparable<?>> makeDialectCell() {
		PopupDropDownEditCell pudd = new PopupDropDownEditCell();
		pudd.setItems(DIALECTS);
		return new DecisionTableCellValueAdaptor<String>(pudd);
	}

	// Make a new Cell for Condition and Actions columns
	private DecisionTableCellValueAdaptor<? extends Comparable<?>> makeNewCell(
			DTColumnConfig col, DecisionTableWidget dtable) {

		GuidedDecisionTable model = dtable.getModel();
		SuggestionCompletionEngine sce = dtable.getSCE();
		DecisionTableCellValueAdaptor<? extends Comparable<?>> cell = makeTextCell();

		// Columns with lists of values, enums etc are always Text (for now)
		String[] vals = model.getValueList(col, sce);
		if (vals.length == 0) {
			if (model.isNumeric(col, sce)) {
				cell = makeNumericCell();
			} else if (model.isBoolean(col, sce)) {
				cell = makeBooleanCell();
			} else if (model.isDate(col, sce)) {
				cell = makeDateCell();
			}
		} else {
			PopupDropDownEditCell pudd = new PopupDropDownEditCell();
			pudd.setItems(vals);
			cell = new DecisionTableCellValueAdaptor<String>(pudd);
		}
		return cell;
	}

	// Make a new Cell for Numeric columns
	private DecisionTableCellValueAdaptor<? extends Comparable<?>> makeNumericCell() {
		return new DecisionTableCellValueAdaptor<Integer>(
				new PopupNumericEditCell());
	}

	// Make a new Cell for String columns
	private DecisionTableCellValueAdaptor<? extends Comparable<?>> makeRowNumberCell() {
		return new DecisionTableCellValueAdaptor<Integer>(new RowNumberCell());
	}

	// Make a new Cell for a RowNumberCol
	private DecisionTableCellValueAdaptor<? extends Comparable<?>> makeTextCell() {
		return new DecisionTableCellValueAdaptor<String>(
				new PopupTextEditCell());
	}

}