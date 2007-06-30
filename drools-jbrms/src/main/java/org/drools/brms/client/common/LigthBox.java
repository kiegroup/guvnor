package org.drools.brms.client.common;
/*
 * Copyright 2006 Robert Hanson <iamroberthanson AT gmail.com>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *    http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.WindowResizeListener;
import com.google.gwt.user.client.ui.PopupListener;
import com.google.gwt.user.client.ui.PopupPanel;

/**
 * A popup panel that grays out the rest of the page.
 * <p>The image used to gray the page out is "images/lightbox.png"</p>
 * @author BrianG
 */
public class LigthBox implements PopupListener
{
    private PNGImage png;
    private PopupPanel child;
    private PopupPanel background;
    private WindowResizeListener windowResizeListener;


    public LigthBox (PopupPanel child)
    {
        background = new PopupPanel();
        
        windowResizeListener = new WindowResizeListener()
        {
            public void onWindowResized (int width, int height)
            {
                background.setWidth(Integer.toString(width));
                background.setHeight(Integer.toString(height));
                png.setPixelSize(width, height);
                background.setPopupPosition(0, 0);
            }
        };
        Window.addWindowResizeListener(windowResizeListener);

        this.child = child;
        this.child.addPopupListener(this);
    }


    private native void backgroundFixup (Element e)
    /*-{
        // fixes issue with GWT 1.1.10 by hiding the iframe
        if (e.__frame) {
            e.__frame.style.visibility = 'hidden';
        }
    }-*/;


    public void onPopupClosed (PopupPanel sender, boolean autoClosed)
    {
        if (png != null) {
            this.hide();
        }
    }
	

	public void show ()
    {
		int w = DirtyableComposite.getWidth(); 
		int h = DirtyableComposite.getHeight();
		
		background.setWidth(Integer.toString(w));
		background.setHeight(Integer.toString(h));
		background.setWidget(png = new PNGImage("images/lightbox.png", w, h));
		background.setPopupPosition(0, 0);
		hideSelects();
		
        background.show();
        backgroundFixup(background.getElement());

        child.show();
		center();
	}

    
	private void center ()
    {
		//TODO figure out how to center inner popup
		// http://groups.google.com/group/Google-Web-Toolkit/browse_frm/thread/83a4400f9eb93621/69d05316c5891057?lnk=gst&q=offsetwidth&rnum=1#69d05316c5891057
		int left = (DirtyableComposite.getWidth() - child.getOffsetWidth()) / 2;
		int top = 100;
		child.setPopupPosition(left, top);
	}

    
	public void hide ()
    {
        png.removeFromParent();
        png = null;
        showSelects();
        child.hide();
        background.hide();
        Window.removeWindowResizeListener(windowResizeListener);
    }

    
	private native void hideSelects() /*-{
        var selects = $doc.getElementsByTagName("select");
        for (i = 0; i != selects.length; i++) {
            selects[i].style.visibility = "hidden";
        }
    }-*/;

    
	private native void showSelects() /*-{
        var selects = $doc.getElementsByTagName("select");
        for (i = 0; i != selects.length; i++) {
            selects[i].style.visibility = "visible";
        }
    }-*/;

}
