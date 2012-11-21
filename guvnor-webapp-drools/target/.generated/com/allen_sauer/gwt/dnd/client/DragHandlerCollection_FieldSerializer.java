package com.allen_sauer.gwt.dnd.client;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class DragHandlerCollection_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  public static void deserialize(SerializationStreamReader streamReader, com.allen_sauer.gwt.dnd.client.DragHandlerCollection instance) throws SerializationException {
    
    com.google.gwt.user.client.rpc.core.java.util.ArrayList_CustomFieldSerializer.deserialize(streamReader, instance);
  }
  
  public static com.allen_sauer.gwt.dnd.client.DragHandlerCollection instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new com.allen_sauer.gwt.dnd.client.DragHandlerCollection();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, com.allen_sauer.gwt.dnd.client.DragHandlerCollection instance) throws SerializationException {
    
    com.google.gwt.user.client.rpc.core.java.util.ArrayList_CustomFieldSerializer.serialize(streamWriter, instance);
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return com.allen_sauer.gwt.dnd.client.DragHandlerCollection_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    com.allen_sauer.gwt.dnd.client.DragHandlerCollection_FieldSerializer.deserialize(reader, (com.allen_sauer.gwt.dnd.client.DragHandlerCollection)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    com.allen_sauer.gwt.dnd.client.DragHandlerCollection_FieldSerializer.serialize(writer, (com.allen_sauer.gwt.dnd.client.DragHandlerCollection)object);
  }
  
}
