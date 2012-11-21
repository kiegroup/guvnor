package org.drools.ide.common.client.modeldriven.dt52;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class AttributeCol52_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native java.lang.String getAttribute(org.drools.ide.common.client.modeldriven.dt52.AttributeCol52 instance) /*-{
    return instance.@org.drools.ide.common.client.modeldriven.dt52.AttributeCol52::attribute;
  }-*/;
  
  private static native void setAttribute(org.drools.ide.common.client.modeldriven.dt52.AttributeCol52 instance, java.lang.String value) 
  /*-{
    instance.@org.drools.ide.common.client.modeldriven.dt52.AttributeCol52::attribute = value;
  }-*/;
  
  private static native boolean getReverseOrder(org.drools.ide.common.client.modeldriven.dt52.AttributeCol52 instance) /*-{
    return instance.@org.drools.ide.common.client.modeldriven.dt52.AttributeCol52::reverseOrder;
  }-*/;
  
  private static native void setReverseOrder(org.drools.ide.common.client.modeldriven.dt52.AttributeCol52 instance, boolean value) 
  /*-{
    instance.@org.drools.ide.common.client.modeldriven.dt52.AttributeCol52::reverseOrder = value;
  }-*/;
  
  private static native boolean getUseRowNumber(org.drools.ide.common.client.modeldriven.dt52.AttributeCol52 instance) /*-{
    return instance.@org.drools.ide.common.client.modeldriven.dt52.AttributeCol52::useRowNumber;
  }-*/;
  
  private static native void setUseRowNumber(org.drools.ide.common.client.modeldriven.dt52.AttributeCol52 instance, boolean value) 
  /*-{
    instance.@org.drools.ide.common.client.modeldriven.dt52.AttributeCol52::useRowNumber = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, org.drools.ide.common.client.modeldriven.dt52.AttributeCol52 instance) throws SerializationException {
    setAttribute(instance, streamReader.readString());
    setReverseOrder(instance, streamReader.readBoolean());
    setUseRowNumber(instance, streamReader.readBoolean());
    
    org.drools.ide.common.client.modeldriven.dt52.DTColumnConfig52_FieldSerializer.deserialize(streamReader, instance);
  }
  
  public static org.drools.ide.common.client.modeldriven.dt52.AttributeCol52 instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new org.drools.ide.common.client.modeldriven.dt52.AttributeCol52();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.ide.common.client.modeldriven.dt52.AttributeCol52 instance) throws SerializationException {
    streamWriter.writeString(getAttribute(instance));
    streamWriter.writeBoolean(getReverseOrder(instance));
    streamWriter.writeBoolean(getUseRowNumber(instance));
    
    org.drools.ide.common.client.modeldriven.dt52.DTColumnConfig52_FieldSerializer.serialize(streamWriter, instance);
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.ide.common.client.modeldriven.dt52.AttributeCol52_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.ide.common.client.modeldriven.dt52.AttributeCol52_FieldSerializer.deserialize(reader, (org.drools.ide.common.client.modeldriven.dt52.AttributeCol52)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.ide.common.client.modeldriven.dt52.AttributeCol52_FieldSerializer.serialize(writer, (org.drools.ide.common.client.modeldriven.dt52.AttributeCol52)object);
  }
  
}
