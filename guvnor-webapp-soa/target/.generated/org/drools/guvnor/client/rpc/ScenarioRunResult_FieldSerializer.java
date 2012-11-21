package org.drools.guvnor.client.rpc;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class ScenarioRunResult_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native java.util.List getErrors(org.drools.guvnor.client.rpc.ScenarioRunResult instance) /*-{
    return instance.@org.drools.guvnor.client.rpc.ScenarioRunResult::errors;
  }-*/;
  
  private static native void setErrors(org.drools.guvnor.client.rpc.ScenarioRunResult instance, java.util.List value) 
  /*-{
    instance.@org.drools.guvnor.client.rpc.ScenarioRunResult::errors = value;
  }-*/;
  
  private static native org.drools.ide.common.client.testscenarios.Scenario getScenario(org.drools.guvnor.client.rpc.ScenarioRunResult instance) /*-{
    return instance.@org.drools.guvnor.client.rpc.ScenarioRunResult::scenario;
  }-*/;
  
  private static native void setScenario(org.drools.guvnor.client.rpc.ScenarioRunResult instance, org.drools.ide.common.client.testscenarios.Scenario value) 
  /*-{
    instance.@org.drools.guvnor.client.rpc.ScenarioRunResult::scenario = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, org.drools.guvnor.client.rpc.ScenarioRunResult instance) throws SerializationException {
    setErrors(instance, (java.util.List) streamReader.readObject());
    setScenario(instance, (org.drools.ide.common.client.testscenarios.Scenario) streamReader.readObject());
    
  }
  
  public static org.drools.guvnor.client.rpc.ScenarioRunResult instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new org.drools.guvnor.client.rpc.ScenarioRunResult();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.guvnor.client.rpc.ScenarioRunResult instance) throws SerializationException {
    streamWriter.writeObject(getErrors(instance));
    streamWriter.writeObject(getScenario(instance));
    
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.guvnor.client.rpc.ScenarioRunResult_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.guvnor.client.rpc.ScenarioRunResult_FieldSerializer.deserialize(reader, (org.drools.guvnor.client.rpc.ScenarioRunResult)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.guvnor.client.rpc.ScenarioRunResult_FieldSerializer.serialize(writer, (org.drools.guvnor.client.rpc.ScenarioRunResult)object);
  }
  
}
