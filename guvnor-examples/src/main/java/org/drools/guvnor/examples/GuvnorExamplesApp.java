/*
 * Copyright 2011 JBoss Inc
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

package org.drools.guvnor.examples;

import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.drools.guvnor.examples.mortgage.client.MortgageClientExample;

public class GuvnorExamplesApp extends JFrame {

    public static void main(String[] args) {
        GuvnorExamplesApp guvnorExamplesApp = new GuvnorExamplesApp();
        guvnorExamplesApp.pack();
        guvnorExamplesApp.setVisible(true);
    }

    public GuvnorExamplesApp() {
        super("Drools examples");
        setContentPane(createContentPane());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private Container createContentPane() {
        JPanel contentPane = new JPanel(new GridLayout(0, 1));
        contentPane.add(new JLabel("Which example do you want to see?"));

        contentPane.add(new JButton(new AbstractAction("MortgageClientExample") {
            public void actionPerformed(ActionEvent e) {
                MortgageClientExample.main(new String[0]);
            }
        }));
        return contentPane;
    }

}
