package org.drools.guvnor.client.rpc;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class AnalysisFieldUsage_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  public static void deserialize(SerializationStreamReader streamReader, org.drools.guvnor.client.rpc.AnalysisFieldUsage instance) throws SerializationException {
    instance.name = streamReader.readString();
    instance.rules = (java.lang.String[]) streamReader.readObject();
    
  }
  
  public static org.drools.guvnor.client.rpc.AnalysisFieldUsage instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new org.drools.guvnor.client.rpc.AnalysisFieldUsage();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.guvnor.client.rpc.AnalysisFieldUsage instance) throws SerializationException {
    streamWriter.writeString(instance.name);
    streamWriter.writeObject(instance.rules);
    
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.guvnor.client.rpc.AnalysisFieldUsage_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.guvnor.client.rpc.AnalysisFieldUsage_FieldSerializer.deserialize(reader, (org.drools.guvnor.client.rpc.AnalysisFieldUsage)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.guvnor.client.rpc.AnalysisFieldUsage_FieldSerializer.serialize(writer, (org.drools.guvnor.client.rpc.AnalysisFieldUsage)object);
  }
  
}
