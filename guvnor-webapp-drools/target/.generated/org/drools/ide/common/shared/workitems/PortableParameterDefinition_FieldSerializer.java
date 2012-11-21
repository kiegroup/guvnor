package org.drools.ide.common.shared.workitems;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class PortableParameterDefinition_FieldSerializer {
  private static native java.lang.String getName(org.drools.ide.common.shared.workitems.PortableParameterDefinition instance) /*-{
    return instance.@org.drools.ide.common.shared.workitems.PortableParameterDefinition::name;
  }-*/;
  
  private static native void setName(org.drools.ide.common.shared.workitems.PortableParameterDefinition instance, java.lang.String value) 
  /*-{
    instance.@org.drools.ide.common.shared.workitems.PortableParameterDefinition::name = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, org.drools.ide.common.shared.workitems.PortableParameterDefinition instance) throws SerializationException {
    setName(instance, streamReader.readString());
    
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.ide.common.shared.workitems.PortableParameterDefinition instance) throws SerializationException {
    streamWriter.writeString(getName(instance));
    
  }
  
}
