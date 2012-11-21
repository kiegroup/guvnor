package org.drools.ide.common.shared.workitems;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class PortableWorkDefinition_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native java.lang.String getDisplayName(org.drools.ide.common.shared.workitems.PortableWorkDefinition instance) /*-{
    return instance.@org.drools.ide.common.shared.workitems.PortableWorkDefinition::displayName;
  }-*/;
  
  private static native void setDisplayName(org.drools.ide.common.shared.workitems.PortableWorkDefinition instance, java.lang.String value) 
  /*-{
    instance.@org.drools.ide.common.shared.workitems.PortableWorkDefinition::displayName = value;
  }-*/;
  
  private static native java.lang.String getName(org.drools.ide.common.shared.workitems.PortableWorkDefinition instance) /*-{
    return instance.@org.drools.ide.common.shared.workitems.PortableWorkDefinition::name;
  }-*/;
  
  private static native void setName(org.drools.ide.common.shared.workitems.PortableWorkDefinition instance, java.lang.String value) 
  /*-{
    instance.@org.drools.ide.common.shared.workitems.PortableWorkDefinition::name = value;
  }-*/;
  
  private static native java.util.Map getParameters(org.drools.ide.common.shared.workitems.PortableWorkDefinition instance) /*-{
    return instance.@org.drools.ide.common.shared.workitems.PortableWorkDefinition::parameters;
  }-*/;
  
  private static native void setParameters(org.drools.ide.common.shared.workitems.PortableWorkDefinition instance, java.util.Map value) 
  /*-{
    instance.@org.drools.ide.common.shared.workitems.PortableWorkDefinition::parameters = value;
  }-*/;
  
  private static native java.util.Map getResults(org.drools.ide.common.shared.workitems.PortableWorkDefinition instance) /*-{
    return instance.@org.drools.ide.common.shared.workitems.PortableWorkDefinition::results;
  }-*/;
  
  private static native void setResults(org.drools.ide.common.shared.workitems.PortableWorkDefinition instance, java.util.Map value) 
  /*-{
    instance.@org.drools.ide.common.shared.workitems.PortableWorkDefinition::results = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, org.drools.ide.common.shared.workitems.PortableWorkDefinition instance) throws SerializationException {
    setDisplayName(instance, streamReader.readString());
    setName(instance, streamReader.readString());
    setParameters(instance, (java.util.Map) streamReader.readObject());
    setResults(instance, (java.util.Map) streamReader.readObject());
    
  }
  
  public static org.drools.ide.common.shared.workitems.PortableWorkDefinition instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new org.drools.ide.common.shared.workitems.PortableWorkDefinition();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.ide.common.shared.workitems.PortableWorkDefinition instance) throws SerializationException {
    streamWriter.writeString(getDisplayName(instance));
    streamWriter.writeString(getName(instance));
    streamWriter.writeObject(getParameters(instance));
    streamWriter.writeObject(getResults(instance));
    
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.ide.common.shared.workitems.PortableWorkDefinition_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.ide.common.shared.workitems.PortableWorkDefinition_FieldSerializer.deserialize(reader, (org.drools.ide.common.shared.workitems.PortableWorkDefinition)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.ide.common.shared.workitems.PortableWorkDefinition_FieldSerializer.serialize(writer, (org.drools.ide.common.shared.workitems.PortableWorkDefinition)object);
  }
  
}
