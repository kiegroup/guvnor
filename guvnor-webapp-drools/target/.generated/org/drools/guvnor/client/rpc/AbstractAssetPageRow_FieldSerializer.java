package org.drools.guvnor.client.rpc;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class AbstractAssetPageRow_FieldSerializer {
  private static native java.lang.String getFormat(org.drools.guvnor.client.rpc.AbstractAssetPageRow instance) /*-{
    return instance.@org.drools.guvnor.client.rpc.AbstractAssetPageRow::format;
  }-*/;
  
  private static native void setFormat(org.drools.guvnor.client.rpc.AbstractAssetPageRow instance, java.lang.String value) 
  /*-{
    instance.@org.drools.guvnor.client.rpc.AbstractAssetPageRow::format = value;
  }-*/;
  
  private static native java.lang.String getName(org.drools.guvnor.client.rpc.AbstractAssetPageRow instance) /*-{
    return instance.@org.drools.guvnor.client.rpc.AbstractAssetPageRow::name;
  }-*/;
  
  private static native void setName(org.drools.guvnor.client.rpc.AbstractAssetPageRow instance, java.lang.String value) 
  /*-{
    instance.@org.drools.guvnor.client.rpc.AbstractAssetPageRow::name = value;
  }-*/;
  
  private static native java.lang.String getUuid(org.drools.guvnor.client.rpc.AbstractAssetPageRow instance) /*-{
    return instance.@org.drools.guvnor.client.rpc.AbstractAssetPageRow::uuid;
  }-*/;
  
  private static native void setUuid(org.drools.guvnor.client.rpc.AbstractAssetPageRow instance, java.lang.String value) 
  /*-{
    instance.@org.drools.guvnor.client.rpc.AbstractAssetPageRow::uuid = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, org.drools.guvnor.client.rpc.AbstractAssetPageRow instance) throws SerializationException {
    setFormat(instance, streamReader.readString());
    setName(instance, streamReader.readString());
    setUuid(instance, streamReader.readString());
    
    org.drools.guvnor.client.rpc.AbstractPageRow_FieldSerializer.deserialize(streamReader, instance);
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.guvnor.client.rpc.AbstractAssetPageRow instance) throws SerializationException {
    streamWriter.writeString(getFormat(instance));
    streamWriter.writeString(getName(instance));
    streamWriter.writeString(getUuid(instance));
    
    org.drools.guvnor.client.rpc.AbstractPageRow_FieldSerializer.serialize(streamWriter, instance);
  }
  
}
