package org.drools.ide.common.client.modeldriven.brl;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class ActionFieldFunction_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  public static void deserialize(SerializationStreamReader streamReader, org.drools.ide.common.client.modeldriven.brl.ActionFieldFunction instance) throws SerializationException {
    instance.method = streamReader.readString();
    
    org.drools.ide.common.client.modeldriven.brl.ActionFieldValue_FieldSerializer.deserialize(streamReader, instance);
  }
  
  public static org.drools.ide.common.client.modeldriven.brl.ActionFieldFunction instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new org.drools.ide.common.client.modeldriven.brl.ActionFieldFunction();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.ide.common.client.modeldriven.brl.ActionFieldFunction instance) throws SerializationException {
    streamWriter.writeString(instance.method);
    
    org.drools.ide.common.client.modeldriven.brl.ActionFieldValue_FieldSerializer.serialize(streamWriter, instance);
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.ide.common.client.modeldriven.brl.ActionFieldFunction_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.ide.common.client.modeldriven.brl.ActionFieldFunction_FieldSerializer.deserialize(reader, (org.drools.ide.common.client.modeldriven.brl.ActionFieldFunction)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.ide.common.client.modeldriven.brl.ActionFieldFunction_FieldSerializer.serialize(writer, (org.drools.ide.common.client.modeldriven.brl.ActionFieldFunction)object);
  }
  
}
