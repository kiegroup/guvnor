package org.drools.ide.common.client.modeldriven.brl;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class ActionFieldValue_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  public static void deserialize(SerializationStreamReader streamReader, org.drools.ide.common.client.modeldriven.brl.ActionFieldValue instance) throws SerializationException {
    instance.field = streamReader.readString();
    instance.nature = streamReader.readLong();
    instance.type = streamReader.readString();
    instance.value = streamReader.readString();
    
  }
  
  public static org.drools.ide.common.client.modeldriven.brl.ActionFieldValue instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new org.drools.ide.common.client.modeldriven.brl.ActionFieldValue();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.ide.common.client.modeldriven.brl.ActionFieldValue instance) throws SerializationException {
    streamWriter.writeString(instance.field);
    streamWriter.writeLong(instance.nature);
    streamWriter.writeString(instance.type);
    streamWriter.writeString(instance.value);
    
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.ide.common.client.modeldriven.brl.ActionFieldValue_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.ide.common.client.modeldriven.brl.ActionFieldValue_FieldSerializer.deserialize(reader, (org.drools.ide.common.client.modeldriven.brl.ActionFieldValue)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.ide.common.client.modeldriven.brl.ActionFieldValue_FieldSerializer.serialize(writer, (org.drools.ide.common.client.modeldriven.brl.ActionFieldValue)object);
  }
  
}
