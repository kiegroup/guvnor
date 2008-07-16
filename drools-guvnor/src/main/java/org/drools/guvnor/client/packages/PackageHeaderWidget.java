package org.drools.guvnor.client.packages;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.drools.guvnor.client.common.FormStylePopup;
import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.common.ImageButton;
import org.drools.guvnor.client.common.InfoPopup;
import org.drools.guvnor.client.rpc.PackageConfigData;
import org.drools.guvnor.client.rpc.RepositoryServiceFactory;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * This is for managing imports etc.
 * @author Michael Neale
 */
public class PackageHeaderWidget extends Composite {


	private PackageConfigData conf;
	private SimplePanel layout;
	private ListBox importList;
	private ListBox globalList;


	public PackageHeaderWidget(PackageConfigData conf) {
		this.conf = conf;
		layout = new SimplePanel();
		render();


		initWidget(layout);
	}


	private void render() {

		final Types t = PackageHeaderHelper.parseHeader(conf.header);
		if (t == null) {
			textEditorVersion();
		} else {
			layout.clear();
			HorizontalPanel main = new HorizontalPanel();

			VerticalPanel imports = new VerticalPanel();
			imports.add(new Label("Imported types:"));
			importList = new ListBox(true);

			doImports(t);
			HorizontalPanel importCols = new HorizontalPanel();
			importCols.add(importList);
			VerticalPanel importActions = new VerticalPanel();
			importActions.add(new ImageButton("images/new_item.gif") {
				{
					addClickListener(new ClickListener() {
						public void onClick(Widget w) {
							showTypeQuestion(w, t, false, "Fact types are classes from 'jar' files that have been uploaded to the current package.");
						}
					});
				}
			});
			importActions.add(new ImageButton("images/trash.gif") {
				{
					addClickListener(new ClickListener() {
						public void onClick(Widget w) {
							if (Window.confirm("Are you sure you want to remove this fact type?")) {
								int i = importList.getSelectedIndex();
								importList.removeItem(i);
								t.imports.remove(i);
								updateHeader(t);
							}
						}
					});
				}
			});

			importCols.add(importActions);
			imports.add(importCols);


			VerticalPanel globals = new VerticalPanel();
			globals.add(new Label("Globals:"));
			globalList = new ListBox(true);
			doGlobals(t);
			HorizontalPanel globalCols = new HorizontalPanel();
			globalCols.add(globalList);
			VerticalPanel globalActions = new VerticalPanel();
			globalActions.add(new ImageButton("images/new_item.gif") {
				{
					addClickListener(new ClickListener() {
						public void onClick(Widget w) {
							showTypeQuestion(w, t, true, "Global types are classes from 'jar' files that have been uploaded to the current package.");
						}
					});
				}
			});
			globalActions.add(new ImageButton("images/trash.gif") {
				{
					addClickListener(new ClickListener() {
						public void onClick(Widget w) {
							if (Window.confirm("Are you sure you want to remove this global?")) {
								int i = globalList.getSelectedIndex();
								globalList.removeItem(i);
								t.globals.remove(i);
								updateHeader(t);
							}
						}
					});
				}
			});
			globalCols.add(globalActions);
			globals.add(globalCols);

			main.add(imports);
			main.add(globals);

			Button advanced = new Button() {
				{
					setText("Advanced view");
					setTitle("Switch to text mode editing.");
					addClickListener(new ClickListener() {
						public void onClick(Widget w) {
							if (Window.confirm("Switch to advanced text mode for package editing?")) {
								textEditorVersion();
							}
						}
					});
				}
			};
			main.add(advanced);


			layout.add(main);

		}
	}


	private void textEditorVersion() {
		layout.clear();
		final TextArea area = new TextArea();
		area.setWidth( "100%" );
		area.setVisibleLines( 8 );

		area.setCharacterWidth( 100 );

		area.setText( this.conf.header );
		area.addChangeListener( new ChangeListener() {
		    public void onChange(Widget w) {
		         conf.header = area.getText();
		    }
		});
		layout.add(area);
	}

	private void showTypeQuestion(Widget w, final Types t, final boolean global, String headerMessage) {

		final FormStylePopup pop = new FormStylePopup("images/home_icon.gif", "Choose a fact type");
		pop.addRow(new HTML("<small><i>" + headerMessage +
				" </i></small>"));
		final ListBox factList = new ListBox();
		factList.addItem("loading list ....");

		RepositoryServiceFactory.getService().listTypesInPackage(this.conf.uuid, new GenericCallback() {
			public void onSuccess(Object data) {
				factList.clear();
				String[] list = (String[]) data;
				for (int i = 0; i < list.length; i++) {
					if (global) {
						factList.addItem(list[i]);
					} else {
						if (list[i].indexOf('.') > -1) {
							factList.addItem(list[i]);
						}
					}
				}
			}
		});

		InfoPopup info = new InfoPopup("Types in the package", "If no types appear in the list, create a model asset, and upload a jar file to it for this package. " +
				"The jar file should contain the .class files for the types needed by the rules only.");
		HorizontalPanel h = new HorizontalPanel();
		h.add(factList);
		h.add(info);
		pop.addAttribute("Choose class type:", h);
		final TextBox globalName = new TextBox();
		if (global) {
			pop.addAttribute("Global name:", globalName);
		}
		final TextBox className = new TextBox();
		InfoPopup infoClass = new InfoPopup("Entering a type class name", "You should only need to do this if a fact class is on the BRMS classpath itself. Otherwise it should be in the list above.");
		h = new HorizontalPanel();
		h.add(className);
		h.add(infoClass);
		pop.addAttribute("(advanced) class name:", h);

		Button ok = new Button("OK") {
			{
				addClickListener(new ClickListener() {
					public void onClick(Widget w) {
						String type = (!"".equals(className.getText())) ? className.getText() : factList.getItemText(factList.getSelectedIndex());
						if (!global) {
							t.imports.add(new Import(type));
							doImports(t);
						} else {
							if ("".equals(globalName.getText())) {
								Window.alert("You must enter a global variable name.");
								return;
							}
							t.globals.add(new Global(type, globalName.getText()));
							doGlobals(t);
						}
						updateHeader(t);
						pop.hide();
					}
				});
			}
		};
		pop.addAttribute("", ok);

		pop.show();



	}



	private void updateHeader(Types t) {
		this.conf.header = PackageHeaderHelper.renderTypes(t);
	}

	private void doGlobals(Types t) {
		globalList.clear();
		for (Iterator it = t.globals.iterator(); it.hasNext();) {
			Global g = (Global) it.next();
			globalList.addItem(g.type + " [" + g.name + "]");
		}
	}


	private void doImports(Types t) {
		importList.clear();
		for (Iterator it = t.imports.iterator(); it.hasNext();) {
			Import i = (Import) it.next();
			importList.addItem(i.type);
		}
	}









	static class Types {
		List imports = new ArrayList();
		List globals = new ArrayList();
	}

	static class Import {
		String type;

		Import(String t) {
			this.type = t;
		}
	}

	static class Global {
		String type;
		String name;

		Global(String type, String name) {
			this.type = type;
			this.name = name;
		}
	}

}




