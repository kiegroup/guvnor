package org.drools.guvnor.client.asseteditor;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class PropertyHolder_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native java.lang.String getName(org.drools.guvnor.client.asseteditor.PropertyHolder instance) /*-{
    return instance.@org.drools.guvnor.client.asseteditor.PropertyHolder::name;
  }-*/;
  
  private static native void setName(org.drools.guvnor.client.asseteditor.PropertyHolder instance, java.lang.String value) 
  /*-{
    instance.@org.drools.guvnor.client.asseteditor.PropertyHolder::name = value;
  }-*/;
  
  private static native java.lang.String getValue(org.drools.guvnor.client.asseteditor.PropertyHolder instance) /*-{
    return instance.@org.drools.guvnor.client.asseteditor.PropertyHolder::value;
  }-*/;
  
  private static native void setValue(org.drools.guvnor.client.asseteditor.PropertyHolder instance, java.lang.String value) 
  /*-{
    instance.@org.drools.guvnor.client.asseteditor.PropertyHolder::value = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, org.drools.guvnor.client.asseteditor.PropertyHolder instance) throws SerializationException {
    setName(instance, streamReader.readString());
    setValue(instance, streamReader.readString());
    
  }
  
  public static org.drools.guvnor.client.asseteditor.PropertyHolder instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new org.drools.guvnor.client.asseteditor.PropertyHolder();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.guvnor.client.asseteditor.PropertyHolder instance) throws SerializationException {
    streamWriter.writeString(getName(instance));
    streamWriter.writeString(getValue(instance));
    
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.guvnor.client.asseteditor.PropertyHolder_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.guvnor.client.asseteditor.PropertyHolder_FieldSerializer.deserialize(reader, (org.drools.guvnor.client.asseteditor.PropertyHolder)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.guvnor.client.asseteditor.PropertyHolder_FieldSerializer.serialize(writer, (org.drools.guvnor.client.asseteditor.PropertyHolder)object);
  }
  
}
