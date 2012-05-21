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

import com.google.gwt.user.client.ui.PopupPanel;

/**
 * @author Heiko.Braun <heiko.braun@jboss.com>
 */
public class SearchWindow {

    private PopupPanel window;

    public SearchWindow(String title, SearchDefinitionView view) {
        view.setParent(this);
        createLayoutWindowPanel(title, view);

    }

    /**
     * The 'layout' window panel.
     */
    private void createLayoutWindowPanel(String title, SearchDefinitionView view) {
        //TODO: Needs to use a closable popup -Rikkola-
//        window = new PopupPanel(title);
        window = new PopupPanel();
        window.setAnimationEnabled(true);
        window.setSize("250px", "160px");

        window.setWidget(view);

//        window.addWindowCloseListener(new WindowCloseListener() {
//            public void onWindowClosed() {
//                window = null;
//            }
//
//            public String onWindowClosing() {
//                return null;
//            }
//        });
    }

    public void center() {
        window.center();
    }

    public void close() {
        window.hide();
        window = null;
    }
}
