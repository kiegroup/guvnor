package org.drools.ide.common.client.modeldriven.dt52;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class DTCellValue52_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native org.drools.ide.common.client.modeldriven.dt52.DTDataTypes52 getDataType(org.drools.ide.common.client.modeldriven.dt52.DTCellValue52 instance) /*-{
    return instance.@org.drools.ide.common.client.modeldriven.dt52.DTCellValue52::dataType;
  }-*/;
  
  private static native void setDataType(org.drools.ide.common.client.modeldriven.dt52.DTCellValue52 instance, org.drools.ide.common.client.modeldriven.dt52.DTDataTypes52 value) 
  /*-{
    instance.@org.drools.ide.common.client.modeldriven.dt52.DTCellValue52::dataType = value;
  }-*/;
  
  private static native boolean getIsOtherwise(org.drools.ide.common.client.modeldriven.dt52.DTCellValue52 instance) /*-{
    return instance.@org.drools.ide.common.client.modeldriven.dt52.DTCellValue52::isOtherwise;
  }-*/;
  
  private static native void setIsOtherwise(org.drools.ide.common.client.modeldriven.dt52.DTCellValue52 instance, boolean value) 
  /*-{
    instance.@org.drools.ide.common.client.modeldriven.dt52.DTCellValue52::isOtherwise = value;
  }-*/;
  
  private static native java.lang.Boolean getValueBoolean(org.drools.ide.common.client.modeldriven.dt52.DTCellValue52 instance) /*-{
    return instance.@org.drools.ide.common.client.modeldriven.dt52.DTCellValue52::valueBoolean;
  }-*/;
  
  private static native void setValueBoolean(org.drools.ide.common.client.modeldriven.dt52.DTCellValue52 instance, java.lang.Boolean value) 
  /*-{
    instance.@org.drools.ide.common.client.modeldriven.dt52.DTCellValue52::valueBoolean = value;
  }-*/;
  
  private static native java.util.Date getValueDate(org.drools.ide.common.client.modeldriven.dt52.DTCellValue52 instance) /*-{
    return instance.@org.drools.ide.common.client.modeldriven.dt52.DTCellValue52::valueDate;
  }-*/;
  
  private static native void setValueDate(org.drools.ide.common.client.modeldriven.dt52.DTCellValue52 instance, java.util.Date value) 
  /*-{
    instance.@org.drools.ide.common.client.modeldriven.dt52.DTCellValue52::valueDate = value;
  }-*/;
  
  private static native java.lang.Number getValueNumeric(org.drools.ide.common.client.modeldriven.dt52.DTCellValue52 instance) /*-{
    return instance.@org.drools.ide.common.client.modeldriven.dt52.DTCellValue52::valueNumeric;
  }-*/;
  
  private static native void setValueNumeric(org.drools.ide.common.client.modeldriven.dt52.DTCellValue52 instance, java.lang.Number value) 
  /*-{
    instance.@org.drools.ide.common.client.modeldriven.dt52.DTCellValue52::valueNumeric = value;
  }-*/;
  
  private static native java.lang.String getValueString(org.drools.ide.common.client.modeldriven.dt52.DTCellValue52 instance) /*-{
    return instance.@org.drools.ide.common.client.modeldriven.dt52.DTCellValue52::valueString;
  }-*/;
  
  private static native void setValueString(org.drools.ide.common.client.modeldriven.dt52.DTCellValue52 instance, java.lang.String value) 
  /*-{
    instance.@org.drools.ide.common.client.modeldriven.dt52.DTCellValue52::valueString = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, org.drools.ide.common.client.modeldriven.dt52.DTCellValue52 instance) throws SerializationException {
    setDataType(instance, (org.drools.ide.common.client.modeldriven.dt52.DTDataTypes52) streamReader.readObject());
    setIsOtherwise(instance, streamReader.readBoolean());
    setValueBoolean(instance, (java.lang.Boolean) streamReader.readObject());
    setValueDate(instance, (java.util.Date) streamReader.readObject());
    setValueNumeric(instance, (java.lang.Number) streamReader.readObject());
    setValueString(instance, streamReader.readString());
    
  }
  
  public static org.drools.ide.common.client.modeldriven.dt52.DTCellValue52 instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new org.drools.ide.common.client.modeldriven.dt52.DTCellValue52();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.ide.common.client.modeldriven.dt52.DTCellValue52 instance) throws SerializationException {
    streamWriter.writeObject(getDataType(instance));
    streamWriter.writeBoolean(getIsOtherwise(instance));
    streamWriter.writeObject(getValueBoolean(instance));
    streamWriter.writeObject(getValueDate(instance));
    streamWriter.writeObject(getValueNumeric(instance));
    streamWriter.writeString(getValueString(instance));
    
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.ide.common.client.modeldriven.dt52.DTCellValue52_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.ide.common.client.modeldriven.dt52.DTCellValue52_FieldSerializer.deserialize(reader, (org.drools.ide.common.client.modeldriven.dt52.DTCellValue52)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.ide.common.client.modeldriven.dt52.DTCellValue52_FieldSerializer.serialize(writer, (org.drools.ide.common.client.modeldriven.dt52.DTCellValue52)object);
  }
  
}
