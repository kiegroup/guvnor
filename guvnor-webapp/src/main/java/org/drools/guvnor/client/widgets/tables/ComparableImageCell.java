/*
* Copyright 2010 JBoss Inc
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.drools.guvnor.client.widgets.tables;

import org.drools.guvnor.client.resources.ComparableImage;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;


public class ComparableImageCell extends AbstractCell<ComparableImage> {
/*	interface Template extends SafeHtmlTemplates {
		@Template("<img src=\"{0}\"/>")
		SafeHtml img(String url);
	}
	private static Template template;*/
	
    @Override
    public void render(Context context,
                       ComparableImage value,
                       SafeHtmlBuilder sb) {
        if (value != null) {
            // The template will sanitize the URI.
        	sb.append(SafeHtmlUtils.fromTrustedString(value.getImageHTML()));
          }
    }

}
