package org.drools.guvnor.client.rpc;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class QueryPageRow_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native java.lang.String getAbbreviatedDescription(org.drools.guvnor.client.rpc.QueryPageRow instance) /*-{
    return instance.@org.drools.guvnor.client.rpc.QueryPageRow::abbreviatedDescription;
  }-*/;
  
  private static native void setAbbreviatedDescription(org.drools.guvnor.client.rpc.QueryPageRow instance, java.lang.String value) 
  /*-{
    instance.@org.drools.guvnor.client.rpc.QueryPageRow::abbreviatedDescription = value;
  }-*/;
  
  private static native java.util.Date getCreatedDate(org.drools.guvnor.client.rpc.QueryPageRow instance) /*-{
    return instance.@org.drools.guvnor.client.rpc.QueryPageRow::createdDate;
  }-*/;
  
  private static native void setCreatedDate(org.drools.guvnor.client.rpc.QueryPageRow instance, java.util.Date value) 
  /*-{
    instance.@org.drools.guvnor.client.rpc.QueryPageRow::createdDate = value;
  }-*/;
  
  private static native java.lang.String getCreator(org.drools.guvnor.client.rpc.QueryPageRow instance) /*-{
    return instance.@org.drools.guvnor.client.rpc.QueryPageRow::creator;
  }-*/;
  
  private static native void setCreator(org.drools.guvnor.client.rpc.QueryPageRow instance, java.lang.String value) 
  /*-{
    instance.@org.drools.guvnor.client.rpc.QueryPageRow::creator = value;
  }-*/;
  
  private static native java.lang.String getDescription(org.drools.guvnor.client.rpc.QueryPageRow instance) /*-{
    return instance.@org.drools.guvnor.client.rpc.QueryPageRow::description;
  }-*/;
  
  private static native void setDescription(org.drools.guvnor.client.rpc.QueryPageRow instance, java.lang.String value) 
  /*-{
    instance.@org.drools.guvnor.client.rpc.QueryPageRow::description = value;
  }-*/;
  
  private static native java.lang.String getLastContributor(org.drools.guvnor.client.rpc.QueryPageRow instance) /*-{
    return instance.@org.drools.guvnor.client.rpc.QueryPageRow::lastContributor;
  }-*/;
  
  private static native void setLastContributor(org.drools.guvnor.client.rpc.QueryPageRow instance, java.lang.String value) 
  /*-{
    instance.@org.drools.guvnor.client.rpc.QueryPageRow::lastContributor = value;
  }-*/;
  
  private static native java.util.Date getLastModified(org.drools.guvnor.client.rpc.QueryPageRow instance) /*-{
    return instance.@org.drools.guvnor.client.rpc.QueryPageRow::lastModified;
  }-*/;
  
  private static native void setLastModified(org.drools.guvnor.client.rpc.QueryPageRow instance, java.util.Date value) 
  /*-{
    instance.@org.drools.guvnor.client.rpc.QueryPageRow::lastModified = value;
  }-*/;
  
  private static native java.lang.String getPackageName(org.drools.guvnor.client.rpc.QueryPageRow instance) /*-{
    return instance.@org.drools.guvnor.client.rpc.QueryPageRow::packageName;
  }-*/;
  
  private static native void setPackageName(org.drools.guvnor.client.rpc.QueryPageRow instance, java.lang.String value) 
  /*-{
    instance.@org.drools.guvnor.client.rpc.QueryPageRow::packageName = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, org.drools.guvnor.client.rpc.QueryPageRow instance) throws SerializationException {
    setAbbreviatedDescription(instance, streamReader.readString());
    setCreatedDate(instance, (java.util.Date) streamReader.readObject());
    setCreator(instance, streamReader.readString());
    setDescription(instance, streamReader.readString());
    setLastContributor(instance, streamReader.readString());
    setLastModified(instance, (java.util.Date) streamReader.readObject());
    setPackageName(instance, streamReader.readString());
    
    org.drools.guvnor.client.rpc.AbstractAssetPageRow_FieldSerializer.deserialize(streamReader, instance);
  }
  
  public static org.drools.guvnor.client.rpc.QueryPageRow instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new org.drools.guvnor.client.rpc.QueryPageRow();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.guvnor.client.rpc.QueryPageRow instance) throws SerializationException {
    streamWriter.writeString(getAbbreviatedDescription(instance));
    streamWriter.writeObject(getCreatedDate(instance));
    streamWriter.writeString(getCreator(instance));
    streamWriter.writeString(getDescription(instance));
    streamWriter.writeString(getLastContributor(instance));
    streamWriter.writeObject(getLastModified(instance));
    streamWriter.writeString(getPackageName(instance));
    
    org.drools.guvnor.client.rpc.AbstractAssetPageRow_FieldSerializer.serialize(streamWriter, instance);
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.guvnor.client.rpc.QueryPageRow_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.guvnor.client.rpc.QueryPageRow_FieldSerializer.deserialize(reader, (org.drools.guvnor.client.rpc.QueryPageRow)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.guvnor.client.rpc.QueryPageRow_FieldSerializer.serialize(writer, (org.drools.guvnor.client.rpc.QueryPageRow)object);
  }
  
}
