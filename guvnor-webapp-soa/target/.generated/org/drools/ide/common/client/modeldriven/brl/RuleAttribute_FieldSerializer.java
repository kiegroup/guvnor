package org.drools.ide.common.client.modeldriven.brl;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class RuleAttribute_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  public static void deserialize(SerializationStreamReader streamReader, org.drools.ide.common.client.modeldriven.brl.RuleAttribute instance) throws SerializationException {
    instance.attributeName = streamReader.readString();
    instance.value = streamReader.readString();
    
  }
  
  public static org.drools.ide.common.client.modeldriven.brl.RuleAttribute instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new org.drools.ide.common.client.modeldriven.brl.RuleAttribute();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.ide.common.client.modeldriven.brl.RuleAttribute instance) throws SerializationException {
    streamWriter.writeString(instance.attributeName);
    streamWriter.writeString(instance.value);
    
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.ide.common.client.modeldriven.brl.RuleAttribute_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.ide.common.client.modeldriven.brl.RuleAttribute_FieldSerializer.deserialize(reader, (org.drools.ide.common.client.modeldriven.brl.RuleAttribute)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.ide.common.client.modeldriven.brl.RuleAttribute_FieldSerializer.serialize(writer, (org.drools.ide.common.client.modeldriven.brl.RuleAttribute)object);
  }
  
}
