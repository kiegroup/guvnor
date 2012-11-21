package org.drools.guvnor.client.rpc;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class SingleScenarioResult_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native java.util.List getAuditLog(org.drools.guvnor.client.rpc.SingleScenarioResult instance) /*-{
    return instance.@org.drools.guvnor.client.rpc.SingleScenarioResult::auditLog;
  }-*/;
  
  private static native void setAuditLog(org.drools.guvnor.client.rpc.SingleScenarioResult instance, java.util.List value) 
  /*-{
    instance.@org.drools.guvnor.client.rpc.SingleScenarioResult::auditLog = value;
  }-*/;
  
  private static native org.drools.guvnor.client.rpc.ScenarioRunResult getResult(org.drools.guvnor.client.rpc.SingleScenarioResult instance) /*-{
    return instance.@org.drools.guvnor.client.rpc.SingleScenarioResult::result;
  }-*/;
  
  private static native void setResult(org.drools.guvnor.client.rpc.SingleScenarioResult instance, org.drools.guvnor.client.rpc.ScenarioRunResult value) 
  /*-{
    instance.@org.drools.guvnor.client.rpc.SingleScenarioResult::result = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, org.drools.guvnor.client.rpc.SingleScenarioResult instance) throws SerializationException {
    setAuditLog(instance, (java.util.List) streamReader.readObject());
    setResult(instance, (org.drools.guvnor.client.rpc.ScenarioRunResult) streamReader.readObject());
    
  }
  
  public static org.drools.guvnor.client.rpc.SingleScenarioResult instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new org.drools.guvnor.client.rpc.SingleScenarioResult();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.guvnor.client.rpc.SingleScenarioResult instance) throws SerializationException {
    streamWriter.writeObject(getAuditLog(instance));
    streamWriter.writeObject(getResult(instance));
    
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.guvnor.client.rpc.SingleScenarioResult_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.guvnor.client.rpc.SingleScenarioResult_FieldSerializer.deserialize(reader, (org.drools.guvnor.client.rpc.SingleScenarioResult)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.guvnor.client.rpc.SingleScenarioResult_FieldSerializer.serialize(writer, (org.drools.guvnor.client.rpc.SingleScenarioResult)object);
  }
  
}
