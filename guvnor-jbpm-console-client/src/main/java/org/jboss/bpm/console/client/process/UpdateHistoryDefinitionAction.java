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

import com.mvc4g.client.ActionInterface;
import org.jboss.bpm.console.client.URLBuilder;
import org.jboss.bpm.console.client.common.AbstractRESTAction;
import org.jboss.bpm.console.client.model.HistoryProcessInstanceRef;
import org.jboss.bpm.console.client.model.JSOParser;
import org.jboss.bpm.console.client.model.ProcessDefinitionRef;
import org.jboss.bpm.console.client.util.ConsoleLog;

import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.RequestBuilder.Method;
import com.mvc4g.client.Controller;
import org.jboss.errai.bus.client.ErraiBus;
import org.jboss.errai.bus.client.api.Message;
import org.jboss.errai.bus.client.api.MessageCallback;
import org.jboss.errai.bus.client.api.base.MessageBuilder;
import org.jboss.errai.bus.client.framework.MessageBus;
import org.jboss.errai.common.client.protocols.MessageParts;


/**
 * @author Maciej Swiderski <swiderski.maciej@gmail.com>
 */
public class UpdateHistoryDefinitionAction implements ActionInterface
{

  public final static String ID = UpdateHistoryDefinitionAction.class.getName();

  private MessageBus bus = ErraiBus.get();

  private Controller controller;

  public UpdateHistoryDefinitionAction()
  {
    bus.subscribe(ID, new MessageCallback()
    {
      public void callback(final Message message)
      {
        // Currently selected process definition
        DefinitionHistoryListView definitionList = (DefinitionHistoryListView)
            controller.getView(DefinitionHistoryListView.ID);
        ProcessDefinitionRef selectedDefinition = definitionList.getSelection();
        assert selectedDefinition!=null;

        HistoryInstanceListView view = (HistoryInstanceListView)
            controller.getView(HistoryInstanceListView.ID);

        if(view!=null) // may not be initialized (lazy)
        {          
          List<HistoryProcessInstanceRef> results = message.get(List.class, "INSTANCE_LIST");
          view.update(selectedDefinition, results);          
        }
      }
    });
  }

  public void execute(Controller controller, Object o)
  {
    this.controller = controller;

    ProcessDefinitionRef def = (ProcessDefinitionRef)o;

    MessageBuilder.createMessage()
        .toSubject("JBPM_HISTORY_SERVICE")
        .command("GET_FINISHED_PROCESS_INSTANCES")
        .with(MessageParts.ReplyTo, ID)
        .with("PROCESS_DEFINITION_ID", def.getId())
        .noErrorHandling()
        .sendNowWith(bus);
  }

}
