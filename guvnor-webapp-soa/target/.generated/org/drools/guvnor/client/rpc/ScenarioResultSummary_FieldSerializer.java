package org.drools.guvnor.client.rpc;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class ScenarioResultSummary_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native int getFailures(org.drools.guvnor.client.rpc.ScenarioResultSummary instance) /*-{
    return instance.@org.drools.guvnor.client.rpc.ScenarioResultSummary::failures;
  }-*/;
  
  private static native void setFailures(org.drools.guvnor.client.rpc.ScenarioResultSummary instance, int value) 
  /*-{
    instance.@org.drools.guvnor.client.rpc.ScenarioResultSummary::failures = value;
  }-*/;
  
  private static native java.lang.String getScenarioDescription(org.drools.guvnor.client.rpc.ScenarioResultSummary instance) /*-{
    return instance.@org.drools.guvnor.client.rpc.ScenarioResultSummary::scenarioDescription;
  }-*/;
  
  private static native void setScenarioDescription(org.drools.guvnor.client.rpc.ScenarioResultSummary instance, java.lang.String value) 
  /*-{
    instance.@org.drools.guvnor.client.rpc.ScenarioResultSummary::scenarioDescription = value;
  }-*/;
  
  private static native java.lang.String getScenarioName(org.drools.guvnor.client.rpc.ScenarioResultSummary instance) /*-{
    return instance.@org.drools.guvnor.client.rpc.ScenarioResultSummary::scenarioName;
  }-*/;
  
  private static native void setScenarioName(org.drools.guvnor.client.rpc.ScenarioResultSummary instance, java.lang.String value) 
  /*-{
    instance.@org.drools.guvnor.client.rpc.ScenarioResultSummary::scenarioName = value;
  }-*/;
  
  private static native int getTotal(org.drools.guvnor.client.rpc.ScenarioResultSummary instance) /*-{
    return instance.@org.drools.guvnor.client.rpc.ScenarioResultSummary::total;
  }-*/;
  
  private static native void setTotal(org.drools.guvnor.client.rpc.ScenarioResultSummary instance, int value) 
  /*-{
    instance.@org.drools.guvnor.client.rpc.ScenarioResultSummary::total = value;
  }-*/;
  
  private static native java.lang.String getUuid(org.drools.guvnor.client.rpc.ScenarioResultSummary instance) /*-{
    return instance.@org.drools.guvnor.client.rpc.ScenarioResultSummary::uuid;
  }-*/;
  
  private static native void setUuid(org.drools.guvnor.client.rpc.ScenarioResultSummary instance, java.lang.String value) 
  /*-{
    instance.@org.drools.guvnor.client.rpc.ScenarioResultSummary::uuid = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, org.drools.guvnor.client.rpc.ScenarioResultSummary instance) throws SerializationException {
    setFailures(instance, streamReader.readInt());
    setScenarioDescription(instance, streamReader.readString());
    setScenarioName(instance, streamReader.readString());
    setTotal(instance, streamReader.readInt());
    setUuid(instance, streamReader.readString());
    
  }
  
  public static org.drools.guvnor.client.rpc.ScenarioResultSummary instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new org.drools.guvnor.client.rpc.ScenarioResultSummary();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.guvnor.client.rpc.ScenarioResultSummary instance) throws SerializationException {
    streamWriter.writeInt(getFailures(instance));
    streamWriter.writeString(getScenarioDescription(instance));
    streamWriter.writeString(getScenarioName(instance));
    streamWriter.writeInt(getTotal(instance));
    streamWriter.writeString(getUuid(instance));
    
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.guvnor.client.rpc.ScenarioResultSummary_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.guvnor.client.rpc.ScenarioResultSummary_FieldSerializer.deserialize(reader, (org.drools.guvnor.client.rpc.ScenarioResultSummary)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.guvnor.client.rpc.ScenarioResultSummary_FieldSerializer.serialize(writer, (org.drools.guvnor.client.rpc.ScenarioResultSummary)object);
  }
  
}
