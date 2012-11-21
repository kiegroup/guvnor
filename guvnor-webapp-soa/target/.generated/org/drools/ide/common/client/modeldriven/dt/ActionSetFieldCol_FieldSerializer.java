package org.drools.ide.common.client.modeldriven.dt;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class ActionSetFieldCol_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  public static void deserialize(SerializationStreamReader streamReader, org.drools.ide.common.client.modeldriven.dt.ActionSetFieldCol instance) throws SerializationException {
    instance.boundName = streamReader.readString();
    instance.factField = streamReader.readString();
    instance.type = streamReader.readString();
    instance.update = streamReader.readBoolean();
    instance.valueList = streamReader.readString();
    
    org.drools.ide.common.client.modeldriven.dt.ActionCol_FieldSerializer.deserialize(streamReader, instance);
  }
  
  public static org.drools.ide.common.client.modeldriven.dt.ActionSetFieldCol instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new org.drools.ide.common.client.modeldriven.dt.ActionSetFieldCol();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.ide.common.client.modeldriven.dt.ActionSetFieldCol instance) throws SerializationException {
    streamWriter.writeString(instance.boundName);
    streamWriter.writeString(instance.factField);
    streamWriter.writeString(instance.type);
    streamWriter.writeBoolean(instance.update);
    streamWriter.writeString(instance.valueList);
    
    org.drools.ide.common.client.modeldriven.dt.ActionCol_FieldSerializer.serialize(streamWriter, instance);
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.ide.common.client.modeldriven.dt.ActionSetFieldCol_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.ide.common.client.modeldriven.dt.ActionSetFieldCol_FieldSerializer.deserialize(reader, (org.drools.ide.common.client.modeldriven.dt.ActionSetFieldCol)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.ide.common.client.modeldriven.dt.ActionSetFieldCol_FieldSerializer.serialize(writer, (org.drools.ide.common.client.modeldriven.dt.ActionSetFieldCol)object);
  }
  
}
