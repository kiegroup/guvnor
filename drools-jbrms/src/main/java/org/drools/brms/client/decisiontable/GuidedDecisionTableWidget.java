package org.drools.brms.client.decisiontable;

import org.drools.brms.client.modeldriven.dt.ActionCol;
import org.drools.brms.client.modeldriven.dt.ConditionCol;
import org.drools.brms.client.modeldriven.dt.GuidedDecisionTable;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.KeyboardListener;
import com.google.gwt.user.client.ui.KeyboardListenerAdapter;
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
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.Toolbar;
import com.gwtext.client.widgets.ToolbarMenuButton;
import com.gwtext.client.widgets.Window;
import com.gwtext.client.widgets.grid.BaseColumnConfig;
import com.gwtext.client.widgets.grid.CellMetadata;
import com.gwtext.client.widgets.grid.ColumnConfig;
import com.gwtext.client.widgets.grid.ColumnModel;
import com.gwtext.client.widgets.grid.GridPanel;
import com.gwtext.client.widgets.grid.GroupingView;
import com.gwtext.client.widgets.grid.Renderer;
import com.gwtext.client.widgets.grid.RowNumberingColumnConfig;
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
	private boolean DEBUG = true;

	public GuidedDecisionTableWidget(GuidedDecisionTable dt) {

    	this.dt = dt;

        GridPanel grid = doGrid();
        VerticalPanel p = new VerticalPanel();
        p.add(grid);


        initWidget(p);
    }

	private GridPanel doGrid() {

		FieldDef[] fds = new FieldDef[dt.actionCols.size() + dt.conditionCols.size() + 1];

		fds[0] = new StringFieldDef("num");

		BaseColumnConfig[] cols = new BaseColumnConfig[fds.length + 1];
		if (!DEBUG ) {
			cols[0] = new RowNumberingColumnConfig();
		} else {
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
							//return value;
						}
					});
				}
			};
		}




		//do all the condition cols
		for (int i = 0; i < dt.conditionCols.size(); i++) {
			//here we could also deal with numeric type?
			final ConditionCol c = (ConditionCol) dt.conditionCols.get(i);
			fds[i + 1] = new StringFieldDef(c.header);
			cols[i + 1] = new ColumnConfig() {
				{
					setHeader(c.header);
					setDataIndex(c.header);
					setSortable(true);
				}
			};
		}

		//the split thing
		cols[dt.conditionCols.size() + 1] = new ColumnConfig() {
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

		for (int i = 0; i < dt.actionCols.size(); i++) {
			//here we could also deal with numeric type?
			final ActionCol c = (ActionCol) dt.actionCols.get(i);
			fds[i + dt.conditionCols.size() + 1] = new StringFieldDef(c.header);

			cols[i + dt.conditionCols.size() + 2]  = new ColumnConfig() {
				{
					setHeader(c.header);
					setDataIndex(c.header);
					//and here we do the appropriate editor
					setSortable(true);
				}
			};
		}

		final RecordDef recordDef = new RecordDef(fds);
        ArrayReader reader = new ArrayReader(recordDef);
        MemoryProxy proxy = new MemoryProxy( dt.data );




        ColumnModel cm = new ColumnModel(cols);
        final GroupingStore store = new GroupingStore();
        store.setReader(reader);
        store.setDataProxy(proxy);
        store.setSortInfo(new SortState("num", SortDir.ASC));
        store.load();


        final GridPanel grid = new GridPanel(store, cm);
        grid.setStripeRows(true);

        GroupingView gv = new GroupingView();

        //to stretch it out
        gv.setForceFit(true);
        gv.setGroupTextTpl("{text} ({[values.rs.length]} {[values.rs.length > 1 ? \"Items\" : \"Item\"]})");

        grid.setView(gv);


        grid.setStore(store);
        grid.setWidth(500);
        grid.setHeight(300);


        grid.addGridCellListener(new GridCellListenerAdapter() {
        	public void onCellDblClick(GridPanel grid, int rowIndex,
        			int colIndex, EventObject e) {

        		final String dta = grid.getColumnModel().getDataIndex(colIndex);

        		final Record r = store.getAt(rowIndex);

        		String val = r.getAsString(dta);

        		final Window w = new Window();
        		w.setWidth(170);

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
        		box.setFocus(true);


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
        menu.addItem(new Item("Remove selected row...", new BaseItemListenerAdapter() {
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
