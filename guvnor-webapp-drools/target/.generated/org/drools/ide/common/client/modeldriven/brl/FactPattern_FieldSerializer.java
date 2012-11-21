package org.drools.ide.common.client.modeldriven.brl;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class FactPattern_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native java.lang.String getBoundName(org.drools.ide.common.client.modeldriven.brl.FactPattern instance) /*-{
    return instance.@org.drools.ide.common.client.modeldriven.brl.FactPattern::boundName;
  }-*/;
  
  private static native void setBoundName(org.drools.ide.common.client.modeldriven.brl.FactPattern instance, java.lang.String value) 
  /*-{
    instance.@org.drools.ide.common.client.modeldriven.brl.FactPattern::boundName = value;
  }-*/;
  
  private static native java.lang.String getFactType(org.drools.ide.common.client.modeldriven.brl.FactPattern instance) /*-{
    return instance.@org.drools.ide.common.client.modeldriven.brl.FactPattern::factType;
  }-*/;
  
  private static native void setFactType(org.drools.ide.common.client.modeldriven.brl.FactPattern instance, java.lang.String value) 
  /*-{
    instance.@org.drools.ide.common.client.modeldriven.brl.FactPattern::factType = value;
  }-*/;
  
  private static native boolean getIsNegated(org.drools.ide.common.client.modeldriven.brl.FactPattern instance) /*-{
    return instance.@org.drools.ide.common.client.modeldriven.brl.FactPattern::isNegated;
  }-*/;
  
  private static native void setIsNegated(org.drools.ide.common.client.modeldriven.brl.FactPattern instance, boolean value) 
  /*-{
    instance.@org.drools.ide.common.client.modeldriven.brl.FactPattern::isNegated = value;
  }-*/;
  
  private static native org.drools.ide.common.client.modeldriven.brl.CEPWindow getWindow(org.drools.ide.common.client.modeldriven.brl.FactPattern instance) /*-{
    return instance.@org.drools.ide.common.client.modeldriven.brl.FactPattern::window;
  }-*/;
  
  private static native void setWindow(org.drools.ide.common.client.modeldriven.brl.FactPattern instance, org.drools.ide.common.client.modeldriven.brl.CEPWindow value) 
  /*-{
    instance.@org.drools.ide.common.client.modeldriven.brl.FactPattern::window = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, org.drools.ide.common.client.modeldriven.brl.FactPattern instance) throws SerializationException {
    setBoundName(instance, streamReader.readString());
    instance.constraintList = (org.drools.ide.common.client.modeldriven.brl.CompositeFieldConstraint) streamReader.readObject();
    setFactType(instance, streamReader.readString());
    setIsNegated(instance, streamReader.readBoolean());
    setWindow(instance, (org.drools.ide.common.client.modeldriven.brl.CEPWindow) streamReader.readObject());
    
  }
  
  public static org.drools.ide.common.client.modeldriven.brl.FactPattern instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new org.drools.ide.common.client.modeldriven.brl.FactPattern();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.ide.common.client.modeldriven.brl.FactPattern instance) throws SerializationException {
    streamWriter.writeString(getBoundName(instance));
    streamWriter.writeObject(instance.constraintList);
    streamWriter.writeString(getFactType(instance));
    streamWriter.writeBoolean(getIsNegated(instance));
    streamWriter.writeObject(getWindow(instance));
    
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.ide.common.client.modeldriven.brl.FactPattern_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.ide.common.client.modeldriven.brl.FactPattern_FieldSerializer.deserialize(reader, (org.drools.ide.common.client.modeldriven.brl.FactPattern)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.ide.common.client.modeldriven.brl.FactPattern_FieldSerializer.serialize(writer, (org.drools.ide.common.client.modeldriven.brl.FactPattern)object);
  }
  
}
