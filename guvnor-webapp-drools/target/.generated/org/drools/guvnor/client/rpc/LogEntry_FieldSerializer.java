package org.drools.guvnor.client.rpc;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class LogEntry_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  public static void deserialize(SerializationStreamReader streamReader, org.drools.guvnor.client.rpc.LogEntry instance) throws SerializationException {
    instance.message = streamReader.readString();
    instance.severity = streamReader.readInt();
    instance.timestamp = (java.util.Date) streamReader.readObject();
    
  }
  
  public static org.drools.guvnor.client.rpc.LogEntry instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new org.drools.guvnor.client.rpc.LogEntry();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.guvnor.client.rpc.LogEntry instance) throws SerializationException {
    streamWriter.writeString(instance.message);
    streamWriter.writeInt(instance.severity);
    streamWriter.writeObject(instance.timestamp);
    
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.guvnor.client.rpc.LogEntry_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.guvnor.client.rpc.LogEntry_FieldSerializer.deserialize(reader, (org.drools.guvnor.client.rpc.LogEntry)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.guvnor.client.rpc.LogEntry_FieldSerializer.serialize(writer, (org.drools.guvnor.client.rpc.LogEntry)object);
  }
  
}
