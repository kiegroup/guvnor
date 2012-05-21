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

import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.Response;
import com.mvc4g.client.Controller;
import org.jboss.bpm.console.client.ApplicationContext;
import org.jboss.bpm.console.client.URLBuilder;
import org.jboss.bpm.console.client.common.*;
import org.jboss.bpm.console.client.model.JSOParser;
import org.jboss.bpm.console.client.model.ProcessDefinitionRef;
import org.jboss.bpm.console.client.model.ProcessInstanceRef;
import org.jboss.bpm.console.client.util.ConsoleLog;
import org.jboss.errai.bus.client.ErraiBus;
import org.jboss.errai.bus.client.api.base.MessageBuilder;

import java.util.List;

/**
 * Loads a process instance list and updates
 * {@link org.jboss.bpm.console.client.process.InstanceListView}.<br>
 * Triggered by {@link org.jboss.bpm.console.client.model.ProcessDefinitionRef}.
 *
 * @author Heiko.Braun <heiko.braun@jboss.com>
 */
public class UpdateInstancesAction extends AbstractRESTAction
{
  public final static String ID = UpdateInstancesAction.class.getName();

    public UpdateInstancesAction(ApplicationContext applicationContext) {
        super(applicationContext);
    }

    public String getId()
  {
    return ID;
  }

  public String getUrl(Object event)
  {
    final ProcessDefinitionRef def = (ProcessDefinitionRef)event;
    return URLBuilder.getInstance().getProcessInstancesURL(def.getId());
  }

  public RequestBuilder.Method getRequestMethod()
  {
    return RequestBuilder.GET;
  }

  @Override
  protected DataDriven getDataDriven(Controller controller)
  {
    return (InstanceListView) controller.getView(InstanceListView.ID);
  }

  public void handleSuccessfulResponse(final Controller controller, final Object event, Response response)
  {
    final long start = System.currentTimeMillis();

    final ProcessDefinitionRef def = (ProcessDefinitionRef)event;
    List<ProcessInstanceRef> instances = JSOParser.parseProcessInstances(response.getText());
    InstanceListView view = (InstanceListView) controller.getView(InstanceListView.ID);
    if(view!=null) view.update(def, instances);

    ConsoleLog.info("Loaded " + instances.size() + " process instance(s) in "+(System.currentTimeMillis()-start)+" ms");

/*    // refresh tasks
    MessageBuilder.createMessage()
        .toSubject(Model.SUBJECT)
        .command(ModelCommands.HAS_BEEN_UPDATED)
        .with(ModelParts.CLASS, Model.PROCESS_MODEL)
        .noErrorHandling()
        .sendNowWith(ErraiBus.get()
        );*/

  }

}

