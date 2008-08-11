package org.drools.guvnor.client.admin;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.drools.guvnor.client.common.FormStylePopup;
import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.common.ImageButton;
import org.drools.guvnor.client.common.LoadingPopup;
import org.drools.guvnor.client.common.SmallLabel;
import org.drools.guvnor.client.rpc.RepositoryServiceFactory;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.gwtext.client.core.EventObject;
import com.gwtext.client.core.SortDir;
import com.gwtext.client.data.ArrayReader;
import com.gwtext.client.data.FieldDef;
import com.gwtext.client.data.GroupingStore;
import com.gwtext.client.data.MemoryProxy;
import com.gwtext.client.data.RecordDef;
import com.gwtext.client.data.SortState;
import com.gwtext.client.data.StringFieldDef;
import com.gwtext.client.widgets.Button;
import com.gwtext.client.widgets.Toolbar;
import com.gwtext.client.widgets.ToolbarButton;
import com.gwtext.client.widgets.ToolbarSeparator;
import com.gwtext.client.widgets.ToolbarTextItem;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;
import com.gwtext.client.widgets.grid.ColumnConfig;
import com.gwtext.client.widgets.grid.ColumnModel;
import com.gwtext.client.widgets.grid.GridPanel;
import com.gwtext.client.widgets.grid.GroupingView;
import com.gwtext.client.widgets.grid.event.GridRowListener;
import com.gwtext.client.widgets.grid.event.GridRowListenerAdapter;

public class PermissionViewer extends Composite {

	private VerticalPanel layout;
	private Map<String, List<String>> currentUsers;
	private GridPanel grid;

	public PermissionViewer() {
		layout = new VerticalPanel();
		layout.setHeight("100%");
		layout.setWidth("100%");


		refresh();
		initWidget(layout);
	}

	private void refresh() {
		LoadingPopup.showMessage("Loading user permissions...");
		RepositoryServiceFactory.getService().listUserPermissions(new GenericCallback<Map<String,List<String>>>() {
			public void onSuccess(Map<String, List<String>> list) {
				if (grid != null) {
					layout.remove(grid);
					grid.destroy();
				}
				showUsers(list);
				LoadingPopup.close();
			}
		});
	}

	private void showUsers(Map<String,List<String>> users) {



		this.currentUsers = users;

		//showing a grid - the cols are:
		//userName, hasAdmin, hasPackage, hasCategory permissions...
		//double click to edit
		Object[][] data = new Object[users.size()][4];


		int row = 0;
		for (Map.Entry<String, List<String>> userRow : users.entrySet()) {
			data[row][0] = userRow.getKey();
			List<String> permTypes = userRow.getValue();
			data[row][1] = isAdmin(permTypes);
			data[row][2] = isPackage(permTypes);
			data[row][3] = isCategory(permTypes);

			row++;
		}


		MemoryProxy proxy = new MemoryProxy(data);
		RecordDef recordDef = new RecordDef(
				new FieldDef[]{
						new StringFieldDef("userName"),
						new StringFieldDef("isAdmin"),
						new StringFieldDef("isPackage"),
						new StringFieldDef("isCategory"),
				}
		);

		ArrayReader reader = new ArrayReader(recordDef);
		GroupingStore store = new GroupingStore();
        store.setReader(reader);
        store.setDataProxy(proxy);
		store.setGroupField("isAdmin");
		store.setSortInfo(new SortState("userName", SortDir.ASC));
		store.load();

		ColumnModel cm  = new ColumnModel(new ColumnConfig[] {
				new ColumnConfig() {
					{
						setDataIndex("userName");
						setSortable(true);
						setHeader("User name");
					}
				},
				new ColumnConfig() {
					{
						setHeader("Administrator");
						setSortable(true);
						setDataIndex("isAdmin");
					}
				},
				new ColumnConfig() {
					{
						setHeader("Has package permissions");
						setSortable(true);
						setDataIndex("isPackage");
					}
				},
				new ColumnConfig() {
					{
						setHeader("Has category permissions");
						setSortable(true);
						setDataIndex("isCategory");
					}
				}

			});

		grid = new GridPanel();
		grid.setColumnModel(cm);
		grid.setStore(store);
		grid.setWidth(400);
		grid.setHeight(600);

        GroupingView gv = new GroupingView();

        //to stretch it out
        gv.setForceFit(true);
        gv.setGroupTextTpl("{text} ({[values.rs.length]} {[values.rs.length > 1 ? \"Items\" : \"Item\"]})");


        grid.setView(gv);


		Toolbar tb = new Toolbar();
		grid.setTopToolbar(tb);

		tb.addItem(new ToolbarTextItem("Currently configured users:"));
		tb.addItem(new ToolbarSeparator());

		layout.add(grid);
		ToolbarButton reload = new ToolbarButton("Reload");
		reload.addListener(new ButtonListenerAdapter() {
					public void onClick(Button button, EventObject e) {
						refresh();
					}
				});
		tb.addButton(reload);


		grid.addGridRowListener(new GridRowListenerAdapter() {
			@Override
			public void onRowDblClick(GridPanel grid, int rowIndex,
					EventObject e) {
				String userName = grid.getSelectionModel().getSelected().getAsString("userName");
				showEditor(userName);
			}
		});

//		ToolbarButton create = new ToolbarButton("Create new user");
//		create.addListener(new ButtonListenerAdapter() {
//					public void onClick(Button button, EventObject e) {
//
//
//						layout.remove(g);
//						refresh();
//					}
//				});








	}


	private void showEditor(final String userName) {
		LoadingPopup.showMessage("Loading users permissions...");
		RepositoryServiceFactory.getService().retrieveUserPermissions(userName, new GenericCallback<Map<String, List<String>>>() {
			public void onSuccess(Map<String, List<String>> perms) {
				FormStylePopup editor = new FormStylePopup("images/managment.gif", "Edit user: " + userName);
				editor.addRow(new HTML("<i>Users are athenticated by a directory service, here you can define Guvnor specific permissions as needed.</i>"));
				//now render the actual permissions...
				VerticalPanel vp = new VerticalPanel();
				editor.addAttribute("", doPermsPanel(perms, vp));
				editor.show();
				LoadingPopup.close();
			}
		});
	}


	/**
	 * The permissions panel.
	 */
	private Widget doPermsPanel(final Map<String, List<String>> perms, final Panel vp) {
		vp.clear();

		for (Map.Entry<String, List<String>> perm : perms.entrySet()) {
			if (perm.getKey().equals("admin")) {
				HorizontalPanel h = new HorizontalPanel();
				h.add(new HTML("<b>This user is an administrator</b>"));
				com.google.gwt.user.client.ui.Button del = new com.google.gwt.user.client.ui.Button("Remove Admin rights");

				del.addClickListener(new ClickListener() {
					public void onClick(Widget w) {
						if (Window.confirm("Are you sure you want to remove Administrator permissions?")) {
							perms.remove("admin");
							doPermsPanel(perms, vp);
						}
					}
				});
				h.add(del);
				vp.add(h);
			} else {
				final String permType = perm.getKey();
				final List<String> permList = perm.getValue();

				Grid g = new Grid(permList.size() + 1, 3);
				g.setWidget(0, 0, new HTML("<b>" + permType + "</b>"));

				for (int i = 0; i < permList.size(); i++) {
					final String p = permList.get(i);
					ImageButton del = new ImageButton("images/delete_item_small.gif", "Remove permission.", new ClickListener() {
						public void onClick(Widget w) {
							if (Window.confirm("Are you sure you want to remove permission [" + p + "]?")) {
								permList.remove(p);
								if (permList.size() == 0) {
									perms.remove(permType);
								}
								doPermsPanel(perms, vp);
							}
						}
					});

					g.setWidget(i + 1, 1, new SmallLabel(p));
					g.setWidget(i + 1, 2, del);
				}

				vp.add(g);
			}


		}

		//now to be able to add...



		return vp;
	}

	private Object isCategory(List<String> permTypes) {
		for (String s : permTypes) {
			if (s.startsWith("analyst")) return "Yes";
		}
		return "";
	}

	private String isPackage(List<String> permTypes) {
		for (String s : permTypes) {
			if (s.startsWith("package")) return "Yes";
		}
		return "";
	}

	private String isAdmin(List<String> permTypes) {
		if (permTypes.contains("admin")) {
			return "Yes";
		} else {
			return "";
		}
	}



}
