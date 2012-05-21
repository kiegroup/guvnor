/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2006, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.bpm.console.client.report.search;

import java.util.List;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestionEvent;
import com.google.gwt.user.client.ui.SuggestionHandler;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.mvc4g.client.Controller;
import com.mvc4g.client.ViewInterface;
import org.jboss.bpm.console.client.ApplicationContext;
import org.jboss.bpm.console.client.model.ProcessDefinitionRef;

/**
 * @author Heiko.Braun <heiko.braun@jboss.com>
 */
public class SearchDefinitionView
        extends VerticalPanel implements ViewInterface {

    private Controller controller;
    private ApplicationContext appContext;
    private SearchDelegate delegate;
    private SuggestBox suggestBox;

    private String selection = null;

    private SearchWindow parent;

    public SearchDefinitionView(ApplicationContext appContext, SearchDelegate delegate) {

        this.appContext = appContext;
        this.delegate = delegate;

        this.add(new Label("Loading, please wait..."));
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    private MultiWordSuggestOracle createOracle(List<ProcessDefinitionRef> definitions) {
        MultiWordSuggestOracle oracle = new MultiWordSuggestOracle();

        for (ProcessDefinitionRef p : definitions) {
            oracle.add(p.getId());
        }

        return oracle;
    }

    public void update(List<ProcessDefinitionRef> definitions) {
        this.clear();
        this.selection = null;

        Label desc = new Label("Please enter a process definition ID");
        //desc.setStyleName("bpm-label-header");
        this.add(desc);

        suggestBox = new SuggestBox(
                createOracle(definitions)
        );

        suggestBox.addEventHandler(
                new SuggestionHandler() {
                    public void onSuggestionSelected(SuggestionEvent suggestionEvent) {
                        selection = suggestionEvent.getSelectedSuggestion().getReplacementString();
                    }
                }
        );

        this.add(suggestBox);

        Grid g = new Grid(2, 2);
        g.setWidget(0, 0, new Label("ID: "));
        g.setWidget(0, 1, suggestBox);

        Button button = new Button(delegate.getActionName(),
                new ClickListener() {
                    public void onClick(Widget widget) {
                        if (selection != null) {
                            delegate.handleResult(selection);
                            parent.close();
                        }
                    }
                });

        g.setWidget(1, 1, button);
        this.add(g);

        // TODO: -Rikkola-
//    invalidate();
    }

    void setParent(SearchWindow window) {
        this.parent = window;
    }
}
