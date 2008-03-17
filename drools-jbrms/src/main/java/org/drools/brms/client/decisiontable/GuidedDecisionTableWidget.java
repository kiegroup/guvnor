package org.drools.brms.client.decisiontable;

import java.util.Iterator;
import java.util.List;

import org.drools.brms.client.common.FormStylePopup;
import org.drools.brms.client.common.ImageButton;
import org.drools.brms.client.common.SmallLabel;
import org.drools.brms.client.modeldriven.SuggestionCompletionEngine;
import org.drools.brms.client.modeldriven.brl.ISingleFieldConstraint;
import org.drools.brms.client.modeldriven.dt.ActionCol;
import org.drools.brms.client.modeldriven.dt.ActionSetFieldCol;
import org.drools.brms.client.modeldriven.dt.AttributeCol;
import org.drools.brms.client.modeldriven.dt.ConditionCol;
import org.drools.brms.client.modeldriven.dt.GuidedDecisionTable;
import org.drools.brms.client.packages.SuggestionCompletionCache;
import org.drools.brms.client.rpc.RuleAsset;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.KeyboardListener;
import com.google.gwt.user.client.ui.KeyboardListenerAdapter;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.gwtext.client.core.EventObject;
import com.gwtext.client.core.SortDir;
import com.gwtext.client.data.ArrayReader;
import com.gwtext.client.data.FieldDef;
import com.gwtext.client.data.GroupingStore;
import com.gwtext.client.data.MemoryProxy;
import com.gwtext.client.data.Record;
import com.gwtext.client.data.RecordDef;
import com.gwtext.client.data.SortState;
import com.gwtext.client.data.Store;
import com.gwtext.client.data.StringFieldDef;
import com.gwtext.client.widgets.Component;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.Toolbar;
import com.gwtext.client.widgets.ToolbarMenuButton;
import com.gwtext.client.widgets.Window;
import com.gwtext.client.widgets.form.FieldSet;
import com.gwtext.client.widgets.form.FormPanel;
import com.gwtext.client.widgets.grid.BaseColumnConfig;
import com.gwtext.client.widgets.grid.CellMetadata;
import com.gwtext.client.widgets.grid.ColumnConfig;
import com.gwtext.client.widgets.grid.ColumnModel;
import com.gwtext.client.widgets.grid.GridPanel;
import com.gwtext.client.widgets.grid.GroupingView;
import com.gwtext.client.widgets.grid.Renderer;
import com.gwtext.client.widgets.grid.event.GridCellListenerAdapter;
import com.gwtext.client.widgets.menu.BaseItem;
import com.gwtext.client.widgets.menu.Item;
import com.gwtext.client.widgets.menu.Menu;
import com.gwtext.client.widgets.menu.event.BaseItemListenerAdapter;

/**
 * This is the new guided decision table editor for the web.
 * @author Michael Neale
 */
public class GuidedDecisionTableWidget extends Composite {

    private GuidedDecisionTable dt;
	private VerticalPanel layout;
	private GridPanel grid;
	private FieldDef[] fds;
	private VerticalPanel attributeConfigWidget;
	private VerticalPanel conditionsConfigWidget;
	private String packageName;
	private VerticalPanel actionsConfigWidget;


	public GuidedDecisionTableWidget(RuleAsset asset) {

    	this.dt = (GuidedDecisionTable) asset.content;
    	this.packageName = asset.metaData.packageName;


        layout = new VerticalPanel();

        FormPanel config = new FormPanel();
        config.setTitle("Decision table");

        config.setBodyBorder(false);
        config.setCollapsed(true);
        config.setCollapsible(true);


        FieldSet attributes = new FieldSet("Attribute columns");
        attributes.setCollapsible(true);

        attributes.setFrame(true);
        attributes.add(getAttributes());
        config.add(attributes);


        FieldSet conditions = new FieldSet("Condition columns");
        conditions.setCollapsible(true);
        conditions.add(getConditions());
        config.add(conditions);


        FieldSet actions = new FieldSet("Action columns");
        actions.setCollapsible(true);
        actions.add(getActions());
        config.add(actions);


        layout.add(config);

        refreshGrid();


        initWidget(layout);
    }

	private Widget getActions() {
		actionsConfigWidget = new VerticalPanel();
		refreshActionsWidget();
		return actionsConfigWidget;
	}

	private void refreshActionsWidget() {
		this.actionsConfigWidget.clear();
		for (int i = 0; i < dt.actionCols.size(); i++) {
			ActionCol c = (ActionCol) dt.actionCols.get(i);
			HorizontalPanel hp = new HorizontalPanel();
			hp.add(removeAction(c));
			hp.add(editAction(c));
			hp.add(new SmallLabel(c.header));
			actionsConfigWidget.add(hp);
		}
		actionsConfigWidget.add(newAction());

	}

	private Widget editAction(ActionCol c) {
		return new Image("images/edit.gif");
	}

	private Widget newAction() {
		return new ImageButton( "images/new_item.gif", "Create a new action column", new ClickListener() {
			public void onClick(Widget w) {
				final FormStylePopup pop = new FormStylePopup();
				pop.setModal(false);

				final ListBox choice = new ListBox();
				choice.addItem("Set the value of a field", "set");
				choice.addItem("Set the value of a field on a new fact", "insert");
				choice.addItem("Retract an existing fact", "retract");
				Button ok = new Button("OK");
				ok.addClickListener(new ClickListener() {
					public void onClick(Widget w) {
						String s = choice.getValue(choice.getSelectedIndex());
						if (s.equals("set")) {
							showSet();
						} else if (s.equals("insert")) {
							showInsert();
						} else if (s.equals("retract")) {
							showRetract();
						}
						pop.hide();
					}

					private void showRetract() {
						// TODO Auto-generated method stub

					}

					private void showInsert() {
						// TODO Auto-generated method stub

					}

					private void showSet() {
						ActionSetColumn set = new ActionSetColumn(getSCE(), dt, new Command() {
							public void execute() {
								//want to add in a blank row into the data
								scrapeData(dt.attributeCols.size() + dt.conditionCols.size() + dt.actionCols.size() + 1);
								refreshGrid();
								refreshActionsWidget();
							}
						}, new ActionSetFieldCol(), true);
						set.show();
					}
				});

				pop.addAttribute("Type of action column:", choice);

				pop.addAttribute("", ok);

				pop.show();

			}

		});


	}

	private Widget removeAction(final ActionCol c) {
		Image del = new ImageButton("images/delete_item_small.gif", "Remove this action column", new ClickListener() {
			public void onClick(Widget w) {
				if (com.google.gwt.user.client.Window.confirm("Are you sure you want to delete the column for " + c.header + " - all data in that column will be removed?")) {
					dt.actionCols.remove(c);
					removeField(c.header);
					scrapeData(-1);
					refreshGrid();
					refreshActionsWidget();
				}
			}
		});

		return del;
	}

	private Widget getConditions() {
		conditionsConfigWidget = new VerticalPanel();
		refreshConditionsWidget();
		return conditionsConfigWidget;
	}

	private void refreshConditionsWidget() {
		this.conditionsConfigWidget.clear();
		for (int i = 0; i < dt.conditionCols.size(); i++) {
			ConditionCol c = (ConditionCol) dt.conditionCols.get(i);
			HorizontalPanel hp = new HorizontalPanel();
			hp.add(removeCondition(c));
			hp.add(editCondition(c));
			hp.add(new SmallLabel(c.header));
			conditionsConfigWidget.add(hp);
		}
		conditionsConfigWidget.add(newCondition());



	}

	private Widget newCondition() {
		final ConditionCol newCol = new ConditionCol();
		newCol.constraintValueType = ISingleFieldConstraint.TYPE_LITERAL;
		return new ImageButton("images/new_item.gif", "Add a new condition column", new ClickListener() {
			public void onClick(Widget w) {
				GuidedDTColumnConfig dialog = new GuidedDTColumnConfig(getSCE(), dt, new Command() {
					public void execute() {
						//want to add in a blank row into the data
						scrapeData(dt.attributeCols.size() + dt.conditionCols.size() + 1);
						refreshGrid();
						refreshConditionsWidget();
					}
				}, newCol, true);
				dialog.show();
			}
		});
	}

	private Widget editCondition(final ConditionCol c) {
		return new ImageButton("images/edit.gif", "Edit this columns configuration", new ClickListener() {
			public void onClick(Widget w) {
				GuidedDTColumnConfig dialog = new GuidedDTColumnConfig(getSCE(), dt, new Command() {
					public void execute() {
						scrapeData(-1);
						refreshGrid();
						refreshConditionsWidget();
					}
				}, c, false);
				dialog.show();
			}
		});
	}

	protected SuggestionCompletionEngine getSCE() {
		return SuggestionCompletionCache.getInstance().getEngineFromCache(this.packageName);
	}

	private Widget removeCondition(final ConditionCol c) {
		Image del = new ImageButton("images/delete_item_small.gif", "Remove this condition column", new ClickListener() {
			public void onClick(Widget w) {
				if (com.google.gwt.user.client.Window.confirm("Are you sure you want to delete the column for " + c.header + " - all data in that column will be removed?")) {
					dt.conditionCols.remove(c);
					removeField(c.header);
					scrapeData(-1);
					refreshGrid();
					refreshConditionsWidget();
				}
			}
		});

		return del;
	}

	private Widget getAttributes() {
		attributeConfigWidget = new VerticalPanel();
		refreshAttributeWidget();
		return attributeConfigWidget;
	}

	private void refreshAttributeWidget() {
		this.attributeConfigWidget.clear();
		for (int i = 0; i < dt.attributeCols.size(); i++) {
			AttributeCol at = (AttributeCol) dt.attributeCols.get(i);
			HorizontalPanel hp = new HorizontalPanel();
			hp.add(removeAttr(at));
			hp.add(new SmallLabel(at.attr));
			attributeConfigWidget.add(hp);
		}
		attributeConfigWidget.add(newAttr());
	}

	private Widget newAttr() {
		ImageButton but = new ImageButton("images/new_item.gif", "Add a new attribute.", new ClickListener() {
			public void onClick(Widget w) {
				//show choice of attributes
				final FormStylePopup pop = new FormStylePopup();
		        final ListBox list = new ListBox();
		        list.addItem( "Choose..." );


		        addItem( "salience",list);
		        addItem( "enabled", list );
		        addItem( "date-effective", list );
		        addItem( "date-expires", list );
		        addItem( "no-loop", list );
		        addItem( "agenda-group", list );
		        addItem( "activation-group", list );
		        addItem( "duration", list );
		        addItem( "auto-focus", list );
		        addItem( "lock-on-active", list );
		        addItem( "ruleflow-group", list );

		        pop.addAttribute("New attribute:", list);

		        Button ok = new Button("Add");
		        ok.addClickListener(new ClickListener() {
					public void onClick(Widget w) {

						AttributeCol attr = new AttributeCol();
						attr.attr = list.getItemText(list.getSelectedIndex());
						if (attr.attr.equals("Choose...")) {
							com.google.gwt.user.client.Window.alert("Please pick a valid attribute");
							return;
						}
						dt.attributeCols.add(attr);
						scrapeData(dt.attributeCols.size() + 1);
						refreshGrid();
						refreshAttributeWidget();
						pop.hide();
					}
		        });

		        pop.addAttribute("", ok);
		        pop.show();
			}

			private void addItem(String at, final ListBox list) {
				if (!hasAttribute(at, dt.attributeCols)) list.addItem( at );
			}

			private boolean hasAttribute(String at, List attributeCols) {
				for (Iterator iterator = attributeCols.iterator(); iterator
						.hasNext();) {
					AttributeCol c = (AttributeCol) iterator.next();
					if (c.attr.equals(at)) {
						return true;
					}
				}
				return false;
			}
		});
		return but;
	}

	private Widget removeAttr(final AttributeCol at) {
		Image del = new ImageButton("images/delete_item_small.gif", "Remove this attribute", new ClickListener() {
			public void onClick(Widget w) {
				if (com.google.gwt.user.client.Window.confirm("Are you sure you want to delete the column for " + at.attr + " - all data in that column will be removed?")) {
					dt.attributeCols.remove(at);
					removeField(at.attr);
					scrapeData(-1);
					refreshGrid();
					refreshAttributeWidget();
				}
			}
		});

		return del;
	}

	/**
	 * Here we read the record data from the grid into the data in the model.
	 * if we have an insertCol - then a new empty column of data will be added in that
	 * row position.
	 */
	private void scrapeData(int insertCol) {
		Record[] recs = grid.getStore().getRecords();
		dt.data = new String[recs.length][];
		for (int i = 0; i < recs.length; i++) {
			Record r = recs[i];
			if (insertCol == -1) {
				String[] row = new String[fds.length];
				dt.data[i] = row;
				for (int j = 0; j < fds.length; j++) {
					row[j] = r.getAsString(fds[j].getName());
				}
			} else {
				String[] row = new String[fds.length + 1];
				dt.data[i] = row;
				for (int j = 0; j < fds.length; j++) {
					if (j < insertCol) {
						row[j] = r.getAsString(fds[j].getName());
					} else if (j >= insertCol) {
						row[j + 1] = r.getAsString(fds[j].getName());
					}
				}
			}
		}
	}

	/**
	 * removes the field from the field def.
	 * @param headerName
	 */
	private void removeField(String headerName) {
		FieldDef[] fds_ = new FieldDef[fds.length -1];
		int new_i = 0;
		for (int i = 0; i < fds.length; i++) {
			FieldDef fd = fds[i];
			if (!fd.getName().equals(headerName)) {
				fds_[new_i] = fd;
				new_i++;
			}
		}
		this.fds = fds_;

	}


	private void refreshGrid() {
		if (layout.getWidgetCount() > 1) {
			layout.remove(1);
		}
		grid = doGrid();
        layout.add(grid);
	}

	private GridPanel doGrid() {

		fds = new FieldDef[dt.attributeCols.size() + dt.actionCols.size() + dt.conditionCols.size() + 2]; //its +2 as we have counter and description data

		fds[0] = new StringFieldDef("num");
		fds[1] = new StringFieldDef("desc");

		int colCount = 0;

		BaseColumnConfig[] cols = new BaseColumnConfig[fds.length + 1]; //its +1 as we have the separator -> thing.
		cols[0] = new ColumnConfig() {
				{
					setDataIndex("num");
					setWidth(20);
					setSortable(true);
					setRenderer(new Renderer() {
						public String render(Object value,
								CellMetadata cellMetadata, Record record,
								int rowIndex, int colNum, Store store) {
							return "<span class='x-grid3-cell-inner x-grid3-td-numberer'>" + value + "</span>";
						}
					});
				}
			};
		colCount++;
		cols[1] = new ColumnConfig() {
			{
				setDataIndex("desc");
				setSortable(true);
				setHeader("Description");
			}
		};
		colCount++;


		//now to attributes
		for (int i = 0; i < dt.attributeCols.size(); i++) {
			final AttributeCol attr = (AttributeCol) dt.attributeCols.get(i);
			fds[colCount] = new StringFieldDef(attr.attr);
			cols[colCount] = new ColumnConfig() {
				{
					setHeader(attr.attr);
					setDataIndex(attr.attr);
					setSortable(true);
				}
			};
			colCount++;
		}


		//do all the condition cols
		for (int i = 0; i < dt.conditionCols.size(); i++) {
			//here we could also deal with numeric type?
			final ConditionCol c = (ConditionCol) dt.conditionCols.get(i);
			fds[colCount] = new StringFieldDef(c.header);
			cols[colCount] = new ColumnConfig() {
				{
					setHeader(c.header);
					setDataIndex(c.header);
					setSortable(true);
				}
			};
			colCount++;
		}

		//the split thing
		cols[colCount] = new ColumnConfig() {
			{
    			setDataIndex("x");
    			setHeader("");
    			setFixed(true);
    			setResizable(false);

    			setRenderer(new Renderer() {
					public String render(Object value,
							CellMetadata cellMetadata, Record record,
							int rowIndex, int colNum, Store store) {
						return "<b>&#8594;</b>";
					}
    			});
    			setWidth(20);
			}
		};
		colCount++;

		for (int i = 0; i < dt.actionCols.size(); i++) {
			//here we could also deal with numeric type?
			final ActionCol c = (ActionCol) dt.actionCols.get(i);
			fds[colCount-1] = new StringFieldDef(c.header);

			cols[colCount]  = new ColumnConfig() {
				{
					setHeader(c.header);
					setDataIndex(c.header);
					//and here we do the appropriate editor
					setSortable(true);
				}
			};
			colCount++;
		}

		final RecordDef recordDef = new RecordDef(fds);
        ArrayReader reader = new ArrayReader(recordDef);
        MemoryProxy proxy = new MemoryProxy( dt.data );




        ColumnModel cm = new ColumnModel(cols);
        final GroupingStore store = new GroupingStore();
        store.setReader(reader);
        store.setDataProxy(proxy);
        store.setSortInfo(new SortState("num", SortDir.ASC));
        store.setGroupField("desc");
        store.load();


        final GridPanel grid = new GridPanel(store, cm);
        grid.setStripeRows(true);

        GroupingView gv = new GroupingView();

        //to stretch it out
        gv.setForceFit(true);
        gv.setGroupTextTpl("{text} ({[values.rs.length]} {[values.rs.length > 1 ? \"Items\" : \"Item\"]})");

        grid.setView(gv);


        grid.setStore(store);
        grid.setWidth(700);
        grid.setHeight(500);


        grid.addGridCellListener(new GridCellListenerAdapter() {
        	public void onCellDblClick(GridPanel grid, int rowIndex,
        			int colIndex, EventObject e) {

        		final String dta = grid.getColumnModel().getDataIndex(colIndex);

        		final Record r = store.getAt(rowIndex);

        		String val = r.getAsString(dta);

        		final Window w = new Window();
        		w.setWidth(168);
        		w.setAutoDestroy(true);

        		final TextBox box = new TextBox();
        		box.setText(val);
        		box.addKeyboardListener(new KeyboardListenerAdapter() {
        			public void onKeyUp(Widget sender, char keyCode,
        					int modifiers) {
        				if (keyCode == KeyboardListener.KEY_ENTER) {
    						r.set(dta, box.getText());
    						w.destroy();
        				}
        			}
        		});
        		Panel p = new Panel();
        		p.add(box);
        		w.add(p);
        		w.setBorder(false);

        		w.setPosition(e.getPageX(), e.getPageY());
        		w.show();

        		//box.setFocus(true);


        	}
        });

        Toolbar tb = new Toolbar();
        Menu menu = new Menu();
        menu.addItem(new Item("Add row...", new BaseItemListenerAdapter() {
        	public void onClick(BaseItem item, EventObject e) {
        		Record r = recordDef.createRecord(new Object[recordDef.getFields().length]);
        		r.set("num", store.getRecords().length + 1);

        		store.add(r);
        	}
        }));
        menu.addItem(new Item("Remove selected row(s)...", new BaseItemListenerAdapter() {
        	public void onClick(BaseItem item, EventObject e) {
        		Record[] selected = grid.getSelectionModel().getSelections();
        		if (com.google.gwt.user.client.Window.confirm("Are you sure you want to delete the selected row(s)? ")) {
        			for (int i = 0; i < selected.length; i++) {
        				store.remove(selected[i]);
					}
        			renumber(store.getRecords());
        		}
        	}
        }));
        ToolbarMenuButton tbb = new ToolbarMenuButton("Modify...", menu);

        tb.addButton(tbb);
        grid.add(tb);






        return grid;

	}

	private void renumber(Record[] rs) {
		for (int i = 0; i < rs.length; i++) {
			rs[i].set("num", "" + (i + 1));
		}
	}

}
