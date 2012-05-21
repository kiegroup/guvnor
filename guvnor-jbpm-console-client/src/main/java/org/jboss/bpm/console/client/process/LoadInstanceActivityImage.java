/*
 * Copyright 2009 JBoss, a divison Red Hat, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.bpm.console.client.process;

import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.Response;
import com.mvc4g.client.Controller;
import org.jboss.bpm.console.client.ApplicationContext;
import org.jboss.bpm.console.client.URLBuilder;
import org.jboss.bpm.console.client.common.AbstractRESTAction;
import org.jboss.bpm.console.client.model.ProcessInstanceRef;

/**
 * @author Heiko.Braun <heiko.braun@jboss.com>
 */
public class LoadInstanceActivityImage extends AbstractRESTAction
{
    public final static String ID = LoadActivityDiagramAction.class.getName();

    protected LoadInstanceActivityImage(ApplicationContext applicationContext) {
        super(applicationContext);
    }

    public String getId()
    {
        return ID;
    }

    public String getUrl(Object event)
    {        
        ProcessInstanceRef inst = (ProcessInstanceRef)event;
        return URLBuilder.getInstance().getActivityImage(inst.getDefinitionId(),inst.getId());
    }

    public RequestBuilder.Method getRequestMethod()
    {
        return RequestBuilder.GET;
    }

    public void handleSuccessfulResponse(
            final Controller controller, final Object event, Response response)
    {
        ProcessInstanceRef inst = (ProcessInstanceRef)event;

        // update view
        ActivityDiagramView view = (ActivityDiagramView) controller.getView(ActivityDiagramView.ID);
        String url = URLBuilder.getInstance().getActivityImage(inst.getDefinitionId(), inst.getId());
        view.update(url);
    }
}