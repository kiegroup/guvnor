package org.drools.guvnor.client.rpc;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class NewAssetConfiguration_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native java.lang.String getAssetName(org.drools.guvnor.client.rpc.NewAssetConfiguration instance) /*-{
    return instance.@org.drools.guvnor.client.rpc.NewAssetConfiguration::assetName;
  }-*/;
  
  private static native void setAssetName(org.drools.guvnor.client.rpc.NewAssetConfiguration instance, java.lang.String value) 
  /*-{
    instance.@org.drools.guvnor.client.rpc.NewAssetConfiguration::assetName = value;
  }-*/;
  
  private static native java.lang.String getDescription(org.drools.guvnor.client.rpc.NewAssetConfiguration instance) /*-{
    return instance.@org.drools.guvnor.client.rpc.NewAssetConfiguration::description;
  }-*/;
  
  private static native void setDescription(org.drools.guvnor.client.rpc.NewAssetConfiguration instance, java.lang.String value) 
  /*-{
    instance.@org.drools.guvnor.client.rpc.NewAssetConfiguration::description = value;
  }-*/;
  
  private static native java.lang.String getFormat(org.drools.guvnor.client.rpc.NewAssetConfiguration instance) /*-{
    return instance.@org.drools.guvnor.client.rpc.NewAssetConfiguration::format;
  }-*/;
  
  private static native void setFormat(org.drools.guvnor.client.rpc.NewAssetConfiguration instance, java.lang.String value) 
  /*-{
    instance.@org.drools.guvnor.client.rpc.NewAssetConfiguration::format = value;
  }-*/;
  
  private static native java.lang.String getInitialCategory(org.drools.guvnor.client.rpc.NewAssetConfiguration instance) /*-{
    return instance.@org.drools.guvnor.client.rpc.NewAssetConfiguration::initialCategory;
  }-*/;
  
  private static native void setInitialCategory(org.drools.guvnor.client.rpc.NewAssetConfiguration instance, java.lang.String value) 
  /*-{
    instance.@org.drools.guvnor.client.rpc.NewAssetConfiguration::initialCategory = value;
  }-*/;
  
  private static native java.lang.String getPackageName(org.drools.guvnor.client.rpc.NewAssetConfiguration instance) /*-{
    return instance.@org.drools.guvnor.client.rpc.NewAssetConfiguration::packageName;
  }-*/;
  
  private static native void setPackageName(org.drools.guvnor.client.rpc.NewAssetConfiguration instance, java.lang.String value) 
  /*-{
    instance.@org.drools.guvnor.client.rpc.NewAssetConfiguration::packageName = value;
  }-*/;
  
  private static native java.lang.String getPackageUUID(org.drools.guvnor.client.rpc.NewAssetConfiguration instance) /*-{
    return instance.@org.drools.guvnor.client.rpc.NewAssetConfiguration::packageUUID;
  }-*/;
  
  private static native void setPackageUUID(org.drools.guvnor.client.rpc.NewAssetConfiguration instance, java.lang.String value) 
  /*-{
    instance.@org.drools.guvnor.client.rpc.NewAssetConfiguration::packageUUID = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, org.drools.guvnor.client.rpc.NewAssetConfiguration instance) throws SerializationException {
    setAssetName(instance, streamReader.readString());
    setDescription(instance, streamReader.readString());
    setFormat(instance, streamReader.readString());
    setInitialCategory(instance, streamReader.readString());
    setPackageName(instance, streamReader.readString());
    setPackageUUID(instance, streamReader.readString());
    
  }
  
  public static org.drools.guvnor.client.rpc.NewAssetConfiguration instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new org.drools.guvnor.client.rpc.NewAssetConfiguration();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.guvnor.client.rpc.NewAssetConfiguration instance) throws SerializationException {
    streamWriter.writeString(getAssetName(instance));
    streamWriter.writeString(getDescription(instance));
    streamWriter.writeString(getFormat(instance));
    streamWriter.writeString(getInitialCategory(instance));
    streamWriter.writeString(getPackageName(instance));
    streamWriter.writeString(getPackageUUID(instance));
    
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.guvnor.client.rpc.NewAssetConfiguration_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.guvnor.client.rpc.NewAssetConfiguration_FieldSerializer.deserialize(reader, (org.drools.guvnor.client.rpc.NewAssetConfiguration)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.guvnor.client.rpc.NewAssetConfiguration_FieldSerializer.serialize(writer, (org.drools.guvnor.client.rpc.NewAssetConfiguration)object);
  }
  
}
