package org.drools.ide.common.client.modeldriven.dt52;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class DTColumnConfig52_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native java.lang.String getHeader(org.drools.ide.common.client.modeldriven.dt52.DTColumnConfig52 instance) /*-{
    return instance.@org.drools.ide.common.client.modeldriven.dt52.DTColumnConfig52::header;
  }-*/;
  
  private static native void setHeader(org.drools.ide.common.client.modeldriven.dt52.DTColumnConfig52 instance, java.lang.String value) 
  /*-{
    instance.@org.drools.ide.common.client.modeldriven.dt52.DTColumnConfig52::header = value;
  }-*/;
  
  private static native boolean getHideColumn(org.drools.ide.common.client.modeldriven.dt52.DTColumnConfig52 instance) /*-{
    return instance.@org.drools.ide.common.client.modeldriven.dt52.DTColumnConfig52::hideColumn;
  }-*/;
  
  private static native void setHideColumn(org.drools.ide.common.client.modeldriven.dt52.DTColumnConfig52 instance, boolean value) 
  /*-{
    instance.@org.drools.ide.common.client.modeldriven.dt52.DTColumnConfig52::hideColumn = value;
  }-*/;
  
  private static native org.drools.ide.common.client.modeldriven.dt52.DTCellValue52 getTypedDefaultValue(org.drools.ide.common.client.modeldriven.dt52.DTColumnConfig52 instance) /*-{
    return instance.@org.drools.ide.common.client.modeldriven.dt52.DTColumnConfig52::typedDefaultValue;
  }-*/;
  
  private static native void setTypedDefaultValue(org.drools.ide.common.client.modeldriven.dt52.DTColumnConfig52 instance, org.drools.ide.common.client.modeldriven.dt52.DTCellValue52 value) 
  /*-{
    instance.@org.drools.ide.common.client.modeldriven.dt52.DTColumnConfig52::typedDefaultValue = value;
  }-*/;
  
  private static native int getWidth(org.drools.ide.common.client.modeldriven.dt52.DTColumnConfig52 instance) /*-{
    return instance.@org.drools.ide.common.client.modeldriven.dt52.DTColumnConfig52::width;
  }-*/;
  
  private static native void setWidth(org.drools.ide.common.client.modeldriven.dt52.DTColumnConfig52 instance, int value) 
  /*-{
    instance.@org.drools.ide.common.client.modeldriven.dt52.DTColumnConfig52::width = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, org.drools.ide.common.client.modeldriven.dt52.DTColumnConfig52 instance) throws SerializationException {
    instance.defaultValue = streamReader.readString();
    setHeader(instance, streamReader.readString());
    setHideColumn(instance, streamReader.readBoolean());
    setTypedDefaultValue(instance, (org.drools.ide.common.client.modeldriven.dt52.DTCellValue52) streamReader.readObject());
    setWidth(instance, streamReader.readInt());
    
  }
  
  public static org.drools.ide.common.client.modeldriven.dt52.DTColumnConfig52 instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new org.drools.ide.common.client.modeldriven.dt52.DTColumnConfig52();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.ide.common.client.modeldriven.dt52.DTColumnConfig52 instance) throws SerializationException {
    streamWriter.writeString(instance.defaultValue);
    streamWriter.writeString(getHeader(instance));
    streamWriter.writeBoolean(getHideColumn(instance));
    streamWriter.writeObject(getTypedDefaultValue(instance));
    streamWriter.writeInt(getWidth(instance));
    
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.ide.common.client.modeldriven.dt52.DTColumnConfig52_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.ide.common.client.modeldriven.dt52.DTColumnConfig52_FieldSerializer.deserialize(reader, (org.drools.ide.common.client.modeldriven.dt52.DTColumnConfig52)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.ide.common.client.modeldriven.dt52.DTColumnConfig52_FieldSerializer.serialize(writer, (org.drools.ide.common.client.modeldriven.dt52.DTColumnConfig52)object);
  }
  
}
