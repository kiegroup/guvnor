package org.drools.ide.common.client.modeldriven.dt;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class ActionInsertFactCol_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  public static void deserialize(SerializationStreamReader streamReader, org.drools.ide.common.client.modeldriven.dt.ActionInsertFactCol instance) throws SerializationException {
    instance.boundName = streamReader.readString();
    instance.factField = streamReader.readString();
    instance.factType = streamReader.readString();
    instance.type = streamReader.readString();
    instance.valueList = streamReader.readString();
    
    org.drools.ide.common.client.modeldriven.dt.ActionCol_FieldSerializer.deserialize(streamReader, instance);
  }
  
  public static org.drools.ide.common.client.modeldriven.dt.ActionInsertFactCol instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new org.drools.ide.common.client.modeldriven.dt.ActionInsertFactCol();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.ide.common.client.modeldriven.dt.ActionInsertFactCol instance) throws SerializationException {
    streamWriter.writeString(instance.boundName);
    streamWriter.writeString(instance.factField);
    streamWriter.writeString(instance.factType);
    streamWriter.writeString(instance.type);
    streamWriter.writeString(instance.valueList);
    
    org.drools.ide.common.client.modeldriven.dt.ActionCol_FieldSerializer.serialize(streamWriter, instance);
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.ide.common.client.modeldriven.dt.ActionInsertFactCol_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.ide.common.client.modeldriven.dt.ActionInsertFactCol_FieldSerializer.deserialize(reader, (org.drools.ide.common.client.modeldriven.dt.ActionInsertFactCol)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.ide.common.client.modeldriven.dt.ActionInsertFactCol_FieldSerializer.serialize(writer, (org.drools.ide.common.client.modeldriven.dt.ActionInsertFactCol)object);
  }
  
}
