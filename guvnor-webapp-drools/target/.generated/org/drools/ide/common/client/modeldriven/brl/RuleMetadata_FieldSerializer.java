package org.drools.ide.common.client.modeldriven.brl;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class RuleMetadata_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  public static void deserialize(SerializationStreamReader streamReader, org.drools.ide.common.client.modeldriven.brl.RuleMetadata instance) throws SerializationException {
    instance.attributeName = streamReader.readString();
    instance.value = streamReader.readString();
    
  }
  
  public static org.drools.ide.common.client.modeldriven.brl.RuleMetadata instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new org.drools.ide.common.client.modeldriven.brl.RuleMetadata();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.ide.common.client.modeldriven.brl.RuleMetadata instance) throws SerializationException {
    streamWriter.writeString(instance.attributeName);
    streamWriter.writeString(instance.value);
    
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.ide.common.client.modeldriven.brl.RuleMetadata_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.ide.common.client.modeldriven.brl.RuleMetadata_FieldSerializer.deserialize(reader, (org.drools.ide.common.client.modeldriven.brl.RuleMetadata)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.ide.common.client.modeldriven.brl.RuleMetadata_FieldSerializer.serialize(writer, (org.drools.ide.common.client.modeldriven.brl.RuleMetadata)object);
  }
  
}
