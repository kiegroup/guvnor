package org.drools.guvnor.client.rpc;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class MetaDataQuery_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  public static void deserialize(SerializationStreamReader streamReader, org.drools.guvnor.client.rpc.MetaDataQuery instance) throws SerializationException {
    instance.attribute = streamReader.readString();
    instance.valueList = streamReader.readString();
    
  }
  
  public static org.drools.guvnor.client.rpc.MetaDataQuery instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new org.drools.guvnor.client.rpc.MetaDataQuery();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.guvnor.client.rpc.MetaDataQuery instance) throws SerializationException {
    streamWriter.writeString(instance.attribute);
    streamWriter.writeString(instance.valueList);
    
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.guvnor.client.rpc.MetaDataQuery_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.guvnor.client.rpc.MetaDataQuery_FieldSerializer.deserialize(reader, (org.drools.guvnor.client.rpc.MetaDataQuery)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.guvnor.client.rpc.MetaDataQuery_FieldSerializer.serialize(writer, (org.drools.guvnor.client.rpc.MetaDataQuery)object);
  }
  
}
