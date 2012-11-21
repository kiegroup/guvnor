package org.drools.ide.common.client.modeldriven.brl;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class ActionCallMethod_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  public static void deserialize(SerializationStreamReader streamReader, org.drools.ide.common.client.modeldriven.brl.ActionCallMethod instance) throws SerializationException {
    instance.methodName = streamReader.readString();
    instance.state = streamReader.readInt();
    
    org.drools.ide.common.client.modeldriven.brl.ActionSetField_FieldSerializer.deserialize(streamReader, instance);
  }
  
  public static org.drools.ide.common.client.modeldriven.brl.ActionCallMethod instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new org.drools.ide.common.client.modeldriven.brl.ActionCallMethod();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.ide.common.client.modeldriven.brl.ActionCallMethod instance) throws SerializationException {
    streamWriter.writeString(instance.methodName);
    streamWriter.writeInt(instance.state);
    
    org.drools.ide.common.client.modeldriven.brl.ActionSetField_FieldSerializer.serialize(streamWriter, instance);
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.ide.common.client.modeldriven.brl.ActionCallMethod_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.ide.common.client.modeldriven.brl.ActionCallMethod_FieldSerializer.deserialize(reader, (org.drools.ide.common.client.modeldriven.brl.ActionCallMethod)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.ide.common.client.modeldriven.brl.ActionCallMethod_FieldSerializer.serialize(writer, (org.drools.ide.common.client.modeldriven.brl.ActionCallMethod)object);
  }
  
}
