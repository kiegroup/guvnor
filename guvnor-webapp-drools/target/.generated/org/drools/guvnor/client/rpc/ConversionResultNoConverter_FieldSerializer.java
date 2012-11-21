package org.drools.guvnor.client.rpc;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class ConversionResultNoConverter_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  public static void deserialize(SerializationStreamReader streamReader, org.drools.guvnor.client.rpc.ConversionResultNoConverter instance) throws SerializationException {
    
    org.drools.guvnor.client.rpc.ConversionResult_FieldSerializer.deserialize(streamReader, instance);
  }
  
  public static org.drools.guvnor.client.rpc.ConversionResultNoConverter instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new org.drools.guvnor.client.rpc.ConversionResultNoConverter();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.guvnor.client.rpc.ConversionResultNoConverter instance) throws SerializationException {
    
    org.drools.guvnor.client.rpc.ConversionResult_FieldSerializer.serialize(streamWriter, instance);
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.guvnor.client.rpc.ConversionResultNoConverter_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.guvnor.client.rpc.ConversionResultNoConverter_FieldSerializer.deserialize(reader, (org.drools.guvnor.client.rpc.ConversionResultNoConverter)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.guvnor.client.rpc.ConversionResultNoConverter_FieldSerializer.serialize(writer, (org.drools.guvnor.client.rpc.ConversionResultNoConverter)object);
  }
  
}
