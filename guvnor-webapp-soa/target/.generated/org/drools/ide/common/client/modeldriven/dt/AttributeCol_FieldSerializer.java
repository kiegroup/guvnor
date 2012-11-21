package org.drools.ide.common.client.modeldriven.dt;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class AttributeCol_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  public static void deserialize(SerializationStreamReader streamReader, org.drools.ide.common.client.modeldriven.dt.AttributeCol instance) throws SerializationException {
    instance.attr = streamReader.readString();
    
    org.drools.ide.common.client.modeldriven.dt.DTColumnConfig_FieldSerializer.deserialize(streamReader, instance);
  }
  
  public static org.drools.ide.common.client.modeldriven.dt.AttributeCol instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new org.drools.ide.common.client.modeldriven.dt.AttributeCol();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.ide.common.client.modeldriven.dt.AttributeCol instance) throws SerializationException {
    streamWriter.writeString(instance.attr);
    
    org.drools.ide.common.client.modeldriven.dt.DTColumnConfig_FieldSerializer.serialize(streamWriter, instance);
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.ide.common.client.modeldriven.dt.AttributeCol_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.ide.common.client.modeldriven.dt.AttributeCol_FieldSerializer.deserialize(reader, (org.drools.ide.common.client.modeldriven.dt.AttributeCol)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.ide.common.client.modeldriven.dt.AttributeCol_FieldSerializer.serialize(writer, (org.drools.ide.common.client.modeldriven.dt.AttributeCol)object);
  }
  
}
