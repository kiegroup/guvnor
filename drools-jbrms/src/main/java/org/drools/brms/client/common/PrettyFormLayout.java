package org.drools.brms.client.common;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.gwtext.client.core.Ext;
import com.gwtext.client.util.DOMUtil;
import com.gwtext.client.widgets.form.Form;
import com.gwtext.client.widgets.form.FormConfig;

/**
 * Uses ext forms to do a prettier layout.
 *
 * @author Michael Neale
 */
public class PrettyFormLayout extends Composite {

	private VerticalPanel layout = new VerticalPanel();
	private FlexTable	currentTable;
	private String sectionName;

	public PrettyFormLayout() {
        layout.setWidth("100%");
		initWidget(layout);
	}

	public void startSection() {
		this.currentTable = new FlexTable();
	}

	public void startSection(String title) {
		startSection();
		this.sectionName = title;
	}

	public void clear() {
		this.layout.clear();
	}

	public void addHeader(String img, String name, Image edit) {
		HorizontalPanel h = new HorizontalPanel();
		h.add(new Image(img));
		h.add(new Label(name));
		if (edit != null) h.add(edit);


		Form f = newForm(null);
		String id = Ext.generateId();
		f.container(id);
		f.end();
		f.render();
		DOMUtil.convertDivToPanel(id).add(h);

		layout.add(f);
	}

	public void addHeader(String img, Widget content) {
		HorizontalPanel h = new HorizontalPanel();
		h.add(new Image(img));
		h.add(content);
		Form f = newForm(null);
		String id = Ext.generateId();
		f.container(id);
		f.end();
		f.render();
		DOMUtil.convertDivToPanel(id).add(h);

		layout.add(f);
	}

	private Form newForm(final String hdr) {
		return new Form(new FormConfig() {
			{
        		setWidth("100%");
        		setSurroundWithBox(true);
        		if (hdr != null) {
        			setHeader(hdr);
        		}
			}
		});
	}

    public void endSection() {

		Form f = newForm(this.sectionName);

		String id = Ext.generateId();
		f.container(id);
		f.end();
		f.render();

		FlowPanel fp = DOMUtil.convertDivToPanel(id);
		fp.add(this.currentTable);
		this.layout.add(f);
		this.sectionName = null;
	}

	public void addRow(final Widget versionBrowser) {
    	int i = currentTable.getRowCount();
    	currentTable.setWidget(i, 0, versionBrowser);
    	currentTable.getFlexCellFormatter().setColSpan(i, 0, 2);
	}


	public void addAttribute(String lbl, final Widget categories) {
		int i = this.currentTable.getRowCount();
		currentTable.setWidget(i, 0, new Label(lbl));
		currentTable.setWidget(i, 1, categories);
		currentTable.getFlexCellFormatter().setHorizontalAlignment(i, 0, HasHorizontalAlignment.ALIGN_RIGHT);
	}


}
