package org.drools.ide.common.client.modeldriven.dt;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class ConditionCol_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  public static void deserialize(SerializationStreamReader streamReader, org.drools.ide.common.client.modeldriven.dt.ConditionCol instance) throws SerializationException {
    instance.boundName = streamReader.readString();
    instance.constraintValueType = streamReader.readInt();
    instance.factField = streamReader.readString();
    instance.factType = streamReader.readString();
    instance.header = streamReader.readString();
    instance.operator = streamReader.readString();
    instance.valueList = streamReader.readString();
    
    org.drools.ide.common.client.modeldriven.dt.DTColumnConfig_FieldSerializer.deserialize(streamReader, instance);
  }
  
  public static org.drools.ide.common.client.modeldriven.dt.ConditionCol instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new org.drools.ide.common.client.modeldriven.dt.ConditionCol();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.ide.common.client.modeldriven.dt.ConditionCol instance) throws SerializationException {
    streamWriter.writeString(instance.boundName);
    streamWriter.writeInt(instance.constraintValueType);
    streamWriter.writeString(instance.factField);
    streamWriter.writeString(instance.factType);
    streamWriter.writeString(instance.header);
    streamWriter.writeString(instance.operator);
    streamWriter.writeString(instance.valueList);
    
    org.drools.ide.common.client.modeldriven.dt.DTColumnConfig_FieldSerializer.serialize(streamWriter, instance);
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.ide.common.client.modeldriven.dt.ConditionCol_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.ide.common.client.modeldriven.dt.ConditionCol_FieldSerializer.deserialize(reader, (org.drools.ide.common.client.modeldriven.dt.ConditionCol)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.ide.common.client.modeldriven.dt.ConditionCol_FieldSerializer.serialize(writer, (org.drools.ide.common.client.modeldriven.dt.ConditionCol)object);
  }
  
}
