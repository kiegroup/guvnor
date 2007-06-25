
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


package org.drools.brms.client;

import org.drools.brms.client.decisiontable.EditableDTGrid;

import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Demonstrates the various text widgets.
 */
public class RuleBases extends JBRMSFeature {

  public static ComponentInfo init() {
    return new ComponentInfo(
      "RuleBases",
      "A temporary place holder for DTs.") {
      public JBRMSFeature createInstance() {
        return new RuleBases();
      }

    };
  }


  public RuleBases() {
    VerticalPanel panel = new VerticalPanel();
    panel.setSpacing(8);
    panel.add( new EditableDTGrid("Pricing rules") );
    initWidget(panel);
  }

  public void onShow() {
  }

}