package org.drools.guvnor.client.rpc;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class ValidatedResponse_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  public static void deserialize(SerializationStreamReader streamReader, org.drools.guvnor.client.rpc.ValidatedResponse instance) throws SerializationException {
    instance.errorHeader = streamReader.readString();
    instance.errorMessage = streamReader.readString();
    instance.hasErrors = streamReader.readBoolean();
    instance.payload = (com.google.gwt.user.client.rpc.IsSerializable) streamReader.readObject();
    
  }
  
  public static org.drools.guvnor.client.rpc.ValidatedResponse instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new org.drools.guvnor.client.rpc.ValidatedResponse();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.guvnor.client.rpc.ValidatedResponse instance) throws SerializationException {
    streamWriter.writeString(instance.errorHeader);
    streamWriter.writeString(instance.errorMessage);
    streamWriter.writeBoolean(instance.hasErrors);
    streamWriter.writeObject(instance.payload);
    
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.guvnor.client.rpc.ValidatedResponse_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.guvnor.client.rpc.ValidatedResponse_FieldSerializer.deserialize(reader, (org.drools.guvnor.client.rpc.ValidatedResponse)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.guvnor.client.rpc.ValidatedResponse_FieldSerializer.serialize(writer, (org.drools.guvnor.client.rpc.ValidatedResponse)object);
  }
  
}
