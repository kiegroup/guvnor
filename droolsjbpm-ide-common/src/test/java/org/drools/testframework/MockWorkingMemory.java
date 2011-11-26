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

package org.drools.testframework;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;

import org.drools.Agenda;
import org.drools.FactException;
import org.drools.FactHandle;
import org.drools.QueryResults;
import org.drools.RuleBase;
import org.drools.SessionConfiguration;
import org.drools.WorkingMemoryEntryPoint;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalKnowledgeRuntime;
import org.drools.common.InternalRuleBase;
import org.drools.common.InternalWorkingMemory;
import org.drools.common.NodeMemory;
import org.drools.common.ObjectStore;
import org.drools.common.ObjectTypeConfigurationRegistry;
import org.drools.common.RuleBasePartitionId;
import org.drools.common.TruthMaintenanceSystem;
import org.drools.common.WorkingMemoryAction;
import org.drools.concurrent.ExecutorService;
import org.drools.event.AgendaEventListener;
import org.drools.event.AgendaEventSupport;
import org.drools.event.RuleBaseEventListener;
import org.drools.event.WorkingMemoryEventListener;
import org.drools.event.WorkingMemoryEventSupport;
import org.drools.process.instance.WorkItemManager;
import org.drools.reteoo.EntryPointNode;
import org.drools.reteoo.LIANodePropagation;
import org.drools.reteoo.ObjectTypeConf;
import org.drools.reteoo.PartitionTaskManager;
import org.drools.rule.EntryPoint;
import org.drools.rule.Rule;
import org.drools.runtime.Calendars;
import org.drools.runtime.Channel;
import org.drools.runtime.Environment;
import org.drools.runtime.ExitPoint;
import org.drools.runtime.ObjectFilter;
import org.drools.runtime.impl.ExecutionResultImpl;
import org.drools.runtime.process.InternalProcessRuntime;
import org.drools.runtime.process.ProcessInstance;
import org.drools.spi.Activation;
import org.drools.spi.AgendaFilter;
import org.drools.spi.AsyncExceptionHandler;
import org.drools.spi.FactHandleFactory;
import org.drools.spi.GlobalResolver;
import org.drools.time.SessionClock;
import org.drools.time.TimerService;
import org.drools.time.impl.JDKTimerService;
import org.drools.type.DateFormats;

public class MockWorkingMemory implements InternalWorkingMemory {
                
    List<Object> facts = new ArrayList<Object>();
    AgendaEventListener agendaEventListener;
    Map<String, Object> globals = new HashMap<String, Object>();
    private SessionClock clock = new JDKTimerService();

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        facts   = (List<Object>)in.readObject();
        agendaEventListener   = (AgendaEventListener)in.readObject();
        globals   = (Map<String, Object>)in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(facts);
        out.writeObject(agendaEventListener);
        out.writeObject(globals);
    }
    
    public Calendars getCalendars() {
        return null;
    }
    
    public Iterator iterateObjects() {
        return this.facts.iterator();
    }

    public void setGlobal(String identifier, Object value) {
        this.globals.put(identifier, value);

    }

    public void addEventListener(AgendaEventListener listener) {
        this.agendaEventListener = listener;
    }

    public void addLIANodePropagation(LIANodePropagation liaNodePropagation) {
    }

    public void clearNodeMemory(NodeMemory node) {
    }

    public void executeQueuedActions() {
    }

    public ExecutorService getExecutorService() {
        return null;
    }

    public FactHandle getFactHandleByIdentity(Object object) {
        return null;
    }

    public FactHandleFactory getFactHandleFactory() {
        return null;
    }

    public int getId() {
        return 0;
    }

    public InternalFactHandle getInitialFactHandle() {
        return null;
    }

    public Lock getLock() {
        return null;
    }

    public long getNextPropagationIdCounter() {
        return 0;
    }

    public Object getNodeMemory(NodeMemory node) {
        return null;
    }

    public ObjectStore getObjectStore() {
        return null;
    }

    public ObjectTypeConfigurationRegistry getObjectTypeConfigurationRegistry() {
        return null;
    }

    public PartitionTaskManager getPartitionTaskManager(RuleBasePartitionId partitionId) {
        return null;
    }

    public TimerService getTimerService() {
        return null;
    }

    public TruthMaintenanceSystem getTruthMaintenanceSystem() {
        return null;
    }

    public boolean isSequential() {
        return false;
    }

    public void queueWorkingMemoryAction(WorkingMemoryAction action) {
    }

    public void removeProcessInstance(ProcessInstance processInstance) {
    }

    public void retract(FactHandle factHandle,
                        boolean removeLogical,
                        boolean updateEqualsMap,
                        Rule rule,
                        Activation activation) throws FactException {
    }

    public void setAgendaEventSupport(AgendaEventSupport agendaEventSupport) {
    }

    public void setExecutorService(ExecutorService executor) {
    }

    public void setId(int id) {
    }

    public void setRuleBase(InternalRuleBase ruleBase) {
    }

    public void setWorkingMemoryEventSupport(WorkingMemoryEventSupport workingMemoryEventSupport) {
    }

    public void clearActivationGroup(String group) {
    }

    public void clearAgenda() {
    }

    public void clearAgendaGroup(String group) {
    }

    public void clearRuleFlowGroup(String group) {
    }

    public int fireAllRules() throws FactException {
        return 0;
    }

    public int fireAllRules(AgendaFilter agendaFilter) throws FactException {
        return 0;
    }

    public int fireAllRules(int fireLimit) throws FactException {
        return 0;
    }

    public int fireAllRules(AgendaFilter agendaFilter,
                            int fireLimit) throws FactException {
        return 0;
    }

    public Agenda getAgenda() {
        return null;
    }

    public FactHandle getFactHandle(Object object) {
        return null;
    }

    public Object getGlobal(String identifier) {
        return null;
    }

    public GlobalResolver getGlobalResolver() {
        return null;
    }

    public Object getObject(org.drools.runtime.rule.FactHandle handle) {
        return null;
    }

    public ProcessInstance getProcessInstance(long id) {
        return null;
    }

    public Collection<ProcessInstance> getProcessInstances() {
        return null;
    }

    public QueryResults getQueryResults(String query) {
        return null;
    }

    public QueryResults getQueryResults(String query,
                                        Object[] arguments) {
        return null;
    }

    public RuleBase getRuleBase() {
        return null;
    }

    public SessionClock getSessionClock() {
        return this.clock;
    }

    public void setSessionClock(SessionClock clock) {
        this.clock = clock;
    }
    
    public WorkItemManager getWorkItemManager() {
        return null;
    }

    public WorkingMemoryEntryPoint getWorkingMemoryEntryPoint(String id) {
        return null;
    }

    public void halt() {
    }

    public Iterator< ? > iterateFactHandles() {
        return null;
    }

    public Iterator< ? > iterateFactHandles(org.drools.runtime.ObjectFilter filter) {
        return null;
    }

    public Iterator< ? > iterateObjects(org.drools.runtime.ObjectFilter filter) {
        return null;
    }

    public void setAsyncExceptionHandler(AsyncExceptionHandler handler) {
    }

    public void setFocus(String focus) {
    }

    public void setGlobalResolver(GlobalResolver globalResolver) {
    }

    public ProcessInstance startProcess(String processId) {
        return null;
    }

    public ProcessInstance startProcess(String processId,
                                        Map<String, Object> parameters) {
        return null;
    }

    public void addEventListener(WorkingMemoryEventListener listener) {
    }

    public List getAgendaEventListeners() {
        return null;
    }

    public List getWorkingMemoryEventListeners() {
        return null;
    }

    public void removeEventListener(WorkingMemoryEventListener listener) {
    }

    public void removeEventListener(AgendaEventListener listener) {
    }

    public void addEventListener(RuleBaseEventListener listener) {
    }

    public List<RuleBaseEventListener> getRuleBaseEventListeners() {
        return null;
    }

    public void removeEventListener(RuleBaseEventListener listener) {
    }

    public FactHandle insert(Object object) throws FactException {
        this.facts .add(object);
        return new MockFactHandle(object.hashCode());
    }

    public FactHandle insert(Object object,
                             boolean dynamic) throws FactException {
        return null;
    }

    public void modifyInsert(FactHandle factHandle,
                             Object object) {
    }

    public void modifyRetract(FactHandle factHandle) {
    }

    public void retract(org.drools.runtime.rule.FactHandle handle) throws FactException {
    }

    public void update(org.drools.runtime.rule.FactHandle handle,
                       Object object) throws FactException {
    }

    public InternalKnowledgeRuntime getKnowledgeRuntime() {
        return null;
    }

    public void setKnowledgeRuntime(InternalKnowledgeRuntime kruntime) {
    }

    public Map<String, ExitPoint> getExitPoints() {
        return null;
    }

    public Environment getEnvironment() {
        return null;
    }
    
    public SessionConfiguration getSessionConfiguration() {
        return null;
    }

    public Map<String, WorkingMemoryEntryPoint> getEntryPoints() {
        return null;
    }

    public void endBatchExecution() {
    }

    public ExecutionResultImpl getExecutionResult() {
        return null;
    }

    public void startBatchExecution(ExecutionResultImpl results) {
    }

    public Collection< Object > getObjects() {
        return null;
    }

    public Collection< Object > getObjects(ObjectFilter filter) {
        return null;
    }

    public void endOperation() {
    }

    public long getIdleTime() {
        return 0;
    }

    public void startOperation() {
    }

    public long getTimeToNextJob() {
        return 0;
    }

    public void updateEntryPointsCache() {
    }

    public void activationFired() {
    }

    public void prepareToFireActivation() {
    }

    public String getEntryPointId() {
        return null;
    }

    public long getFactCount() {
        return 0;
    }

    public long getTotalFactCount() {
        return 0;
    }

    public DateFormats getDateFormats() {
        return null;
    }

    public <T extends org.drools.runtime.rule.FactHandle> Collection<T> getFactHandles() {
        return null;
    }

    public <T extends org.drools.runtime.rule.FactHandle> Collection<T> getFactHandles(ObjectFilter filter) {
        return null;
    }

    public EntryPoint getEntryPoint() {
        return null;
    }

    public void insert(InternalFactHandle handle,
                       Object object,
                       Rule rule,
                       Activation activation,
                       ObjectTypeConf typeConf) {
    }

    public Map<String, Channel> getChannels() {
        return null;
    }

    public InternalProcessRuntime getProcessRuntime() {
        return null;
    }

    public EntryPointNode getEntryPointNode() {
        return null;
    }

	public void dispose() {
		// TODO Auto-generated method stub
		
	}

}
