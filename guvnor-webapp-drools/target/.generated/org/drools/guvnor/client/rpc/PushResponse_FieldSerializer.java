package org.drools.guvnor.client.rpc;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class PushResponse_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  public static void deserialize(SerializationStreamReader streamReader, org.drools.guvnor.client.rpc.PushResponse instance) throws SerializationException {
    instance.message = streamReader.readString();
    instance.messageType = streamReader.readString();
    
  }
  
  public static org.drools.guvnor.client.rpc.PushResponse instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new org.drools.guvnor.client.rpc.PushResponse();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.guvnor.client.rpc.PushResponse instance) throws SerializationException {
    streamWriter.writeString(instance.message);
    streamWriter.writeString(instance.messageType);
    
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.guvnor.client.rpc.PushResponse_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.guvnor.client.rpc.PushResponse_FieldSerializer.deserialize(reader, (org.drools.guvnor.client.rpc.PushResponse)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.guvnor.client.rpc.PushResponse_FieldSerializer.serialize(writer, (org.drools.guvnor.client.rpc.PushResponse)object);
  }
  
}
