package org.drools.guvnor.client.rpc;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class ConversionResult_ConversionAsset_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native java.lang.String getFormat(org.drools.guvnor.client.rpc.ConversionResult.ConversionAsset instance) /*-{
    return instance.@org.drools.guvnor.client.rpc.ConversionResult$ConversionAsset::format;
  }-*/;
  
  private static native void setFormat(org.drools.guvnor.client.rpc.ConversionResult.ConversionAsset instance, java.lang.String value) 
  /*-{
    instance.@org.drools.guvnor.client.rpc.ConversionResult$ConversionAsset::format = value;
  }-*/;
  
  private static native java.lang.String getUuid(org.drools.guvnor.client.rpc.ConversionResult.ConversionAsset instance) /*-{
    return instance.@org.drools.guvnor.client.rpc.ConversionResult$ConversionAsset::uuid;
  }-*/;
  
  private static native void setUuid(org.drools.guvnor.client.rpc.ConversionResult.ConversionAsset instance, java.lang.String value) 
  /*-{
    instance.@org.drools.guvnor.client.rpc.ConversionResult$ConversionAsset::uuid = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, org.drools.guvnor.client.rpc.ConversionResult.ConversionAsset instance) throws SerializationException {
    setFormat(instance, streamReader.readString());
    setUuid(instance, streamReader.readString());
    
  }
  
  public static org.drools.guvnor.client.rpc.ConversionResult.ConversionAsset instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new org.drools.guvnor.client.rpc.ConversionResult.ConversionAsset();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.guvnor.client.rpc.ConversionResult.ConversionAsset instance) throws SerializationException {
    streamWriter.writeString(getFormat(instance));
    streamWriter.writeString(getUuid(instance));
    
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.guvnor.client.rpc.ConversionResult_ConversionAsset_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.guvnor.client.rpc.ConversionResult_ConversionAsset_FieldSerializer.deserialize(reader, (org.drools.guvnor.client.rpc.ConversionResult.ConversionAsset)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.guvnor.client.rpc.ConversionResult_ConversionAsset_FieldSerializer.serialize(writer, (org.drools.guvnor.client.rpc.ConversionResult.ConversionAsset)object);
  }
  
}
