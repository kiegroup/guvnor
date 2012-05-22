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
package org.jboss.bpm.console.server;

import org.jboss.bpm.report.ReportFacade;

import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

/**
 * JAX-RS core component.
 *
 * @author Heiko.Braun <heiko.braun@jboss.com>
 */
public class ConsoleServerApplication extends Application
{
  HashSet<Object> singletons = new HashSet<Object>();

  public ConsoleServerApplication()
  {
    singletons.add(new InfoFacade());
    singletons.add(new ProcessMgmtFacade());
    singletons.add(new TaskListFacade());
    singletons.add(new TaskMgmtFacade());
    singletons.add(new UserMgmtFacade());    
    singletons.add(new EngineFacade());
    singletons.add(new FormProcessingFacade());
    //TODO: comment it for now, it is only used in jBPM
    //singletons.add(new ReportFacade() );
    singletons.add(new ProcessHistoryFacade());
  }

  @Override
  public Set<Class<?>> getClasses()
  {
    HashSet<Class<?>> set = new HashSet<Class<?>>();
    return set;
  }

  @Override
  public Set<Object> getSingletons()
  {
    return singletons;
  }
}
