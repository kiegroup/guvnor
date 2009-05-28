package org.drools.guvnor.client.common;
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



import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Widget;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.Window;
import com.gwtext.client.widgets.event.WindowListener;
import com.gwtext.client.widgets.event.WindowListenerAdapter;
import com.gwtext.client.widgets.layout.FitLayout;

/**
 * This builds on the FormStyleLayout for providing common popup features in a
 * columnar form layout, with a title and a large (ish) icon.
 *
 * @author Michael Neale
 */
public class FormStylePopup {


    private FormStyleLayout form;
	private Window dialog;
	private String title;

	private Boolean shadow;
	private Integer width;
	private boolean modal = true;
	private int popLeft = -1;
	private int popTop;
	private Command afterShowEvent;

    public FormStylePopup(String image,
                          final String title) {

        form = new FormStyleLayout( image, title );
        this.title = title;

    }

    public FormStylePopup() {
    	form = new FormStyleLayout();
    }




    public FormStylePopup(String image, final String title, Integer width,  Boolean shadow) {
    	this(image, title);
    	this.shadow = shadow;
    	this.width = width;
    }

    public void clear() {
    	this.form.clear();
    }


    public void addAttribute(String label, Widget wid) {
        form.addAttribute( label, wid );
    }

    public void addRow(Widget wid) {
        form.addRow( wid );
    }

    public void hide() {
        this.dialog.hide();
    	this.dialog.destroy();
    }

    public void setPopupPosition(int left, int top) {
    	this.popLeft = left;
    	this.popTop = top;
    }

    public void setAfterShow(Command c) {
    	this.afterShowEvent = c;
    }

	public void show() {

		dialog = new Window();
		dialog.setAutoScroll(true);
		dialog.setModal(modal );
		dialog.setPlain(true);
		dialog.setConstrainHeader(true);
		dialog.setBodyBorder(false);
		dialog.setBorder(false);
        if (width == null) {
            dialog.setWidth(430);
        } else if (width != -1) {
            dialog.setWidth(width);            
        }
		dialog.setShadow((shadow == null)? true : shadow.booleanValue());
		dialog.setResizable(true);
		dialog.setClosable(true);
		dialog.setTitle(title);
		if (popLeft > -1) {
			dialog.setPosition(popLeft, popTop);
		}


		Panel p = new Panel();
		p.setLayout(new FitLayout());
		p.add(form);
		dialog.add(p);
		p.setBodyBorder(false);
		p.setPaddings(0);

		if (this.afterShowEvent != null) {
			this.dialog.addListener(new WindowListenerAdapter() {
				@Override
				public void onActivate(Panel panel) {
					afterShowEvent.execute();
				}
			});
		}

		this.dialog.show();


	}


	public void setModal(boolean m) {
		this.modal = m;
	}

	public void setTitle(String t) {
		this.title = t;
	}

	public void setWidth(int i) {
		this.width = new Integer(i);
	}


}