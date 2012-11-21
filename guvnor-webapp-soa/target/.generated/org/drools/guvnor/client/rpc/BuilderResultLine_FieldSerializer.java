package org.drools.guvnor.client.rpc;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class BuilderResultLine_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native java.lang.String getAssetFormat(org.drools.guvnor.client.rpc.BuilderResultLine instance) /*-{
    return instance.@org.drools.guvnor.client.rpc.BuilderResultLine::assetFormat;
  }-*/;
  
  private static native void setAssetFormat(org.drools.guvnor.client.rpc.BuilderResultLine instance, java.lang.String value) 
  /*-{
    instance.@org.drools.guvnor.client.rpc.BuilderResultLine::assetFormat = value;
  }-*/;
  
  private static native java.lang.String getAssetName(org.drools.guvnor.client.rpc.BuilderResultLine instance) /*-{
    return instance.@org.drools.guvnor.client.rpc.BuilderResultLine::assetName;
  }-*/;
  
  private static native void setAssetName(org.drools.guvnor.client.rpc.BuilderResultLine instance, java.lang.String value) 
  /*-{
    instance.@org.drools.guvnor.client.rpc.BuilderResultLine::assetName = value;
  }-*/;
  
  private static native java.lang.String getMessage(org.drools.guvnor.client.rpc.BuilderResultLine instance) /*-{
    return instance.@org.drools.guvnor.client.rpc.BuilderResultLine::message;
  }-*/;
  
  private static native void setMessage(org.drools.guvnor.client.rpc.BuilderResultLine instance, java.lang.String value) 
  /*-{
    instance.@org.drools.guvnor.client.rpc.BuilderResultLine::message = value;
  }-*/;
  
  private static native java.lang.String getUuid(org.drools.guvnor.client.rpc.BuilderResultLine instance) /*-{
    return instance.@org.drools.guvnor.client.rpc.BuilderResultLine::uuid;
  }-*/;
  
  private static native void setUuid(org.drools.guvnor.client.rpc.BuilderResultLine instance, java.lang.String value) 
  /*-{
    instance.@org.drools.guvnor.client.rpc.BuilderResultLine::uuid = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, org.drools.guvnor.client.rpc.BuilderResultLine instance) throws SerializationException {
    setAssetFormat(instance, streamReader.readString());
    setAssetName(instance, streamReader.readString());
    setMessage(instance, streamReader.readString());
    setUuid(instance, streamReader.readString());
    
    org.drools.guvnor.client.rpc.AbstractPageRow_FieldSerializer.deserialize(streamReader, instance);
  }
  
  public static org.drools.guvnor.client.rpc.BuilderResultLine instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new org.drools.guvnor.client.rpc.BuilderResultLine();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.guvnor.client.rpc.BuilderResultLine instance) throws SerializationException {
    streamWriter.writeString(getAssetFormat(instance));
    streamWriter.writeString(getAssetName(instance));
    streamWriter.writeString(getMessage(instance));
    streamWriter.writeString(getUuid(instance));
    
    org.drools.guvnor.client.rpc.AbstractPageRow_FieldSerializer.serialize(streamWriter, instance);
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.guvnor.client.rpc.BuilderResultLine_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.guvnor.client.rpc.BuilderResultLine_FieldSerializer.deserialize(reader, (org.drools.guvnor.client.rpc.BuilderResultLine)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.guvnor.client.rpc.BuilderResultLine_FieldSerializer.serialize(writer, (org.drools.guvnor.client.rpc.BuilderResultLine)object);
  }
  
}
