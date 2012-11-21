package org.drools.guvnor.client.asseteditor.drools.serviceconfig;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class AssetReference_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native java.lang.String getFormat(org.drools.guvnor.client.asseteditor.drools.serviceconfig.AssetReference instance) /*-{
    return instance.@org.drools.guvnor.client.asseteditor.drools.serviceconfig.AssetReference::format;
  }-*/;
  
  private static native void setFormat(org.drools.guvnor.client.asseteditor.drools.serviceconfig.AssetReference instance, java.lang.String value) 
  /*-{
    instance.@org.drools.guvnor.client.asseteditor.drools.serviceconfig.AssetReference::format = value;
  }-*/;
  
  private static native java.lang.String getName(org.drools.guvnor.client.asseteditor.drools.serviceconfig.AssetReference instance) /*-{
    return instance.@org.drools.guvnor.client.asseteditor.drools.serviceconfig.AssetReference::name;
  }-*/;
  
  private static native void setName(org.drools.guvnor.client.asseteditor.drools.serviceconfig.AssetReference instance, java.lang.String value) 
  /*-{
    instance.@org.drools.guvnor.client.asseteditor.drools.serviceconfig.AssetReference::name = value;
  }-*/;
  
  private static native java.lang.String getPackageRef(org.drools.guvnor.client.asseteditor.drools.serviceconfig.AssetReference instance) /*-{
    return instance.@org.drools.guvnor.client.asseteditor.drools.serviceconfig.AssetReference::packageRef;
  }-*/;
  
  private static native void setPackageRef(org.drools.guvnor.client.asseteditor.drools.serviceconfig.AssetReference instance, java.lang.String value) 
  /*-{
    instance.@org.drools.guvnor.client.asseteditor.drools.serviceconfig.AssetReference::packageRef = value;
  }-*/;
  
  private static native java.lang.String getUrl(org.drools.guvnor.client.asseteditor.drools.serviceconfig.AssetReference instance) /*-{
    return instance.@org.drools.guvnor.client.asseteditor.drools.serviceconfig.AssetReference::url;
  }-*/;
  
  private static native void setUrl(org.drools.guvnor.client.asseteditor.drools.serviceconfig.AssetReference instance, java.lang.String value) 
  /*-{
    instance.@org.drools.guvnor.client.asseteditor.drools.serviceconfig.AssetReference::url = value;
  }-*/;
  
  private static native java.lang.String getUuid(org.drools.guvnor.client.asseteditor.drools.serviceconfig.AssetReference instance) /*-{
    return instance.@org.drools.guvnor.client.asseteditor.drools.serviceconfig.AssetReference::uuid;
  }-*/;
  
  private static native void setUuid(org.drools.guvnor.client.asseteditor.drools.serviceconfig.AssetReference instance, java.lang.String value) 
  /*-{
    instance.@org.drools.guvnor.client.asseteditor.drools.serviceconfig.AssetReference::uuid = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, org.drools.guvnor.client.asseteditor.drools.serviceconfig.AssetReference instance) throws SerializationException {
    setFormat(instance, streamReader.readString());
    setName(instance, streamReader.readString());
    setPackageRef(instance, streamReader.readString());
    setUrl(instance, streamReader.readString());
    setUuid(instance, streamReader.readString());
    
  }
  
  public static org.drools.guvnor.client.asseteditor.drools.serviceconfig.AssetReference instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new org.drools.guvnor.client.asseteditor.drools.serviceconfig.AssetReference();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.guvnor.client.asseteditor.drools.serviceconfig.AssetReference instance) throws SerializationException {
    streamWriter.writeString(getFormat(instance));
    streamWriter.writeString(getName(instance));
    streamWriter.writeString(getPackageRef(instance));
    streamWriter.writeString(getUrl(instance));
    streamWriter.writeString(getUuid(instance));
    
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.guvnor.client.asseteditor.drools.serviceconfig.AssetReference_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.guvnor.client.asseteditor.drools.serviceconfig.AssetReference_FieldSerializer.deserialize(reader, (org.drools.guvnor.client.asseteditor.drools.serviceconfig.AssetReference)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.guvnor.client.asseteditor.drools.serviceconfig.AssetReference_FieldSerializer.serialize(writer, (org.drools.guvnor.client.asseteditor.drools.serviceconfig.AssetReference)object);
  }
  
}
