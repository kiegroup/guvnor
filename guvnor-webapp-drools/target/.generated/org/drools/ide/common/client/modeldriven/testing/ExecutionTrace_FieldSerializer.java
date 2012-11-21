package org.drools.ide.common.client.modeldriven.testing;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class ExecutionTrace_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native java.lang.Long getExecutionTimeResult(org.drools.ide.common.client.modeldriven.testing.ExecutionTrace instance) /*-{
    return instance.@org.drools.ide.common.client.modeldriven.testing.ExecutionTrace::executionTimeResult;
  }-*/;
  
  private static native void setExecutionTimeResult(org.drools.ide.common.client.modeldriven.testing.ExecutionTrace instance, java.lang.Long value) 
  /*-{
    instance.@org.drools.ide.common.client.modeldriven.testing.ExecutionTrace::executionTimeResult = value;
  }-*/;
  
  private static native java.lang.Long getNumberOfRulesFired(org.drools.ide.common.client.modeldriven.testing.ExecutionTrace instance) /*-{
    return instance.@org.drools.ide.common.client.modeldriven.testing.ExecutionTrace::numberOfRulesFired;
  }-*/;
  
  private static native void setNumberOfRulesFired(org.drools.ide.common.client.modeldriven.testing.ExecutionTrace instance, java.lang.Long value) 
  /*-{
    instance.@org.drools.ide.common.client.modeldriven.testing.ExecutionTrace::numberOfRulesFired = value;
  }-*/;
  
  private static native java.lang.String[] getRulesFired(org.drools.ide.common.client.modeldriven.testing.ExecutionTrace instance) /*-{
    return instance.@org.drools.ide.common.client.modeldriven.testing.ExecutionTrace::rulesFired;
  }-*/;
  
  private static native void setRulesFired(org.drools.ide.common.client.modeldriven.testing.ExecutionTrace instance, java.lang.String[] value) 
  /*-{
    instance.@org.drools.ide.common.client.modeldriven.testing.ExecutionTrace::rulesFired = value;
  }-*/;
  
  private static native java.util.Date getScenarioSimulatedDate(org.drools.ide.common.client.modeldriven.testing.ExecutionTrace instance) /*-{
    return instance.@org.drools.ide.common.client.modeldriven.testing.ExecutionTrace::scenarioSimulatedDate;
  }-*/;
  
  private static native void setScenarioSimulatedDate(org.drools.ide.common.client.modeldriven.testing.ExecutionTrace instance, java.util.Date value) 
  /*-{
    instance.@org.drools.ide.common.client.modeldriven.testing.ExecutionTrace::scenarioSimulatedDate = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, org.drools.ide.common.client.modeldriven.testing.ExecutionTrace instance) throws SerializationException {
    setExecutionTimeResult(instance, (java.lang.Long) streamReader.readObject());
    setNumberOfRulesFired(instance, (java.lang.Long) streamReader.readObject());
    setRulesFired(instance, (java.lang.String[]) streamReader.readObject());
    setScenarioSimulatedDate(instance, (java.util.Date) streamReader.readObject());
    
  }
  
  public static org.drools.ide.common.client.modeldriven.testing.ExecutionTrace instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new org.drools.ide.common.client.modeldriven.testing.ExecutionTrace();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.ide.common.client.modeldriven.testing.ExecutionTrace instance) throws SerializationException {
    streamWriter.writeObject(getExecutionTimeResult(instance));
    streamWriter.writeObject(getNumberOfRulesFired(instance));
    streamWriter.writeObject(getRulesFired(instance));
    streamWriter.writeObject(getScenarioSimulatedDate(instance));
    
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.ide.common.client.modeldriven.testing.ExecutionTrace_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.ide.common.client.modeldriven.testing.ExecutionTrace_FieldSerializer.deserialize(reader, (org.drools.ide.common.client.modeldriven.testing.ExecutionTrace)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.ide.common.client.modeldriven.testing.ExecutionTrace_FieldSerializer.serialize(writer, (org.drools.ide.common.client.modeldriven.testing.ExecutionTrace)object);
  }
  
}
