package org.drools.guvnor.client.rpc;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class BulkTestRunResult_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native int getPercentCovered(org.drools.guvnor.client.rpc.BulkTestRunResult instance) /*-{
    return instance.@org.drools.guvnor.client.rpc.BulkTestRunResult::percentCovered;
  }-*/;
  
  private static native void setPercentCovered(org.drools.guvnor.client.rpc.BulkTestRunResult instance, int value) 
  /*-{
    instance.@org.drools.guvnor.client.rpc.BulkTestRunResult::percentCovered = value;
  }-*/;
  
  private static native org.drools.guvnor.client.rpc.BuilderResult getResult(org.drools.guvnor.client.rpc.BulkTestRunResult instance) /*-{
    return instance.@org.drools.guvnor.client.rpc.BulkTestRunResult::result;
  }-*/;
  
  private static native void setResult(org.drools.guvnor.client.rpc.BulkTestRunResult instance, org.drools.guvnor.client.rpc.BuilderResult value) 
  /*-{
    instance.@org.drools.guvnor.client.rpc.BulkTestRunResult::result = value;
  }-*/;
  
  private static native org.drools.guvnor.client.rpc.ScenarioResultSummary[] getResults(org.drools.guvnor.client.rpc.BulkTestRunResult instance) /*-{
    return instance.@org.drools.guvnor.client.rpc.BulkTestRunResult::results;
  }-*/;
  
  private static native void setResults(org.drools.guvnor.client.rpc.BulkTestRunResult instance, org.drools.guvnor.client.rpc.ScenarioResultSummary[] value) 
  /*-{
    instance.@org.drools.guvnor.client.rpc.BulkTestRunResult::results = value;
  }-*/;
  
  private static native java.lang.String[] getRulesNotCovered(org.drools.guvnor.client.rpc.BulkTestRunResult instance) /*-{
    return instance.@org.drools.guvnor.client.rpc.BulkTestRunResult::rulesNotCovered;
  }-*/;
  
  private static native void setRulesNotCovered(org.drools.guvnor.client.rpc.BulkTestRunResult instance, java.lang.String[] value) 
  /*-{
    instance.@org.drools.guvnor.client.rpc.BulkTestRunResult::rulesNotCovered = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, org.drools.guvnor.client.rpc.BulkTestRunResult instance) throws SerializationException {
    setPercentCovered(instance, streamReader.readInt());
    setResult(instance, (org.drools.guvnor.client.rpc.BuilderResult) streamReader.readObject());
    setResults(instance, (org.drools.guvnor.client.rpc.ScenarioResultSummary[]) streamReader.readObject());
    setRulesNotCovered(instance, (java.lang.String[]) streamReader.readObject());
    
  }
  
  public static org.drools.guvnor.client.rpc.BulkTestRunResult instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new org.drools.guvnor.client.rpc.BulkTestRunResult();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.guvnor.client.rpc.BulkTestRunResult instance) throws SerializationException {
    streamWriter.writeInt(getPercentCovered(instance));
    streamWriter.writeObject(getResult(instance));
    streamWriter.writeObject(getResults(instance));
    streamWriter.writeObject(getRulesNotCovered(instance));
    
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.guvnor.client.rpc.BulkTestRunResult_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.guvnor.client.rpc.BulkTestRunResult_FieldSerializer.deserialize(reader, (org.drools.guvnor.client.rpc.BulkTestRunResult)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.guvnor.client.rpc.BulkTestRunResult_FieldSerializer.serialize(writer, (org.drools.guvnor.client.rpc.BulkTestRunResult)object);
  }
  
}
