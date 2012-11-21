package org.drools.guvnor.client.rpc;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class AnalysisFactUsage_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  public static void deserialize(SerializationStreamReader streamReader, org.drools.guvnor.client.rpc.AnalysisFactUsage instance) throws SerializationException {
    instance.fields = (org.drools.guvnor.client.rpc.AnalysisFieldUsage[]) streamReader.readObject();
    instance.name = streamReader.readString();
    
  }
  
  public static org.drools.guvnor.client.rpc.AnalysisFactUsage instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new org.drools.guvnor.client.rpc.AnalysisFactUsage();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.guvnor.client.rpc.AnalysisFactUsage instance) throws SerializationException {
    streamWriter.writeObject(instance.fields);
    streamWriter.writeString(instance.name);
    
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.guvnor.client.rpc.AnalysisFactUsage_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.guvnor.client.rpc.AnalysisFactUsage_FieldSerializer.deserialize(reader, (org.drools.guvnor.client.rpc.AnalysisFactUsage)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.guvnor.client.rpc.AnalysisFactUsage_FieldSerializer.serialize(writer, (org.drools.guvnor.client.rpc.AnalysisFactUsage)object);
  }
  
}
