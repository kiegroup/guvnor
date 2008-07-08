package org.drools.guvnor.client.ruleeditor;

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

import org.drools.guvnor.client.common.DirtyableComposite;
import org.drools.guvnor.client.rpc.RuleAsset;
import org.drools.guvnor.client.rpc.RuleContentText;

import com.google.gwt.user.client.ui.HTML;
import com.gwtext.client.core.Ext;

/**
 * This is a proof of concept showing how a text highlighting editor could be achieved.
 * It uses EditArea (I changed the C syntax to look like drl).
 *
 * For this to work, I had to put in Guvnor.html:
 *
    <script type="text/javascript" src="js/edit_area/edit_area_full.js"></script>
	<script type="text/javascript">
		document.editAreaLoader = editAreaLoader;
	</script>
 *
 * this means that I can do $doc.editAreaLoader from within GWT.
 *
 * @author Michael Neale
 */
public class SyntaxHighlightingEditor extends DirtyableComposite implements SaveEventListener {

    final private RuleContentText data;

    final private RuleAsset       asset;
	private String areaId;

    public SyntaxHighlightingEditor(RuleAsset a) {
        asset = a;
        data = (RuleContentText) asset.content;

        if (data.content == null) {
        	data.content = "";
        }
        areaId = Ext.generateId();

        String html = "<textarea id='" + areaId + "' style='height: 350px; width: 100%;' name='" + areaId + "'>" + data.content +"</textarea>";
        HTML h = new HTML();
        h.setHTML(html);

        initWidget(h);

        doAreas(areaId);


    }



    public native void doAreas(String areaId) /*-{
    		$doc.editAreaLoader.init({
			id: areaId	// id of the textarea to transform
			,start_highlight: true	// if start with highlight
			,allow_resize: "both"
			,allow_toggle: true
			,language: "en"
			,syntax: "c"
		});
    }-*/;






	public void onSave() {
		String textVal = getValue(areaId);
		this.data.content = textVal;
	}


	private native String getValue(String areaId) /*-{
		return $doc.editAreaLoader.getValue(areaId);
	}-*/;



	public void onAfterSave() {
		//not needed.
	}


}