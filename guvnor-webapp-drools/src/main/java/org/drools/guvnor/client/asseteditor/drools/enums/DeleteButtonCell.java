/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.guvnor.client.asseteditor.drools.enums;

import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.ImageResourceCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import org.drools.guvnor.client.resources.ImagesCore;

import java.util.HashSet;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: raymondefa
 * Date: 6/18/12
 * Time: 12:54 PM
 * To change this template use File | Settings | File Templates.
 */
public class DeleteButtonCell extends ButtonCell {
    @Override
    public void render(Context context, SafeHtml data, SafeHtmlBuilder sb) {
        ImagesCore imagesCore = GWT.create(ImagesCore.class);
        ImageResource imageResource = imagesCore.deleteItemSmall();
        sb.appendHtmlConstant("<input type=\"image\" src=\"" + imageResource.getURL() + "\"  tabindex=\"-1\">");

        sb.appendHtmlConstant("</input>");
    }
}
