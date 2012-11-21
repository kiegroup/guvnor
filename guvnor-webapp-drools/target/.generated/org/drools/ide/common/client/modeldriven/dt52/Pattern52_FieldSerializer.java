package org.drools.ide.common.client.modeldriven.dt52;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class Pattern52_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native java.lang.String getBoundName(org.drools.ide.common.client.modeldriven.dt52.Pattern52 instance) /*-{
    return instance.@org.drools.ide.common.client.modeldriven.dt52.Pattern52::boundName;
  }-*/;
  
  private static native void setBoundName(org.drools.ide.common.client.modeldriven.dt52.Pattern52 instance, java.lang.String value) 
  /*-{
    instance.@org.drools.ide.common.client.modeldriven.dt52.Pattern52::boundName = value;
  }-*/;
  
  private static native java.util.List getConditions(org.drools.ide.common.client.modeldriven.dt52.Pattern52 instance) /*-{
    return instance.@org.drools.ide.common.client.modeldriven.dt52.Pattern52::conditions;
  }-*/;
  
  private static native void setConditions(org.drools.ide.common.client.modeldriven.dt52.Pattern52 instance, java.util.List value) 
  /*-{
    instance.@org.drools.ide.common.client.modeldriven.dt52.Pattern52::conditions = value;
  }-*/;
  
  private static native java.lang.String getEntryPointName(org.drools.ide.common.client.modeldriven.dt52.Pattern52 instance) /*-{
    return instance.@org.drools.ide.common.client.modeldriven.dt52.Pattern52::entryPointName;
  }-*/;
  
  private static native void setEntryPointName(org.drools.ide.common.client.modeldriven.dt52.Pattern52 instance, java.lang.String value) 
  /*-{
    instance.@org.drools.ide.common.client.modeldriven.dt52.Pattern52::entryPointName = value;
  }-*/;
  
  private static native java.lang.String getFactType(org.drools.ide.common.client.modeldriven.dt52.Pattern52 instance) /*-{
    return instance.@org.drools.ide.common.client.modeldriven.dt52.Pattern52::factType;
  }-*/;
  
  private static native void setFactType(org.drools.ide.common.client.modeldriven.dt52.Pattern52 instance, java.lang.String value) 
  /*-{
    instance.@org.drools.ide.common.client.modeldriven.dt52.Pattern52::factType = value;
  }-*/;
  
  private static native boolean getIsNegated(org.drools.ide.common.client.modeldriven.dt52.Pattern52 instance) /*-{
    return instance.@org.drools.ide.common.client.modeldriven.dt52.Pattern52::isNegated;
  }-*/;
  
  private static native void setIsNegated(org.drools.ide.common.client.modeldriven.dt52.Pattern52 instance, boolean value) 
  /*-{
    instance.@org.drools.ide.common.client.modeldriven.dt52.Pattern52::isNegated = value;
  }-*/;
  
  private static native org.drools.ide.common.client.modeldriven.brl.CEPWindow getWindow(org.drools.ide.common.client.modeldriven.dt52.Pattern52 instance) /*-{
    return instance.@org.drools.ide.common.client.modeldriven.dt52.Pattern52::window;
  }-*/;
  
  private static native void setWindow(org.drools.ide.common.client.modeldriven.dt52.Pattern52 instance, org.drools.ide.common.client.modeldriven.brl.CEPWindow value) 
  /*-{
    instance.@org.drools.ide.common.client.modeldriven.dt52.Pattern52::window = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, org.drools.ide.common.client.modeldriven.dt52.Pattern52 instance) throws SerializationException {
    setBoundName(instance, streamReader.readString());
    setConditions(instance, (java.util.List) streamReader.readObject());
    setEntryPointName(instance, streamReader.readString());
    setFactType(instance, streamReader.readString());
    setIsNegated(instance, streamReader.readBoolean());
    setWindow(instance, (org.drools.ide.common.client.modeldriven.brl.CEPWindow) streamReader.readObject());
    
  }
  
  public static org.drools.ide.common.client.modeldriven.dt52.Pattern52 instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new org.drools.ide.common.client.modeldriven.dt52.Pattern52();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.ide.common.client.modeldriven.dt52.Pattern52 instance) throws SerializationException {
    streamWriter.writeString(getBoundName(instance));
    streamWriter.writeObject(getConditions(instance));
    streamWriter.writeString(getEntryPointName(instance));
    streamWriter.writeString(getFactType(instance));
    streamWriter.writeBoolean(getIsNegated(instance));
    streamWriter.writeObject(getWindow(instance));
    
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.ide.common.client.modeldriven.dt52.Pattern52_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.ide.common.client.modeldriven.dt52.Pattern52_FieldSerializer.deserialize(reader, (org.drools.ide.common.client.modeldriven.dt52.Pattern52)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.ide.common.client.modeldriven.dt52.Pattern52_FieldSerializer.serialize(writer, (org.drools.ide.common.client.modeldriven.dt52.Pattern52)object);
  }
  
}
