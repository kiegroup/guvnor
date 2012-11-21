package org.drools.guvnor.client.rpc;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class PageRequest_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  public static void deserialize(SerializationStreamReader streamReader, org.drools.guvnor.client.rpc.PageRequest instance) throws SerializationException {
    instance.pageSize = (java.lang.Integer) streamReader.readObject();
    instance.startRowIndex = streamReader.readInt();
    
  }
  
  public static org.drools.guvnor.client.rpc.PageRequest instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new org.drools.guvnor.client.rpc.PageRequest();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.guvnor.client.rpc.PageRequest instance) throws SerializationException {
    streamWriter.writeObject(instance.pageSize);
    streamWriter.writeInt(instance.startRowIndex);
    
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.guvnor.client.rpc.PageRequest_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.guvnor.client.rpc.PageRequest_FieldSerializer.deserialize(reader, (org.drools.guvnor.client.rpc.PageRequest)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.guvnor.client.rpc.PageRequest_FieldSerializer.serialize(writer, (org.drools.guvnor.client.rpc.PageRequest)object);
  }
  
}
