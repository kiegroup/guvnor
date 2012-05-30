/*
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

/*Every That is commented in relate to de attribute data is because a NEP*/

package org.drools.guvnor.client.asseteditor.drools.springcontext;

import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.TextArea;

import org.drools.guvnor.client.asseteditor.EditorWidget;
import org.drools.guvnor.client.asseteditor.RuleViewer;
import org.drools.guvnor.client.asseteditor.SaveEventListener;
import org.drools.guvnor.client.common.DirtyableComposite;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.rpc.Asset;
import org.drools.guvnor.client.rpc.RuleContentText;

/**
 * This is the default Spring editor widget - more to come later.
 */
public class SpringContextEditor extends DirtyableComposite
        implements
        EditorWidget, SaveEventListener {

    private TextArea text;
    final private RuleContentText data;

    public SpringContextEditor(Asset a,
                               RuleViewer v,
                               ClientFactory clientFactory,
                               EventBus eventBus) {
        this(a);
    }

    public SpringContextEditor(Asset a) {
        this(a,
                -1);
    }

    public SpringContextEditor(Asset asset,
                               int visibleLines) {

        data = (RuleContentText) asset.getContent();

        if (data.content == null) {
            data.content = "Empty!";
        }

        Grid layout = new Grid(1,
                2);


        SpringContextElementsBrowser browser = new SpringContextElementsBrowser(new SpringContextElementSelectedListener() {

            public void onElementSelected(String elementName, String pasteValue) {
                insertText(pasteValue, true);
            }
        });

        layout.setWidget(0,
                0,
                browser);
        text = new TextArea();
        text.setWidth("100%");
        text.setVisibleLines((visibleLines == -1) ? 25 : visibleLines);
        text.setText(data.content);
        text.getElement().setAttribute("spellcheck",
                "false"); //NON-NLS

        text.setStyleName("default-text-Area"); //NON-NLS

        text.addChangeHandler(new ChangeHandler() {
            public void onChange(ChangeEvent event) {
                data.content = text.getText();
                makeDirty();
            }
        });

        text.addKeyDownHandler(new KeyDownHandler() {

            public void onKeyDown(KeyDownEvent event) {
                if (event.getNativeKeyCode() == KeyCodes.KEY_TAB) {
                    event.preventDefault();
                    event.stopPropagation();
                    int pos = text.getCursorPos();
                    insertText("\t", false);
                    text.setCursorPos(pos + 1);
                    text.cancelKey();
                    text.setFocus(true);
                }
            }
        });

        layout.setWidget(0,
                1,
                text);


        layout.getColumnFormatter().setWidth(0,
                "10%");
        layout.getColumnFormatter().setWidth(1,
                "90%");
        layout.getCellFormatter().setAlignment(0,
                0,
                HasHorizontalAlignment.ALIGN_LEFT,
                HasVerticalAlignment.ALIGN_TOP);
        layout.getCellFormatter().setAlignment(0,
                1,
                HasHorizontalAlignment.ALIGN_LEFT,
                HasVerticalAlignment.ALIGN_TOP);
        layout.setWidth("95%");

        initWidget(layout);

    }

    void insertText(String ins, boolean isSpecialPaste) {

        text.setFocus(true);

        int i = text.getCursorPos();
        String left = text.getText().substring(0,
                i);
        String right = text.getText().substring(i,
                text.getText().length());
        int cursorPosition = left.toCharArray().length;
        if (isSpecialPaste) {
            int p = ins.indexOf("|");
            if (p > -1) {
                cursorPosition += p;
                ins = ins.replaceAll("\\|", "");
            }

        }

        text.setText(left + ins + right);
        this.data.content = text.getText();

        text.setCursorPos(cursorPosition);
    }


    public void onSave() {
        //data.content = text.getText();
        //asset.content = data;

    }

    public void onAfterSave() {

    }


}
