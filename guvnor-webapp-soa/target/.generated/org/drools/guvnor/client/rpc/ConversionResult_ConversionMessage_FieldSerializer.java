package org.drools.guvnor.client.rpc;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class ConversionResult_ConversionMessage_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native java.lang.String getMessage(org.drools.guvnor.client.rpc.ConversionResult.ConversionMessage instance) /*-{
    return instance.@org.drools.guvnor.client.rpc.ConversionResult$ConversionMessage::message;
  }-*/;
  
  private static native void setMessage(org.drools.guvnor.client.rpc.ConversionResult.ConversionMessage instance, java.lang.String value) 
  /*-{
    instance.@org.drools.guvnor.client.rpc.ConversionResult$ConversionMessage::message = value;
  }-*/;
  
  private static native org.drools.guvnor.client.rpc.ConversionResult.ConversionMessageType getMessageType(org.drools.guvnor.client.rpc.ConversionResult.ConversionMessage instance) /*-{
    return instance.@org.drools.guvnor.client.rpc.ConversionResult$ConversionMessage::messageType;
  }-*/;
  
  private static native void setMessageType(org.drools.guvnor.client.rpc.ConversionResult.ConversionMessage instance, org.drools.guvnor.client.rpc.ConversionResult.ConversionMessageType value) 
  /*-{
    instance.@org.drools.guvnor.client.rpc.ConversionResult$ConversionMessage::messageType = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, org.drools.guvnor.client.rpc.ConversionResult.ConversionMessage instance) throws SerializationException {
    setMessage(instance, streamReader.readString());
    setMessageType(instance, (org.drools.guvnor.client.rpc.ConversionResult.ConversionMessageType) streamReader.readObject());
    
  }
  
  public static org.drools.guvnor.client.rpc.ConversionResult.ConversionMessage instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new org.drools.guvnor.client.rpc.ConversionResult.ConversionMessage();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.guvnor.client.rpc.ConversionResult.ConversionMessage instance) throws SerializationException {
    streamWriter.writeString(getMessage(instance));
    streamWriter.writeObject(getMessageType(instance));
    
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.guvnor.client.rpc.ConversionResult_ConversionMessage_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.guvnor.client.rpc.ConversionResult_ConversionMessage_FieldSerializer.deserialize(reader, (org.drools.guvnor.client.rpc.ConversionResult.ConversionMessage)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.guvnor.client.rpc.ConversionResult_ConversionMessage_FieldSerializer.serialize(writer, (org.drools.guvnor.client.rpc.ConversionResult.ConversionMessage)object);
  }
  
}
