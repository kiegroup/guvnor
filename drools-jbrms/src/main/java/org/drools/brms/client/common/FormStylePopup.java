package org.drools.brms.client.common;
/*
 * Copyright 2005 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



import com.google.gwt.user.client.ui.Widget;
import com.gwtext.client.widgets.LayoutDialog;
import com.gwtext.client.widgets.LayoutDialogConfig;
import com.gwtext.client.widgets.layout.BorderLayout;
import com.gwtext.client.widgets.layout.ContentPanel;
import com.gwtext.client.widgets.layout.LayoutRegionConfig;

/**
 * This builds on the FormStyleLayout for providing common popup features in a
 * columnar form layout, with a title and a large (ish) icon.
 *
 * @author Michael Neale
 */
public class FormStylePopup {


    private FormStyleLayout form;
	private LayoutDialog dialog;
	private String title;

	private Boolean shadow;
	private Integer width;
	private Integer height;

    public FormStylePopup(String image,
                          final String title) {

        form = new FormStyleLayout( image, title );
        this.title = title;

    }

    public FormStylePopup() {
    	form = new FormStyleLayout();
    }

    public FormStylePopup(String image, final String title, Integer width, Integer height, Boolean shadow) {
    	this(image, title);
    	this.shadow = shadow;
    	this.height = height;
    	this.width = width;
    }



    public void addAttribute(String label, Widget wid) {
        form.addAttribute( label, wid );
    }

    public void addRow(Widget wid) {
        form.addRow( wid );
    }

    public void hide() {
    	this.dialog.destroy();
    }



	public void show() {
		LayoutRegionConfig center = new LayoutRegionConfig() {
    	    {
    	        setAutoScroll(true);
    	        setAlwaysShowTabs(false);
    	    }
    	};


    	dialog = new LayoutDialog(new LayoutDialogConfig() {
    	    {
    	        setModal(true);
    	        setWidth((width == null)? 500 : width.intValue());
    	        setHeight((height == null)? form.getNumAttributes() * 40 + 100 : height.intValue());
    	        setShadow((shadow == null)? true : shadow.booleanValue());
    	        setResizable(true);
    	        setClosable(true);
    	        setProxyDrag(true);
    	        setTitle(title);
    	    }
    	}, center);


    	final BorderLayout layout = dialog.getLayout();

    	ContentPanel cp = new ContentPanel();
    	layout.add(cp);


    	cp.add(form);

		this.dialog.show();
	}
}