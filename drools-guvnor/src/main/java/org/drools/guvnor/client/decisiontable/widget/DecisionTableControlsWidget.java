package org.drools.guvnor.client.decisiontable.widget;

import org.drools.ide.common.client.modeldriven.dt.ActionCol;
import org.drools.ide.common.client.modeldriven.dt.AttributeCol;
import org.drools.ide.common.client.modeldriven.dt.ConditionCol;
import org.drools.ide.common.client.modeldriven.dt.DTColumnConfig;
import org.drools.ide.common.client.modeldriven.dt.MetadataCol;

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Simple container for controls to manipulate a Decision Table
 * 
 * @author manstis
 * 
 */
public class DecisionTableControlsWidget extends Composite {

	/**
	 * Simple Widget allowing selection from a list and execution of a command
	 * 
	 * @author manstis
	 * 
	 */
	private static class ColumnPicker extends Composite {

		private Panel panel = new VerticalPanel();
		private Button btnEnter = new Button();
		private ListBox lstOptions = new ListBox(false);

		private ColumnPicker(String buttonLabel) {
			this.btnEnter.setText(buttonLabel);
			this.lstOptions.setVisibleItemCount(1);
			panel.add(btnEnter);
			panel.add(lstOptions);
			initWidget(panel);
		}

		private String getValue() {
			String value = lstOptions
					.getItemText(lstOptions.getSelectedIndex());
			return value;
		}

		private void setCommand(final Command command) {
			this.btnEnter.addClickHandler(new ClickHandler() {

				public void onClick(ClickEvent event) {
					command.execute();
				}

			});
		}

		private void setOptions(String[] items) {
			for (String item : items) {
				this.lstOptions.addItem(item);
			}
		}

	}

	/**
	 * Simple Widget allowing entering of Fact details (Class and Field)
	 * 
	 * @author manstis
	 * 
	 */
	private static class FactPicker extends Composite {

		private Panel panel = new VerticalPanel();
		private Button btnEnter = new Button();
		private TextBox txtFactType = new TextBox();
		private TextBox txtFactField = new TextBox();

		private FactPicker() {
			this.btnEnter.setText("Add Fact column");
			panel.add(btnEnter);

			Panel pnlFactType = new HorizontalPanel();
			Label lblFactType = new Label("Fact Type:");
			lblFactType.getElement().getStyle().setDisplay(Display.INLINE);
			pnlFactType.add(lblFactType);
			pnlFactType.add(txtFactType);

			Panel pnlFactField = new HorizontalPanel();
			Label lblFactField = new Label("Fact Field:");
			lblFactField.getElement().getStyle().setDisplay(Display.INLINE);
			pnlFactField.add(lblFactField);
			pnlFactField.add(txtFactField);

			panel.add(pnlFactType);
			panel.add(pnlFactField);
			initWidget(panel);
		}

		private String getFactField() {
			String value = txtFactField.getText();
			return value;
		}

		private String getFactType() {
			String value = txtFactType.getText();
			return value;
		}

		private void setCommand(final Command command) {
			this.btnEnter.addClickHandler(new ClickHandler() {

				public void onClick(ClickEvent event) {
					command.execute();
				}

			});
		}

	}

	/**
	 * Control allowing entry of numerical value and button to invoke specified
	 * Command.
	 * 
	 * @author manstis
	 * 
	 */
	private static class NumberRequestor extends Composite {

		private Panel panel = new VerticalPanel();
		private Button btnEnter = new Button();
		private TextBox txtNumber = new TextBox();

		private NumberRequestor(final String buttonLabel) {
			this.btnEnter.setText(buttonLabel);
			this.txtNumber.setText("0");
			this.txtNumber.setWidth("64px");
			this.txtNumber.addKeyPressHandler(new KeyPressHandler() {

				private final RegExp pattern = RegExp.compile("\\d+");

				public void onKeyPress(KeyPressEvent event) {
					// Only numerical digits allowed
					if (!pattern.test(String.valueOf(event.getCharCode()))) {
						event.preventDefault();
					}
				}

			});
			panel.add(btnEnter);
			panel.add(txtNumber);
			initWidget(panel);
		}

		private int getValue() {
			String text = txtNumber.getText();
			if (text.equals("")) {
				return 0;
			}
			return Integer.parseInt(text);
		}

		private void setCommand(final Command command) {
			this.btnEnter.addClickHandler(new ClickHandler() {

				public void onClick(ClickEvent event) {
					command.execute();
				}

			});
		}

	}

	private Panel panel = new HorizontalPanel();

	private static final String[] ATTRIBUTE_OPTIONS = { "salience", "enabled",
			"date-effective", "date-expires", "no-loop", "agenda-group",
			"activation-group", "duration", "auto-focus", "lock-on-active",
			"ruleflow-group", "dialect" };

	public DecisionTableControlsWidget(final DecisionTableWidget dtable) {

		// Add Metadata column button
		Button btnAddMetadataColumn = new Button("Add Metadata Column",
				new ClickHandler() {

					public void onClick(ClickEvent event) {
						dtable.clearSelection();
						dtable.addColumn(getNewMetadataColumn());
					}
				});

		// Add Attribute column button
		final ColumnPicker attributeColumnPicker = new ColumnPicker(
				"Add Attribute Column");
		attributeColumnPicker.setOptions(ATTRIBUTE_OPTIONS);
		attributeColumnPicker.setCommand(new Command() {

			public void execute() {
				dtable.clearSelection();
				dtable.addColumn(getNewAttributeColumn(attributeColumnPicker
						.getValue()));
			}

		});

		// Add Condition column button
		final FactPicker factPicker = new FactPicker();
		factPicker.setCommand(new Command() {

			public void execute() {
				dtable.clearSelection();
				dtable.addColumn(getNewConditionColumn(
						factPicker.getFactType(), factPicker.getFactField()));
			}

		});

		// Add Action column button
		Button btnAddActionColumn = new Button("Add Action Column",
				new ClickHandler() {

					public void onClick(ClickEvent event) {
						dtable.clearSelection();
						dtable.addColumn(getNewActionColumn());
					}
				});

		// Add row button
		Button btnAddRow = new Button("Add Row", new ClickHandler() {

			public void onClick(ClickEvent event) {
				dtable.clearSelection();
				dtable.addRow();
			}
		});

		// Hide column button
		final NumberRequestor hideColumnWidget = new NumberRequestor(
				"Hide column:");
		hideColumnWidget.setCommand(new Command() {

			public void execute() {
				dtable.hideColumn(hideColumnWidget.getValue());
			}

		});

		// Show column button
		final NumberRequestor showColumnWidget = new NumberRequestor(
				"Show column:");
		showColumnWidget.setCommand(new Command() {

			public void execute() {
				dtable.showColumn(showColumnWidget.getValue());
			}

		});

		VerticalPanel vp1 = new VerticalPanel();
		vp1.add(btnAddMetadataColumn);
		vp1.add(btnAddActionColumn);
		vp1.add(attributeColumnPicker);
		panel.add(vp1);

		VerticalPanel vp2 = new VerticalPanel();
		vp2.add(factPicker);
		panel.add(vp2);

		VerticalPanel vp3 = new VerticalPanel();
		vp3.add(hideColumnWidget);
		vp3.add(showColumnWidget);
		panel.add(vp3);

		panel.add(btnAddRow);

		initWidget(panel);

	}

	private DTColumnConfig getNewActionColumn() {
		ActionCol column = new ActionCol();
		column.setHeader("action");
		return column;
	}

	private DTColumnConfig getNewAttributeColumn(String type) {
		AttributeCol column = new AttributeCol();
		column.attr = type;
		return column;
	}

	private DTColumnConfig getNewConditionColumn(String factType,
			String factField) {
		ConditionCol column = new ConditionCol();
		column.setFactType(factType);
		column.setFactField(factField);
		return column;
	}

	private DTColumnConfig getNewMetadataColumn() {
		MetadataCol column = new MetadataCol();
		column.attr = "metadata";
		return column;
	}

}
