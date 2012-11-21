package org.drools.ide.common.shared.workitems;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class PortableBooleanParameterDefinition_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native java.lang.String getBinding(org.drools.ide.common.shared.workitems.PortableBooleanParameterDefinition instance) /*-{
    return instance.@org.drools.ide.common.shared.workitems.PortableBooleanParameterDefinition::binding;
  }-*/;
  
  private static native void setBinding(org.drools.ide.common.shared.workitems.PortableBooleanParameterDefinition instance, java.lang.String value) 
  /*-{
    instance.@org.drools.ide.common.shared.workitems.PortableBooleanParameterDefinition::binding = value;
  }-*/;
  
  private static native java.lang.Boolean getValue(org.drools.ide.common.shared.workitems.PortableBooleanParameterDefinition instance) /*-{
    return instance.@org.drools.ide.common.shared.workitems.PortableBooleanParameterDefinition::value;
  }-*/;
  
  private static native void setValue(org.drools.ide.common.shared.workitems.PortableBooleanParameterDefinition instance, java.lang.Boolean value) 
  /*-{
    instance.@org.drools.ide.common.shared.workitems.PortableBooleanParameterDefinition::value = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, org.drools.ide.common.shared.workitems.PortableBooleanParameterDefinition instance) throws SerializationException {
    setBinding(instance, streamReader.readString());
    setValue(instance, (java.lang.Boolean) streamReader.readObject());
    
    org.drools.ide.common.shared.workitems.PortableParameterDefinition_FieldSerializer.deserialize(streamReader, instance);
  }
  
  public static org.drools.ide.common.shared.workitems.PortableBooleanParameterDefinition instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new org.drools.ide.common.shared.workitems.PortableBooleanParameterDefinition();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.ide.common.shared.workitems.PortableBooleanParameterDefinition instance) throws SerializationException {
    streamWriter.writeString(getBinding(instance));
    streamWriter.writeObject(getValue(instance));
    
    org.drools.ide.common.shared.workitems.PortableParameterDefinition_FieldSerializer.serialize(streamWriter, instance);
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.ide.common.shared.workitems.PortableBooleanParameterDefinition_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.ide.common.shared.workitems.PortableBooleanParameterDefinition_FieldSerializer.deserialize(reader, (org.drools.ide.common.shared.workitems.PortableBooleanParameterDefinition)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.ide.common.shared.workitems.PortableBooleanParameterDefinition_FieldSerializer.serialize(writer, (org.drools.ide.common.shared.workitems.PortableBooleanParameterDefinition)object);
  }
  
}
