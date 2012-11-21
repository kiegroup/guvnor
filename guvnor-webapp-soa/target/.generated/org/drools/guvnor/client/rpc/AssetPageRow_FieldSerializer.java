package org.drools.guvnor.client.rpc;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class AssetPageRow_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native java.lang.String getAbbreviatedDescription(org.drools.guvnor.client.rpc.AssetPageRow instance) /*-{
    return instance.@org.drools.guvnor.client.rpc.AssetPageRow::abbreviatedDescription;
  }-*/;
  
  private static native void setAbbreviatedDescription(org.drools.guvnor.client.rpc.AssetPageRow instance, java.lang.String value) 
  /*-{
    instance.@org.drools.guvnor.client.rpc.AssetPageRow::abbreviatedDescription = value;
  }-*/;
  
  private static native java.lang.String getCategorySummary(org.drools.guvnor.client.rpc.AssetPageRow instance) /*-{
    return instance.@org.drools.guvnor.client.rpc.AssetPageRow::categorySummary;
  }-*/;
  
  private static native void setCategorySummary(org.drools.guvnor.client.rpc.AssetPageRow instance, java.lang.String value) 
  /*-{
    instance.@org.drools.guvnor.client.rpc.AssetPageRow::categorySummary = value;
  }-*/;
  
  private static native java.util.Date getCreatedDate(org.drools.guvnor.client.rpc.AssetPageRow instance) /*-{
    return instance.@org.drools.guvnor.client.rpc.AssetPageRow::createdDate;
  }-*/;
  
  private static native void setCreatedDate(org.drools.guvnor.client.rpc.AssetPageRow instance, java.util.Date value) 
  /*-{
    instance.@org.drools.guvnor.client.rpc.AssetPageRow::createdDate = value;
  }-*/;
  
  private static native java.lang.String getCreator(org.drools.guvnor.client.rpc.AssetPageRow instance) /*-{
    return instance.@org.drools.guvnor.client.rpc.AssetPageRow::creator;
  }-*/;
  
  private static native void setCreator(org.drools.guvnor.client.rpc.AssetPageRow instance, java.lang.String value) 
  /*-{
    instance.@org.drools.guvnor.client.rpc.AssetPageRow::creator = value;
  }-*/;
  
  private static native java.lang.String getDescription(org.drools.guvnor.client.rpc.AssetPageRow instance) /*-{
    return instance.@org.drools.guvnor.client.rpc.AssetPageRow::description;
  }-*/;
  
  private static native void setDescription(org.drools.guvnor.client.rpc.AssetPageRow instance, java.lang.String value) 
  /*-{
    instance.@org.drools.guvnor.client.rpc.AssetPageRow::description = value;
  }-*/;
  
  private static native java.lang.String getExternalSource(org.drools.guvnor.client.rpc.AssetPageRow instance) /*-{
    return instance.@org.drools.guvnor.client.rpc.AssetPageRow::externalSource;
  }-*/;
  
  private static native void setExternalSource(org.drools.guvnor.client.rpc.AssetPageRow instance, java.lang.String value) 
  /*-{
    instance.@org.drools.guvnor.client.rpc.AssetPageRow::externalSource = value;
  }-*/;
  
  private static native boolean getIsDisabled(org.drools.guvnor.client.rpc.AssetPageRow instance) /*-{
    return instance.@org.drools.guvnor.client.rpc.AssetPageRow::isDisabled;
  }-*/;
  
  private static native void setIsDisabled(org.drools.guvnor.client.rpc.AssetPageRow instance, boolean value) 
  /*-{
    instance.@org.drools.guvnor.client.rpc.AssetPageRow::isDisabled = value;
  }-*/;
  
  private static native java.lang.String getLastContributor(org.drools.guvnor.client.rpc.AssetPageRow instance) /*-{
    return instance.@org.drools.guvnor.client.rpc.AssetPageRow::lastContributor;
  }-*/;
  
  private static native void setLastContributor(org.drools.guvnor.client.rpc.AssetPageRow instance, java.lang.String value) 
  /*-{
    instance.@org.drools.guvnor.client.rpc.AssetPageRow::lastContributor = value;
  }-*/;
  
  private static native java.util.Date getLastModified(org.drools.guvnor.client.rpc.AssetPageRow instance) /*-{
    return instance.@org.drools.guvnor.client.rpc.AssetPageRow::lastModified;
  }-*/;
  
  private static native void setLastModified(org.drools.guvnor.client.rpc.AssetPageRow instance, java.util.Date value) 
  /*-{
    instance.@org.drools.guvnor.client.rpc.AssetPageRow::lastModified = value;
  }-*/;
  
  private static native java.lang.String getPackageName(org.drools.guvnor.client.rpc.AssetPageRow instance) /*-{
    return instance.@org.drools.guvnor.client.rpc.AssetPageRow::packageName;
  }-*/;
  
  private static native void setPackageName(org.drools.guvnor.client.rpc.AssetPageRow instance, java.lang.String value) 
  /*-{
    instance.@org.drools.guvnor.client.rpc.AssetPageRow::packageName = value;
  }-*/;
  
  private static native java.lang.String getStateName(org.drools.guvnor.client.rpc.AssetPageRow instance) /*-{
    return instance.@org.drools.guvnor.client.rpc.AssetPageRow::stateName;
  }-*/;
  
  private static native void setStateName(org.drools.guvnor.client.rpc.AssetPageRow instance, java.lang.String value) 
  /*-{
    instance.@org.drools.guvnor.client.rpc.AssetPageRow::stateName = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, org.drools.guvnor.client.rpc.AssetPageRow instance) throws SerializationException {
    setAbbreviatedDescription(instance, streamReader.readString());
    setCategorySummary(instance, streamReader.readString());
    setCreatedDate(instance, (java.util.Date) streamReader.readObject());
    setCreator(instance, streamReader.readString());
    setDescription(instance, streamReader.readString());
    setExternalSource(instance, streamReader.readString());
    setIsDisabled(instance, streamReader.readBoolean());
    setLastContributor(instance, streamReader.readString());
    setLastModified(instance, (java.util.Date) streamReader.readObject());
    setPackageName(instance, streamReader.readString());
    setStateName(instance, streamReader.readString());
    
    org.drools.guvnor.client.rpc.AbstractAssetPageRow_FieldSerializer.deserialize(streamReader, instance);
  }
  
  public static org.drools.guvnor.client.rpc.AssetPageRow instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new org.drools.guvnor.client.rpc.AssetPageRow();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.guvnor.client.rpc.AssetPageRow instance) throws SerializationException {
    streamWriter.writeString(getAbbreviatedDescription(instance));
    streamWriter.writeString(getCategorySummary(instance));
    streamWriter.writeObject(getCreatedDate(instance));
    streamWriter.writeString(getCreator(instance));
    streamWriter.writeString(getDescription(instance));
    streamWriter.writeString(getExternalSource(instance));
    streamWriter.writeBoolean(getIsDisabled(instance));
    streamWriter.writeString(getLastContributor(instance));
    streamWriter.writeObject(getLastModified(instance));
    streamWriter.writeString(getPackageName(instance));
    streamWriter.writeString(getStateName(instance));
    
    org.drools.guvnor.client.rpc.AbstractAssetPageRow_FieldSerializer.serialize(streamWriter, instance);
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.guvnor.client.rpc.AssetPageRow_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.guvnor.client.rpc.AssetPageRow_FieldSerializer.deserialize(reader, (org.drools.guvnor.client.rpc.AssetPageRow)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.guvnor.client.rpc.AssetPageRow_FieldSerializer.serialize(writer, (org.drools.guvnor.client.rpc.AssetPageRow)object);
  }
  
}
