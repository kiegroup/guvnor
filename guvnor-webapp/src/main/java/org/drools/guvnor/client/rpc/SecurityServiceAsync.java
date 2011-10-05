/*
 * Copyright 2010 JBoss Inc
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

package org.drools.guvnor.client.rpc;


import org.drools.guvnor.client.configurations.Capability;

import com.google.gwt.user.client.rpc.AsyncCallback;

import java.util.List;

/**
 * Contains methods for authenticating/authorising from the front end.
 */
public interface SecurityServiceAsync {


    public void login(String userName, String password, AsyncCallback cb);


    public void logout(AsyncCallback cb);

    public void getCurrentUser(AsyncCallback<UserSecurityContext> cb);

    public void getUserCapabilities(AsyncCallback<List<Capability>> capabilities);
}
