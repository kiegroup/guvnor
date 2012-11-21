package org.drools.ide.common.shared.workitems;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class PortableEnumParameterDefinition_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native java.lang.String getValue(org.drools.ide.common.shared.workitems.PortableEnumParameterDefinition instance) /*-{
    return instance.@org.drools.ide.common.shared.workitems.PortableEnumParameterDefinition::value;
  }-*/;
  
  private static native void setValue(org.drools.ide.common.shared.workitems.PortableEnumParameterDefinition instance, java.lang.String value) 
  /*-{
    instance.@org.drools.ide.common.shared.workitems.PortableEnumParameterDefinition::value = value;
  }-*/;
  
  private static native java.lang.String[] getValues(org.drools.ide.common.shared.workitems.PortableEnumParameterDefinition instance) /*-{
    return instance.@org.drools.ide.common.shared.workitems.PortableEnumParameterDefinition::values;
  }-*/;
  
  private static native void setValues(org.drools.ide.common.shared.workitems.PortableEnumParameterDefinition instance, java.lang.String[] value) 
  /*-{
    instance.@org.drools.ide.common.shared.workitems.PortableEnumParameterDefinition::values = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, org.drools.ide.common.shared.workitems.PortableEnumParameterDefinition instance) throws SerializationException {
    setValue(instance, streamReader.readString());
    setValues(instance, (java.lang.String[]) streamReader.readObject());
    
    org.drools.ide.common.shared.workitems.PortableObjectParameterDefinition_FieldSerializer.deserialize(streamReader, instance);
  }
  
  public static org.drools.ide.common.shared.workitems.PortableEnumParameterDefinition instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new org.drools.ide.common.shared.workitems.PortableEnumParameterDefinition();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.ide.common.shared.workitems.PortableEnumParameterDefinition instance) throws SerializationException {
    streamWriter.writeString(getValue(instance));
    streamWriter.writeObject(getValues(instance));
    
    org.drools.ide.common.shared.workitems.PortableObjectParameterDefinition_FieldSerializer.serialize(streamWriter, instance);
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.ide.common.shared.workitems.PortableEnumParameterDefinition_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.ide.common.shared.workitems.PortableEnumParameterDefinition_FieldSerializer.deserialize(reader, (org.drools.ide.common.shared.workitems.PortableEnumParameterDefinition)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.ide.common.shared.workitems.PortableEnumParameterDefinition_FieldSerializer.serialize(writer, (org.drools.ide.common.shared.workitems.PortableEnumParameterDefinition)object);
  }
  
}
