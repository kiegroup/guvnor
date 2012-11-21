package org.drools.ide.common.client.modeldriven.dt;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class ActionRetractFactCol_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  public static void deserialize(SerializationStreamReader streamReader, org.drools.ide.common.client.modeldriven.dt.ActionRetractFactCol instance) throws SerializationException {
    instance.boundName = streamReader.readString();
    
    org.drools.ide.common.client.modeldriven.dt.ActionCol_FieldSerializer.deserialize(streamReader, instance);
  }
  
  public static org.drools.ide.common.client.modeldriven.dt.ActionRetractFactCol instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new org.drools.ide.common.client.modeldriven.dt.ActionRetractFactCol();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.ide.common.client.modeldriven.dt.ActionRetractFactCol instance) throws SerializationException {
    streamWriter.writeString(instance.boundName);
    
    org.drools.ide.common.client.modeldriven.dt.ActionCol_FieldSerializer.serialize(streamWriter, instance);
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.ide.common.client.modeldriven.dt.ActionRetractFactCol_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.ide.common.client.modeldriven.dt.ActionRetractFactCol_FieldSerializer.deserialize(reader, (org.drools.ide.common.client.modeldriven.dt.ActionRetractFactCol)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.ide.common.client.modeldriven.dt.ActionRetractFactCol_FieldSerializer.serialize(writer, (org.drools.ide.common.client.modeldriven.dt.ActionRetractFactCol)object);
  }
  
}
