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



import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Image;

/**
 * Really just an image, but tacks on the image-Button style name.
 * @author Michael Neale
 *
 */
public class ImageButton extends Image {

    public ImageButton(String img) {
        super(img);
        setStyleName( "image-Button" );
    }

    public ImageButton(String img, String tooltip) {
        super(img);
        setStyleName( "image-Button" );
        setTitle( tooltip );
    }

    public ImageButton(String img, String tooltip, ClickListener action) {
    	this(img, tooltip);
    	this.addClickListener(action);
    }

}