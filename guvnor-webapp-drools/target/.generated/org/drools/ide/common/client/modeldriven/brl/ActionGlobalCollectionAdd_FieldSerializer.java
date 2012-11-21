package org.drools.ide.common.client.modeldriven.brl;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class ActionGlobalCollectionAdd_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  public static void deserialize(SerializationStreamReader streamReader, org.drools.ide.common.client.modeldriven.brl.ActionGlobalCollectionAdd instance) throws SerializationException {
    instance.factName = streamReader.readString();
    instance.globalName = streamReader.readString();
    
  }
  
  public static org.drools.ide.common.client.modeldriven.brl.ActionGlobalCollectionAdd instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new org.drools.ide.common.client.modeldriven.brl.ActionGlobalCollectionAdd();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.ide.common.client.modeldriven.brl.ActionGlobalCollectionAdd instance) throws SerializationException {
    streamWriter.writeString(instance.factName);
    streamWriter.writeString(instance.globalName);
    
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.ide.common.client.modeldriven.brl.ActionGlobalCollectionAdd_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.ide.common.client.modeldriven.brl.ActionGlobalCollectionAdd_FieldSerializer.deserialize(reader, (org.drools.ide.common.client.modeldriven.brl.ActionGlobalCollectionAdd)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.ide.common.client.modeldriven.brl.ActionGlobalCollectionAdd_FieldSerializer.serialize(writer, (org.drools.ide.common.client.modeldriven.brl.ActionGlobalCollectionAdd)object);
  }
  
}
