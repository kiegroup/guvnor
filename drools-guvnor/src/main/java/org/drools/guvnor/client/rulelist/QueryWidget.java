package org.drools.guvnor.client.rulelist;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.drools.guvnor.client.common.FormStyleLayout;
import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.common.SmallLabel;
import org.drools.guvnor.client.rpc.MetaDataQuery;
import org.drools.guvnor.client.rpc.RepositoryServiceFactory;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.form.DateField;

public class QueryWidget extends Composite {

	private static final String DATE_PICKER_FORMAT = "d-M-Y H:m:s";
	private VerticalPanel layout;
	private EditItemEvent openItem;

	public QueryWidget(EditItemEvent openItem) {
		layout = new VerticalPanel();
		this.openItem = openItem;
		doQuickFind();
		doTextSearch();
		doMetaSearch();

		layout.setWidth("100%");



		initWidget(layout);

		setWidth("100%");

	}

	private void doMetaSearch() {
		Panel p = new Panel();
		p.setCollapsible(true);
		p.setTitle("Attribute search ... ");

		final Map atts = new HashMap() {
			{
				put("Created by", new MetaDataQuery("drools:creator"));
				put("Format", new MetaDataQuery("drools:format"));
				put("Subject", new MetaDataQuery("drools:subject"));
				put("Type", new MetaDataQuery("drools:type"));
				put("External link", new MetaDataQuery("drools:relation"));
				put("Source", new MetaDataQuery("drools:source"));
				put("Description", new MetaDataQuery("drools:description"));
				put("Last modified by", new MetaDataQuery("drools:lastContributor"));
				put("Checkin comment", new MetaDataQuery("drools:checkinComment"));
			}
		};


		FormStyleLayout fm = new FormStyleLayout();
		for (Iterator iterator = atts.keySet().iterator(); iterator.hasNext();) {
			String fieldName = (String) iterator.next();
			final MetaDataQuery q = (MetaDataQuery) atts.get(fieldName);
			final TextBox box = new TextBox();
			box.setTitle("Use * for wildcards, separate different options with a comma.");
			fm.addAttribute(fieldName + ":", box);
			box.addChangeListener(new ChangeListener() {
				public void onChange(Widget w) {
					q.valueList = box.getText();
				}
			});
		}

		HorizontalPanel created = new HorizontalPanel();
		created.add(new SmallLabel("After:"));
		final DateField createdAfter = new DateField("After:", DATE_PICKER_FORMAT);
		created.add(createdAfter);

		created.add(new SmallLabel("Before:"));
		final DateField createdBefore = new DateField("Before", DATE_PICKER_FORMAT);
		created.add(createdBefore);

		fm.addAttribute("Date created", created);



		HorizontalPanel lastMod = new HorizontalPanel();
		lastMod.add(new SmallLabel("After:"));
		final DateField lastModAfter = new DateField("After:", DATE_PICKER_FORMAT);
		lastMod.add(lastModAfter);

		lastMod.add(new SmallLabel("Before:"));
		final DateField lastModBefore = new DateField("Before:", DATE_PICKER_FORMAT);
		lastMod.add(lastModBefore);

		fm.addAttribute("Last modified", lastMod);

		final SimplePanel resultsP = new SimplePanel();
		Button search = new Button("Search");
		fm.addAttribute("", search);
		search.addClickListener(new ClickListener() {
			public void onClick(Widget w) {
				resultsP.clear();
				AssetItemGrid grid = new AssetItemGrid(openItem, "searchresults", new AssetItemGridDataLoader() {
					public void loadData(int startRow, int numberOfRows,
							GenericCallback cb) {
						MetaDataQuery[] mdq = new MetaDataQuery[atts.size()];
						int i = 0;
						for (Iterator iterator = atts.keySet().iterator(); iterator
								.hasNext();) {
							String name = (String) iterator.next();
							mdq[i] = (MetaDataQuery) atts.get(name);
							i++;
						}
						RepositoryServiceFactory.getService().queryMetaData(mdq, createdAfter.getValue(), createdBefore.getValue(),
								lastModAfter.getValue(), lastModBefore.getValue(), false, startRow, numberOfRows, cb);
					}
				});
				resultsP.add(grid);
			}
		});
		fm.addRow(resultsP);
		p.add(fm);
		p.setCollapsed(true);
		layout.add(p);
	}

	private void doQuickFind() {
		Panel p = new Panel();
		p.setCollapsible(true);
		p.setTitle("Name search ...");
		p.add(new QuickFindWidget(openItem));

		p.setCollapsed(false);

		layout.add(p);
	}

	private void doTextSearch() {
		Panel p = new Panel();
		p.setCollapsible(true);
		p.setTitle("Text search ...");

		p.setCollapsed(true);

		FormStyleLayout ts = new FormStyleLayout();
		final TextBox tx = new TextBox();
		ts.addAttribute("Search for:", tx);
		Button go = new Button();
		go.setText("Search");
		ts.addAttribute("", go);
		ts.setWidth("100%");
		p.add(ts);

		final SimplePanel resultsP = new SimplePanel();
		go.addClickListener(new ClickListener() {
			public void onClick(Widget w) {
				if (tx.getText().equals("")) {
					Window.alert("Please enter some search text");
					return;
				}
				resultsP.clear();
				AssetItemGrid grid = new AssetItemGrid(openItem, "searchresults", new AssetItemGridDataLoader() {
					public void loadData(int startRow, int numberOfRows,
							GenericCallback cb) {
						RepositoryServiceFactory.getService().queryFullText(tx.getText(), false, startRow, numberOfRows, cb);
					}
				});
				resultsP.add(grid);
			}
		});
		ts.addRow(resultsP);
		layout.add(p);
	}



}
