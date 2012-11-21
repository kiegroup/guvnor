package org.drools.ide.common.client.factconstraints.customform.predefined;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class DefaultCustomFormImplementation_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native java.lang.String getFactType(org.drools.ide.common.client.factconstraints.customform.predefined.DefaultCustomFormImplementation instance) /*-{
    return instance.@org.drools.ide.common.client.factconstraints.customform.predefined.DefaultCustomFormImplementation::factType;
  }-*/;
  
  private static native void setFactType(org.drools.ide.common.client.factconstraints.customform.predefined.DefaultCustomFormImplementation instance, java.lang.String value) 
  /*-{
    instance.@org.drools.ide.common.client.factconstraints.customform.predefined.DefaultCustomFormImplementation::factType = value;
  }-*/;
  
  private static native java.lang.String getFieldName(org.drools.ide.common.client.factconstraints.customform.predefined.DefaultCustomFormImplementation instance) /*-{
    return instance.@org.drools.ide.common.client.factconstraints.customform.predefined.DefaultCustomFormImplementation::fieldName;
  }-*/;
  
  private static native void setFieldName(org.drools.ide.common.client.factconstraints.customform.predefined.DefaultCustomFormImplementation instance, java.lang.String value) 
  /*-{
    instance.@org.drools.ide.common.client.factconstraints.customform.predefined.DefaultCustomFormImplementation::fieldName = value;
  }-*/;
  
  private static native int getHeight(org.drools.ide.common.client.factconstraints.customform.predefined.DefaultCustomFormImplementation instance) /*-{
    return instance.@org.drools.ide.common.client.factconstraints.customform.predefined.DefaultCustomFormImplementation::height;
  }-*/;
  
  private static native void setHeight(org.drools.ide.common.client.factconstraints.customform.predefined.DefaultCustomFormImplementation instance, int value) 
  /*-{
    instance.@org.drools.ide.common.client.factconstraints.customform.predefined.DefaultCustomFormImplementation::height = value;
  }-*/;
  
  private static native java.lang.String getUrl(org.drools.ide.common.client.factconstraints.customform.predefined.DefaultCustomFormImplementation instance) /*-{
    return instance.@org.drools.ide.common.client.factconstraints.customform.predefined.DefaultCustomFormImplementation::url;
  }-*/;
  
  private static native void setUrl(org.drools.ide.common.client.factconstraints.customform.predefined.DefaultCustomFormImplementation instance, java.lang.String value) 
  /*-{
    instance.@org.drools.ide.common.client.factconstraints.customform.predefined.DefaultCustomFormImplementation::url = value;
  }-*/;
  
  private static native int getWidth(org.drools.ide.common.client.factconstraints.customform.predefined.DefaultCustomFormImplementation instance) /*-{
    return instance.@org.drools.ide.common.client.factconstraints.customform.predefined.DefaultCustomFormImplementation::width;
  }-*/;
  
  private static native void setWidth(org.drools.ide.common.client.factconstraints.customform.predefined.DefaultCustomFormImplementation instance, int value) 
  /*-{
    instance.@org.drools.ide.common.client.factconstraints.customform.predefined.DefaultCustomFormImplementation::width = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, org.drools.ide.common.client.factconstraints.customform.predefined.DefaultCustomFormImplementation instance) throws SerializationException {
    setFactType(instance, streamReader.readString());
    setFieldName(instance, streamReader.readString());
    setHeight(instance, streamReader.readInt());
    setUrl(instance, streamReader.readString());
    setWidth(instance, streamReader.readInt());
    
  }
  
  public static org.drools.ide.common.client.factconstraints.customform.predefined.DefaultCustomFormImplementation instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new org.drools.ide.common.client.factconstraints.customform.predefined.DefaultCustomFormImplementation();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.ide.common.client.factconstraints.customform.predefined.DefaultCustomFormImplementation instance) throws SerializationException {
    streamWriter.writeString(getFactType(instance));
    streamWriter.writeString(getFieldName(instance));
    streamWriter.writeInt(getHeight(instance));
    streamWriter.writeString(getUrl(instance));
    streamWriter.writeInt(getWidth(instance));
    
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.ide.common.client.factconstraints.customform.predefined.DefaultCustomFormImplementation_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.ide.common.client.factconstraints.customform.predefined.DefaultCustomFormImplementation_FieldSerializer.deserialize(reader, (org.drools.ide.common.client.factconstraints.customform.predefined.DefaultCustomFormImplementation)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.ide.common.client.factconstraints.customform.predefined.DefaultCustomFormImplementation_FieldSerializer.serialize(writer, (org.drools.ide.common.client.factconstraints.customform.predefined.DefaultCustomFormImplementation)object);
  }
  
}
