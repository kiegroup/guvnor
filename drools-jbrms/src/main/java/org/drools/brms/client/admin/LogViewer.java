package org.drools.brms.client.admin;

import org.drools.brms.client.common.GenericCallback;
import org.drools.brms.client.common.LoadingPopup;
import org.drools.brms.client.common.PrettyFormLayout;
import org.drools.brms.client.rpc.LogEntry;
import org.drools.brms.client.rpc.RepositoryServiceFactory;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.gwtext.client.core.EventObject;
import com.gwtext.client.core.Ext;
import com.gwtext.client.core.SortDir;
import com.gwtext.client.data.ArrayReader;
import com.gwtext.client.data.DateFieldDef;
import com.gwtext.client.data.FieldDef;
import com.gwtext.client.data.IntegerFieldDef;
import com.gwtext.client.data.MemoryProxy;
import com.gwtext.client.data.Record;
import com.gwtext.client.data.RecordDef;
import com.gwtext.client.data.Store;
import com.gwtext.client.data.StringFieldDef;
import com.gwtext.client.widgets.Button;
import com.gwtext.client.widgets.ButtonConfig;
import com.gwtext.client.widgets.Toolbar;
import com.gwtext.client.widgets.ToolbarButton;
import com.gwtext.client.widgets.ToolbarSeparator;
import com.gwtext.client.widgets.ToolbarTextItem;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;
import com.gwtext.client.widgets.grid.CellMetadata;
import com.gwtext.client.widgets.grid.ColumnConfig;
import com.gwtext.client.widgets.grid.ColumnModel;
import com.gwtext.client.widgets.grid.Grid;
import com.gwtext.client.widgets.grid.Renderer;

public class LogViewer extends Composite {

	private VerticalPanel layout;

	public LogViewer() {
		layout = new VerticalPanel();
		layout.setHeight("100%");
		layout.setWidth("100%");


		refresh();
		initWidget(layout);
	}

	private void refresh() {
		LoadingPopup.showMessage("Loading log messages...");
		RepositoryServiceFactory.getService().showLog(new GenericCallback() {
			public void onSuccess(Object data) {
				LogEntry[] logs = (LogEntry[]) data;
				showLogs(logs);
				LoadingPopup.close();
			}
		});
	}

	private void showLogs(LogEntry[] logs) {
		Object[][] data = new Object[logs.length][3];
		for (int i = 0; i < logs.length; i++) {
			LogEntry e = logs[i];
			if (e != null) {
				data[i][0] = new Integer(e.severity);
				data[i][1] = e.timestamp;
				data[i][2] = e.message;
			} else {
				data[i][0] = new Integer(2);
				data[i][1] = "";
				data[i][2] = "";
			}
		}

		MemoryProxy proxy = new MemoryProxy(data);
		RecordDef recordDef = new RecordDef(
				new FieldDef[]{
						new IntegerFieldDef("severity"),
						new DateFieldDef("timestamp"),
						new StringFieldDef("message"),
				}
		);

		ArrayReader reader = new ArrayReader(recordDef);
		Store store = new Store(proxy, reader);
		store.setDefaultSort("timestamp", SortDir.DESC);
		store.load();

		ColumnModel cm  = new ColumnModel(new ColumnConfig[] {
				new ColumnConfig() {
					{
						setDataIndex("severity");
						setSortable(true);
						setRenderer(new Renderer() {
							public String render(Object value,
									CellMetadata cellMetadata, Record record,
									int rowIndex, int colNum, Store store) {
								Integer i = (Integer) value;
								if (i.intValue() == 0) {
									return "<img src='images/error.gif'/>";
								} else if (i.intValue() == 1) {
									return "<img src='images/information.gif'/>";
								} else {
									return "";
								}
							}
						});
						setWidth(25);
					}
				},
				new ColumnConfig() {
					{
						setHeader("Timestamp");
						setSortable(true);
						setDataIndex("timestamp");
						setWidth(180);
					}
				},
				new ColumnConfig() {
					{
						setHeader("Message");
						setSortable(true);
						setDataIndex("message");
						setWidth(580);
					}
				}
			});

		Grid g = new Grid(Ext.generateId(), "800px", "600px", store, cm);
		g.render();

		Toolbar tb = new Toolbar(g.getView().getHeaderPanel(true));
		tb.addItem(new ToolbarTextItem("Showing recent INFO and ERROR messages from the log:"));
		tb.addItem(new ToolbarSeparator());
		tb.addButton(new ToolbarButton(new ButtonConfig() {
			{
				setText("Reload");
				setButtonListener(new ButtonListenerAdapter() {
					public void onClick(Button button, EventObject e) {
						refresh();
					}
				});
			}
		}));

		layout.add(g);



	}

}
