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

package org.drools.guvnor.client.asseteditor.drools;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.user.client.ui.*;

import org.drools.guvnor.client.asseteditor.RuleViewer;
import org.drools.guvnor.client.common.DirtyableComposite;
import org.drools.guvnor.client.common.ImageButton;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.moduleeditor.drools.SuggestionCompletionCache;
import org.drools.guvnor.client.resources.Images;
import org.drools.guvnor.client.rpc.Asset;
import org.drools.guvnor.client.rpc.RuleContentText;
import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.ide.common.client.modeldriven.brl.DSLSentence;

/**
 * This is a textual rule editor, which provides DSL content assistance. This is
 * similar (but simpler) to the IDE based one.
 */

public class DSLRuleEditor extends DirtyableComposite {

    private static Images images = GWT.create(Images.class);

    private TextArea text;
    final private RuleContentText data;
    private DSLSentence[] conditions;
    private DSLSentence[] actions;

    public DSLRuleEditor(Asset asset,
                         RuleViewer viewer) {
        this(asset);
    }

    public DSLRuleEditor(Asset asset) {

        RuleContentText cont = (RuleContentText) asset.getContent();

        this.data = cont;
        text = new TextArea();
        text.setWidth("100%");
        text.setVisibleLines(16);
        text.setText(data.content);

        SuggestionCompletionEngine eng = SuggestionCompletionCache.getInstance().getEngineFromCache(asset.getMetaData().getModuleName());
        this.actions = eng.actionDSLSentences;
        this.conditions = eng.conditionDSLSentences;

        text.setStyleName("dsl-text-Editor"); //NON-NLS

        FlexTable layout = new FlexTable();
        layout.setWidget(0,
                0,
                text);

        text.addChangeHandler(new ChangeHandler() {
            public void onChange(ChangeEvent event) {
                data.content = text.getText();
                makeDirty();
            }
        });

        text.addKeyDownHandler(new KeyDownHandler() {
            public void onKeyDown(KeyDownEvent event) {
                if (event.getNativeKeyCode() == ' ' && event.getNativeKeyCode() == KeyCodes.KEY_CTRL) {
                    showInTextOptions();
                }

                if (event.getNativeKeyCode() == KeyCodes.KEY_TAB) {
                    int nextPos = text.getCursorPos() + 1;
                    text.cancelKey();
                    insertText("\t");
                    text.setCursorPos(nextPos);

                }
            }
        });

        VerticalPanel vert = new VerticalPanel();

        Image lhsOptions = new ImageButton(images.newDSLPattern());
        Constants constants = GWT.create(Constants.class);
        final String msg = constants.AddANewCondition();
        lhsOptions.setTitle(msg);
        lhsOptions.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                showSuggestions(conditions);
            }
        });

        Image rhsOptions = new ImageButton(images.newDSLAction());
        final String msg2 = constants.AddAnAction();
        rhsOptions.setTitle(msg2);
        rhsOptions.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                showSuggestions(actions);
            }
        });

        vert.add(lhsOptions);
        vert.add(rhsOptions);

        layout.setWidget(0,
                1,
                vert);

        layout.getCellFormatter().setWidth(0,
                0,
                "95%");
        layout.getFlexCellFormatter().setAlignment(0,
                0,
                HasHorizontalAlignment.ALIGN_LEFT,
                HasVerticalAlignment.ALIGN_TOP);
        layout.getCellFormatter().setWidth(0,
                1,
                "5%");
        layout.getFlexCellFormatter().setAlignment(0,
                1,
                HasHorizontalAlignment.ALIGN_CENTER,
                HasVerticalAlignment.ALIGN_MIDDLE);

        layout.setWidth("100%");
        layout.setHeight("100%");

        initWidget(layout);
    }

    protected void showInTextOptions() {
        String prev = text.getText().substring(0,
                this.text.getCursorPos());
        if (prev.indexOf("then") > -1) {
            showSuggestions(this.actions);
        } else {
            showSuggestions(this.conditions);
        }
    }

    private void showSuggestions(DSLSentence[] items) {
        ChoiceList choice = new ChoiceList(items,
                this);
        choice.setPopupPosition(text.getAbsoluteLeft() + 20,
                text.getAbsoluteTop() + 20);
        choice.show();
    }

    void insertText(String ins) {
        int i = text.getCursorPos();
        String left = text.getText().substring(0,
                i);
        String right = text.getText().substring(i,
                text.getText().length());
        text.setText(left + ins + right);
        this.data.content = text.getText();
    }

}
