package org.drools.ide.common.shared.workitems;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class PortableStringParameterDefinition_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native java.lang.String getBinding(org.drools.ide.common.shared.workitems.PortableStringParameterDefinition instance) /*-{
    return instance.@org.drools.ide.common.shared.workitems.PortableStringParameterDefinition::binding;
  }-*/;
  
  private static native void setBinding(org.drools.ide.common.shared.workitems.PortableStringParameterDefinition instance, java.lang.String value) 
  /*-{
    instance.@org.drools.ide.common.shared.workitems.PortableStringParameterDefinition::binding = value;
  }-*/;
  
  private static native java.lang.String getValue(org.drools.ide.common.shared.workitems.PortableStringParameterDefinition instance) /*-{
    return instance.@org.drools.ide.common.shared.workitems.PortableStringParameterDefinition::value;
  }-*/;
  
  private static native void setValue(org.drools.ide.common.shared.workitems.PortableStringParameterDefinition instance, java.lang.String value) 
  /*-{
    instance.@org.drools.ide.common.shared.workitems.PortableStringParameterDefinition::value = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, org.drools.ide.common.shared.workitems.PortableStringParameterDefinition instance) throws SerializationException {
    setBinding(instance, streamReader.readString());
    setValue(instance, streamReader.readString());
    
    org.drools.ide.common.shared.workitems.PortableParameterDefinition_FieldSerializer.deserialize(streamReader, instance);
  }
  
  public static org.drools.ide.common.shared.workitems.PortableStringParameterDefinition instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new org.drools.ide.common.shared.workitems.PortableStringParameterDefinition();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.ide.common.shared.workitems.PortableStringParameterDefinition instance) throws SerializationException {
    streamWriter.writeString(getBinding(instance));
    streamWriter.writeString(getValue(instance));
    
    org.drools.ide.common.shared.workitems.PortableParameterDefinition_FieldSerializer.serialize(streamWriter, instance);
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.ide.common.shared.workitems.PortableStringParameterDefinition_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.ide.common.shared.workitems.PortableStringParameterDefinition_FieldSerializer.deserialize(reader, (org.drools.ide.common.shared.workitems.PortableStringParameterDefinition)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.ide.common.shared.workitems.PortableStringParameterDefinition_FieldSerializer.serialize(writer, (org.drools.ide.common.shared.workitems.PortableStringParameterDefinition)object);
  }
  
}
