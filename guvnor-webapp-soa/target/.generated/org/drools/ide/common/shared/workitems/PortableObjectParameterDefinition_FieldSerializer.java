package org.drools.ide.common.shared.workitems;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class PortableObjectParameterDefinition_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native java.lang.String getBinding(org.drools.ide.common.shared.workitems.PortableObjectParameterDefinition instance) /*-{
    return instance.@org.drools.ide.common.shared.workitems.PortableObjectParameterDefinition::binding;
  }-*/;
  
  private static native void setBinding(org.drools.ide.common.shared.workitems.PortableObjectParameterDefinition instance, java.lang.String value) 
  /*-{
    instance.@org.drools.ide.common.shared.workitems.PortableObjectParameterDefinition::binding = value;
  }-*/;
  
  private static native java.lang.String getClassName(org.drools.ide.common.shared.workitems.PortableObjectParameterDefinition instance) /*-{
    return instance.@org.drools.ide.common.shared.workitems.PortableObjectParameterDefinition::className;
  }-*/;
  
  private static native void setClassName(org.drools.ide.common.shared.workitems.PortableObjectParameterDefinition instance, java.lang.String value) 
  /*-{
    instance.@org.drools.ide.common.shared.workitems.PortableObjectParameterDefinition::className = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, org.drools.ide.common.shared.workitems.PortableObjectParameterDefinition instance) throws SerializationException {
    setBinding(instance, streamReader.readString());
    setClassName(instance, streamReader.readString());
    
    org.drools.ide.common.shared.workitems.PortableParameterDefinition_FieldSerializer.deserialize(streamReader, instance);
  }
  
  public static org.drools.ide.common.shared.workitems.PortableObjectParameterDefinition instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new org.drools.ide.common.shared.workitems.PortableObjectParameterDefinition();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.ide.common.shared.workitems.PortableObjectParameterDefinition instance) throws SerializationException {
    streamWriter.writeString(getBinding(instance));
    streamWriter.writeString(getClassName(instance));
    
    org.drools.ide.common.shared.workitems.PortableParameterDefinition_FieldSerializer.serialize(streamWriter, instance);
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.ide.common.shared.workitems.PortableObjectParameterDefinition_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.ide.common.shared.workitems.PortableObjectParameterDefinition_FieldSerializer.deserialize(reader, (org.drools.ide.common.shared.workitems.PortableObjectParameterDefinition)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.ide.common.shared.workitems.PortableObjectParameterDefinition_FieldSerializer.serialize(writer, (org.drools.ide.common.shared.workitems.PortableObjectParameterDefinition)object);
  }
  
}
