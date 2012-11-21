package org.drools.guvnor.client.rpc;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class CategoryPageRow_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native java.lang.String getAbbreviatedDescription(org.drools.guvnor.client.rpc.CategoryPageRow instance) /*-{
    return instance.@org.drools.guvnor.client.rpc.CategoryPageRow::abbreviatedDescription;
  }-*/;
  
  private static native void setAbbreviatedDescription(org.drools.guvnor.client.rpc.CategoryPageRow instance, java.lang.String value) 
  /*-{
    instance.@org.drools.guvnor.client.rpc.CategoryPageRow::abbreviatedDescription = value;
  }-*/;
  
  private static native java.lang.String getDescription(org.drools.guvnor.client.rpc.CategoryPageRow instance) /*-{
    return instance.@org.drools.guvnor.client.rpc.CategoryPageRow::description;
  }-*/;
  
  private static native void setDescription(org.drools.guvnor.client.rpc.CategoryPageRow instance, java.lang.String value) 
  /*-{
    instance.@org.drools.guvnor.client.rpc.CategoryPageRow::description = value;
  }-*/;
  
  private static native java.util.Date getLastModified(org.drools.guvnor.client.rpc.CategoryPageRow instance) /*-{
    return instance.@org.drools.guvnor.client.rpc.CategoryPageRow::lastModified;
  }-*/;
  
  private static native void setLastModified(org.drools.guvnor.client.rpc.CategoryPageRow instance, java.util.Date value) 
  /*-{
    instance.@org.drools.guvnor.client.rpc.CategoryPageRow::lastModified = value;
  }-*/;
  
  private static native java.lang.String getPackageName(org.drools.guvnor.client.rpc.CategoryPageRow instance) /*-{
    return instance.@org.drools.guvnor.client.rpc.CategoryPageRow::packageName;
  }-*/;
  
  private static native void setPackageName(org.drools.guvnor.client.rpc.CategoryPageRow instance, java.lang.String value) 
  /*-{
    instance.@org.drools.guvnor.client.rpc.CategoryPageRow::packageName = value;
  }-*/;
  
  private static native java.lang.String getStateName(org.drools.guvnor.client.rpc.CategoryPageRow instance) /*-{
    return instance.@org.drools.guvnor.client.rpc.CategoryPageRow::stateName;
  }-*/;
  
  private static native void setStateName(org.drools.guvnor.client.rpc.CategoryPageRow instance, java.lang.String value) 
  /*-{
    instance.@org.drools.guvnor.client.rpc.CategoryPageRow::stateName = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, org.drools.guvnor.client.rpc.CategoryPageRow instance) throws SerializationException {
    setAbbreviatedDescription(instance, streamReader.readString());
    setDescription(instance, streamReader.readString());
    setLastModified(instance, (java.util.Date) streamReader.readObject());
    setPackageName(instance, streamReader.readString());
    setStateName(instance, streamReader.readString());
    
    org.drools.guvnor.client.rpc.AbstractAssetPageRow_FieldSerializer.deserialize(streamReader, instance);
  }
  
  public static org.drools.guvnor.client.rpc.CategoryPageRow instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new org.drools.guvnor.client.rpc.CategoryPageRow();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.guvnor.client.rpc.CategoryPageRow instance) throws SerializationException {
    streamWriter.writeString(getAbbreviatedDescription(instance));
    streamWriter.writeString(getDescription(instance));
    streamWriter.writeObject(getLastModified(instance));
    streamWriter.writeString(getPackageName(instance));
    streamWriter.writeString(getStateName(instance));
    
    org.drools.guvnor.client.rpc.AbstractAssetPageRow_FieldSerializer.serialize(streamWriter, instance);
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.guvnor.client.rpc.CategoryPageRow_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.guvnor.client.rpc.CategoryPageRow_FieldSerializer.deserialize(reader, (org.drools.guvnor.client.rpc.CategoryPageRow)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.guvnor.client.rpc.CategoryPageRow_FieldSerializer.serialize(writer, (org.drools.guvnor.client.rpc.CategoryPageRow)object);
  }
  
}
