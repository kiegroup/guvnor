package org.drools.guvnor.client.rpc;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class ConversionResult_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native java.util.List getMessages(org.drools.guvnor.client.rpc.ConversionResult instance) /*-{
    return instance.@org.drools.guvnor.client.rpc.ConversionResult::messages;
  }-*/;
  
  private static native void setMessages(org.drools.guvnor.client.rpc.ConversionResult instance, java.util.List value) 
  /*-{
    instance.@org.drools.guvnor.client.rpc.ConversionResult::messages = value;
  }-*/;
  
  private static native java.util.List getNewAssets(org.drools.guvnor.client.rpc.ConversionResult instance) /*-{
    return instance.@org.drools.guvnor.client.rpc.ConversionResult::newAssets;
  }-*/;
  
  private static native void setNewAssets(org.drools.guvnor.client.rpc.ConversionResult instance, java.util.List value) 
  /*-{
    instance.@org.drools.guvnor.client.rpc.ConversionResult::newAssets = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, org.drools.guvnor.client.rpc.ConversionResult instance) throws SerializationException {
    setMessages(instance, (java.util.List) streamReader.readObject());
    setNewAssets(instance, (java.util.List) streamReader.readObject());
    
  }
  
  public static org.drools.guvnor.client.rpc.ConversionResult instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new org.drools.guvnor.client.rpc.ConversionResult();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.guvnor.client.rpc.ConversionResult instance) throws SerializationException {
    streamWriter.writeObject(getMessages(instance));
    streamWriter.writeObject(getNewAssets(instance));
    
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.guvnor.client.rpc.ConversionResult_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.guvnor.client.rpc.ConversionResult_FieldSerializer.deserialize(reader, (org.drools.guvnor.client.rpc.ConversionResult)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.guvnor.client.rpc.ConversionResult_FieldSerializer.serialize(writer, (org.drools.guvnor.client.rpc.ConversionResult)object);
  }
  
}
