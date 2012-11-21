package org.cobogw.gwt.user.client.rpc;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class AsyncCallbackCollection_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  public static void deserialize(SerializationStreamReader streamReader, org.cobogw.gwt.user.client.rpc.AsyncCallbackCollection instance) throws SerializationException {
    
    com.google.gwt.user.client.rpc.core.java.util.ArrayList_CustomFieldSerializer.deserialize(streamReader, instance);
  }
  
  public static org.cobogw.gwt.user.client.rpc.AsyncCallbackCollection instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new org.cobogw.gwt.user.client.rpc.AsyncCallbackCollection();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.cobogw.gwt.user.client.rpc.AsyncCallbackCollection instance) throws SerializationException {
    
    com.google.gwt.user.client.rpc.core.java.util.ArrayList_CustomFieldSerializer.serialize(streamWriter, instance);
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.cobogw.gwt.user.client.rpc.AsyncCallbackCollection_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.cobogw.gwt.user.client.rpc.AsyncCallbackCollection_FieldSerializer.deserialize(reader, (org.cobogw.gwt.user.client.rpc.AsyncCallbackCollection)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.cobogw.gwt.user.client.rpc.AsyncCallbackCollection_FieldSerializer.serialize(writer, (org.cobogw.gwt.user.client.rpc.AsyncCallbackCollection)object);
  }
  
}
