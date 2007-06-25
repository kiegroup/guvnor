
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


/**
 * Demonstrates {@link com.google.gwt.user.client.ui.Table}.
 */
public class Search extends JBRMSFeature {

  public static ComponentInfo init() {
    return new ComponentInfo(
      "Search",
      "Find the rules you want to edit and manage.") {
      public JBRMSFeature createInstance() {
        return new Search();
      }

    };
  }


  public Search() {
  }

  public void onShow() {
  }
}