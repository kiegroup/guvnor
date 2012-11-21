package org.drools.ide.common.client.modeldriven.dt52;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class ActionSetFieldCol52_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native java.lang.String getBoundName(org.drools.ide.common.client.modeldriven.dt52.ActionSetFieldCol52 instance) /*-{
    return instance.@org.drools.ide.common.client.modeldriven.dt52.ActionSetFieldCol52::boundName;
  }-*/;
  
  private static native void setBoundName(org.drools.ide.common.client.modeldriven.dt52.ActionSetFieldCol52 instance, java.lang.String value) 
  /*-{
    instance.@org.drools.ide.common.client.modeldriven.dt52.ActionSetFieldCol52::boundName = value;
  }-*/;
  
  private static native java.lang.String getFactField(org.drools.ide.common.client.modeldriven.dt52.ActionSetFieldCol52 instance) /*-{
    return instance.@org.drools.ide.common.client.modeldriven.dt52.ActionSetFieldCol52::factField;
  }-*/;
  
  private static native void setFactField(org.drools.ide.common.client.modeldriven.dt52.ActionSetFieldCol52 instance, java.lang.String value) 
  /*-{
    instance.@org.drools.ide.common.client.modeldriven.dt52.ActionSetFieldCol52::factField = value;
  }-*/;
  
  private static native java.lang.String getType(org.drools.ide.common.client.modeldriven.dt52.ActionSetFieldCol52 instance) /*-{
    return instance.@org.drools.ide.common.client.modeldriven.dt52.ActionSetFieldCol52::type;
  }-*/;
  
  private static native void setType(org.drools.ide.common.client.modeldriven.dt52.ActionSetFieldCol52 instance, java.lang.String value) 
  /*-{
    instance.@org.drools.ide.common.client.modeldriven.dt52.ActionSetFieldCol52::type = value;
  }-*/;
  
  private static native boolean getUpdate(org.drools.ide.common.client.modeldriven.dt52.ActionSetFieldCol52 instance) /*-{
    return instance.@org.drools.ide.common.client.modeldriven.dt52.ActionSetFieldCol52::update;
  }-*/;
  
  private static native void setUpdate(org.drools.ide.common.client.modeldriven.dt52.ActionSetFieldCol52 instance, boolean value) 
  /*-{
    instance.@org.drools.ide.common.client.modeldriven.dt52.ActionSetFieldCol52::update = value;
  }-*/;
  
  private static native java.lang.String getValueList(org.drools.ide.common.client.modeldriven.dt52.ActionSetFieldCol52 instance) /*-{
    return instance.@org.drools.ide.common.client.modeldriven.dt52.ActionSetFieldCol52::valueList;
  }-*/;
  
  private static native void setValueList(org.drools.ide.common.client.modeldriven.dt52.ActionSetFieldCol52 instance, java.lang.String value) 
  /*-{
    instance.@org.drools.ide.common.client.modeldriven.dt52.ActionSetFieldCol52::valueList = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, org.drools.ide.common.client.modeldriven.dt52.ActionSetFieldCol52 instance) throws SerializationException {
    setBoundName(instance, streamReader.readString());
    setFactField(instance, streamReader.readString());
    setType(instance, streamReader.readString());
    setUpdate(instance, streamReader.readBoolean());
    setValueList(instance, streamReader.readString());
    
    org.drools.ide.common.client.modeldriven.dt52.ActionCol52_FieldSerializer.deserialize(streamReader, instance);
  }
  
  public static org.drools.ide.common.client.modeldriven.dt52.ActionSetFieldCol52 instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new org.drools.ide.common.client.modeldriven.dt52.ActionSetFieldCol52();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.ide.common.client.modeldriven.dt52.ActionSetFieldCol52 instance) throws SerializationException {
    streamWriter.writeString(getBoundName(instance));
    streamWriter.writeString(getFactField(instance));
    streamWriter.writeString(getType(instance));
    streamWriter.writeBoolean(getUpdate(instance));
    streamWriter.writeString(getValueList(instance));
    
    org.drools.ide.common.client.modeldriven.dt52.ActionCol52_FieldSerializer.serialize(streamWriter, instance);
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.ide.common.client.modeldriven.dt52.ActionSetFieldCol52_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.ide.common.client.modeldriven.dt52.ActionSetFieldCol52_FieldSerializer.deserialize(reader, (org.drools.ide.common.client.modeldriven.dt52.ActionSetFieldCol52)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.ide.common.client.modeldriven.dt52.ActionSetFieldCol52_FieldSerializer.serialize(writer, (org.drools.ide.common.client.modeldriven.dt52.ActionSetFieldCol52)object);
  }
  
}
