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
package org.jboss.bpm.console.server.integration;

import org.jboss.bpm.console.client.model.ProcessDefinitionRef;
import org.jboss.bpm.console.client.model.ProcessInstanceRef;

import java.util.List;
import java.util.Map;

/**
 * @author Heiko.Braun <heiko.braun@jboss.com>
 * @author Thomas.Diesler@jboss.com
 */
public interface ProcessManagement
{
  List<ProcessDefinitionRef> getProcessDefinitions();

  ProcessDefinitionRef getProcessDefinition(String definitionId);

  List<ProcessDefinitionRef> removeProcessDefinition(String definitionId);

  List<ProcessInstanceRef> getProcessInstances(String definitionId);

  ProcessInstanceRef getProcessInstance(String instanceId);

  ProcessInstanceRef newInstance(String defintionId);

  ProcessInstanceRef newInstance(String definitionId, Map<String, Object> processVars);
  
  Map<String, Object> getInstanceData(String instanceId);

  void setInstanceData(String instanceId, Map<String, Object> data);

  void endInstance(String instanceId, ProcessInstanceRef.RESULT result);

  void deleteInstance(String instanceId);

  void setProcessState(String instanceId, ProcessInstanceRef.STATE nextState);

  void signalExecution(String executionId, String signal);
  
}
