package org.drools.ide.common.client.modeldriven.brl;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class ActionInsertLogicalFact_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  public static void deserialize(SerializationStreamReader streamReader, org.drools.ide.common.client.modeldriven.brl.ActionInsertLogicalFact instance) throws SerializationException {
    
    org.drools.ide.common.client.modeldriven.brl.ActionInsertFact_FieldSerializer.deserialize(streamReader, instance);
  }
  
  public static org.drools.ide.common.client.modeldriven.brl.ActionInsertLogicalFact instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new org.drools.ide.common.client.modeldriven.brl.ActionInsertLogicalFact();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.ide.common.client.modeldriven.brl.ActionInsertLogicalFact instance) throws SerializationException {
    
    org.drools.ide.common.client.modeldriven.brl.ActionInsertFact_FieldSerializer.serialize(streamWriter, instance);
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.ide.common.client.modeldriven.brl.ActionInsertLogicalFact_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.ide.common.client.modeldriven.brl.ActionInsertLogicalFact_FieldSerializer.deserialize(reader, (org.drools.ide.common.client.modeldriven.brl.ActionInsertLogicalFact)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.ide.common.client.modeldriven.brl.ActionInsertLogicalFact_FieldSerializer.serialize(writer, (org.drools.ide.common.client.modeldriven.brl.ActionInsertLogicalFact)object);
  }
  
}
