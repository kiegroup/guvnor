package org.drools.ide.common.shared.workitems;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class PortableListParameterDefinition_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  public static void deserialize(SerializationStreamReader streamReader, org.drools.ide.common.shared.workitems.PortableListParameterDefinition instance) throws SerializationException {
    
    org.drools.ide.common.shared.workitems.PortableObjectParameterDefinition_FieldSerializer.deserialize(streamReader, instance);
  }
  
  public static org.drools.ide.common.shared.workitems.PortableListParameterDefinition instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new org.drools.ide.common.shared.workitems.PortableListParameterDefinition();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.ide.common.shared.workitems.PortableListParameterDefinition instance) throws SerializationException {
    
    org.drools.ide.common.shared.workitems.PortableObjectParameterDefinition_FieldSerializer.serialize(streamWriter, instance);
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.ide.common.shared.workitems.PortableListParameterDefinition_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.ide.common.shared.workitems.PortableListParameterDefinition_FieldSerializer.deserialize(reader, (org.drools.ide.common.shared.workitems.PortableListParameterDefinition)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.ide.common.shared.workitems.PortableListParameterDefinition_FieldSerializer.serialize(writer, (org.drools.ide.common.shared.workitems.PortableListParameterDefinition)object);
  }
  
}
