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
package org.jboss.bpm.console.client.task;

import com.mvc4g.client.ActionInterface;
import com.mvc4g.client.Controller;
import org.jboss.bpm.console.client.model.TaskRef;
import org.jboss.bpm.console.client.task.events.DetailViewEvent;

/**
 * @author Heiko.Braun <heiko.braun@jboss.com>
 */
public class UpdateDetailsAction implements ActionInterface
{

  public final static String ID = UpdateDetailsAction.class.getName();
  
  public void execute(Controller controller, Object object)
  {
    DetailViewEvent event = (DetailViewEvent)object;
    TaskRef task = event.getTask()!=null? event.getTask() : null;
    TaskDetailView view = (TaskDetailView)controller.getView(event.getViewRef());

    if(task!=null)
      view.update(task);
    else
      view.clearView();    
  }
}
