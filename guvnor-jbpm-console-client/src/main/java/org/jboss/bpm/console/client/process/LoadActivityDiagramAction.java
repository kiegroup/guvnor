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
package org.jboss.bpm.console.client.process;

import java.util.List;

import org.jboss.bpm.console.client.ApplicationContext;
import org.jboss.bpm.console.client.URLBuilder;
import org.jboss.bpm.console.client.common.AbstractRESTAction;
import org.jboss.bpm.console.client.model.ActiveNodeInfo;
import org.jboss.bpm.console.client.model.DTOParser;
import org.jboss.bpm.console.client.model.ProcessInstanceRef;
import org.jboss.bpm.console.client.process.events.ActivityDiagramResultEvent;

import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.Response;
import com.mvc4g.client.Controller;

/**
 * @author Heiko.Braun <heiko.braun@jboss.com>
 */
public class LoadActivityDiagramAction extends AbstractRESTAction
{
  public final static String ID = LoadActivityDiagramAction.class.getName();

    protected LoadActivityDiagramAction(ApplicationContext applicationContext) {
        super(applicationContext);
    }

    public String getId()
  {
    return ID;
  }

  public String getUrl(Object event)
  {
    ProcessInstanceRef inst = (ProcessInstanceRef)event;
    return URLBuilder.getInstance().getActiveNodeInfoURL(inst.getId());
  }

  public RequestBuilder.Method getRequestMethod()
  {
    return RequestBuilder.GET;
  }

  public void handleSuccessfulResponse(
      final Controller controller, final Object event, Response response)
  {
    ProcessInstanceRef inst = (ProcessInstanceRef)event;

    List<ActiveNodeInfo> activeNodeInfos = DTOParser.parseActiveNodeInfo(response.getText());
    // update view
    ActivityDiagramView view = (ActivityDiagramView) controller.getView(ActivityDiagramView.ID);
    view.update(new ActivityDiagramResultEvent(URLBuilder.getInstance().getProcessImageURL(inst.getDefinitionId()), activeNodeInfos));
    }
  
}
