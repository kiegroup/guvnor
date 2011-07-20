/*
 * Copyright 2011 JBoss Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package org.drools.guvnor.client.configurations;

public enum Capability {
    SHOW_KNOWLEDGE_BASES_VIEW,  //(show status list view as well) if they have any package perms
    SHOW_CREATE_NEW_ASSET,//if they have any package perms not read only
    SHOW_CREATE_NEW_PACKAGE, //if they are package admin
    SHOW_ADMIN, //if they are admin, package admin??
    SHOW_QA, //if they have any package perms
    SHOW_DEPLOYMENT,  //if they are package admin??
    SHOW_DEPLOYMENT_NEW //can create a new depl, rename etc...
    ;

}
