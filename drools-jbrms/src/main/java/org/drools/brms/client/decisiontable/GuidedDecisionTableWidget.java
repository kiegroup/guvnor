package org.drools.brms.client.decisiontable;

import org.drools.brms.client.modeldriven.dt.ActionCol;
import org.drools.brms.client.modeldriven.dt.ConditionCol;
import org.drools.brms.client.modeldriven.dt.GuidedDecisionTable;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.gwtext.client.core.EventObject;
import com.gwtext.client.data.ArrayReader;
import com.gwtext.client.data.FieldDef;
import com.gwtext.client.data.MemoryProxy;
import com.gwtext.client.data.Record;
import com.gwtext.client.data.RecordDef;
import com.gwtext.client.data.Store;
import com.gwtext.client.data.StringFieldDef;
import com.gwtext.client.widgets.MessageBox;
import com.gwtext.client.widgets.Toolbar;
import com.gwtext.client.widgets.ToolbarMenuButton;
import com.gwtext.client.widgets.form.TextField;
import com.gwtext.client.widgets.grid.BaseColumnConfig;
import com.gwtext.client.widgets.grid.CellMetadata;
import com.gwtext.client.widgets.grid.ColumnConfig;
import com.gwtext.client.widgets.grid.ColumnModel;
import com.gwtext.client.widgets.grid.EditorGridPanel;
import com.gwtext.client.widgets.grid.GridEditor;
import com.gwtext.client.widgets.grid.GridPanel;
import com.gwtext.client.widgets.grid.GridView;
import com.gwtext.client.widgets.grid.Renderer;
import com.gwtext.client.widgets.grid.RowNumberingColumnConfig;
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
	private boolean DEBUG = false;

	public GuidedDecisionTableWidget(GuidedDecisionTable dt) {

    	this.dt = dt;

        GridPanel grid = doGrid();
        VerticalPanel p = new VerticalPanel();
        p.add(grid);


        initWidget(p);
    }

	private GridPanel doGrid() {

		FieldDef[] fds = new FieldDef[dt.actionCols.size() + dt.conditionCols.size()];

		BaseColumnConfig[] cols = new BaseColumnConfig[fds.length + 2];
		if (!DEBUG ) {
			cols[0] = new RowNumberingColumnConfig();
		} else {
			cols[0] = new ColumnConfig() {
				{
					setDataIndex("num");
					setWidth(5);
				}
			};
		}



		//do all the condition cols
		for (int i = 0; i < dt.conditionCols.size(); i++) {
			//here we could also deal with numeric type?
			final ConditionCol c = (ConditionCol) dt.conditionCols.get(i);
			fds[i] = new StringFieldDef(c.header);
			cols[i + 1] = new ColumnConfig() {
				{
					setHeader(c.header);
					setDataIndex(c.header);
					//and here we do the appropriate editor
					setEditor(new GridEditor(new TextField()));
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
			fds[i + dt.conditionCols.size()] = new StringFieldDef(c.header);

			cols[i + dt.conditionCols.size() + 2]  = new ColumnConfig() {
				{
					setHeader(c.header);
					setDataIndex(c.header);
					//and here we do the appropriate editor
					setEditor(new GridEditor(new TextField()));
				}
			};
		}

		final RecordDef recordDef = new RecordDef(fds);
        ArrayReader reader = new ArrayReader(recordDef);
        MemoryProxy proxy = new MemoryProxy( dt.data );




        ColumnModel cm = new ColumnModel(cols);
        final Store store = new Store(proxy, reader);
        store.load();


        final EditorGridPanel grid = new EditorGridPanel(store, cm);
        //to stretch it out
        GridView gv = new GridView();
        gv.setForceFit(true);
        grid.setView(gv);

        grid.setStore(store);
        grid.setWidth(500);
        grid.setHeight(300);
        grid.setClicksToEdit(1);

        Toolbar tb = new Toolbar();
        Menu menu = new Menu();
        menu.addItem(new Item("Add row...", new BaseItemListenerAdapter() {
        	public void onClick(BaseItem item, EventObject e) {
        		Record r = recordDef.createRecord(new Object[recordDef.getFields().length]);
        		grid.stopEditing();
        		store.add(r);
        		grid.startEditing(0, 0);
        	}
        }));
        menu.addItem(new Item("Remove row...", new BaseItemListenerAdapter() {
        	public void onClick(BaseItem item, EventObject e) {
        		MessageBox.prompt("Line number to delete", "Line #", new MessageBox.PromptCallback() {
					public void execute(String btnID, String text) {
						int i = Integer.parseInt(text);

						Record r = store.getAt(i - 1);
						grid.stopEditing();
						store.remove(r);
						grid.startEditing(0, 0);
					}
        		});
        	}
        }));
        ToolbarMenuButton tbb = new ToolbarMenuButton("Modify...", menu);

        tb.addButton(tbb);
        grid.add(tb);





        return grid;

	}

}
