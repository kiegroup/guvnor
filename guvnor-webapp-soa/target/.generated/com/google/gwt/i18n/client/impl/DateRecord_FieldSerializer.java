package com.google.gwt.i18n.client.impl;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class DateRecord_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native boolean getAmbiguousYear(com.google.gwt.i18n.client.impl.DateRecord instance) /*-{
    return instance.@com.google.gwt.i18n.client.impl.DateRecord::ambiguousYear;
  }-*/;
  
  private static native void setAmbiguousYear(com.google.gwt.i18n.client.impl.DateRecord instance, boolean value) 
  /*-{
    instance.@com.google.gwt.i18n.client.impl.DateRecord::ambiguousYear = value;
  }-*/;
  
  private static native int getAmpm(com.google.gwt.i18n.client.impl.DateRecord instance) /*-{
    return instance.@com.google.gwt.i18n.client.impl.DateRecord::ampm;
  }-*/;
  
  private static native void setAmpm(com.google.gwt.i18n.client.impl.DateRecord instance, int value) 
  /*-{
    instance.@com.google.gwt.i18n.client.impl.DateRecord::ampm = value;
  }-*/;
  
  private static native int getDayOfMonth(com.google.gwt.i18n.client.impl.DateRecord instance) /*-{
    return instance.@com.google.gwt.i18n.client.impl.DateRecord::dayOfMonth;
  }-*/;
  
  private static native void setDayOfMonth(com.google.gwt.i18n.client.impl.DateRecord instance, int value) 
  /*-{
    instance.@com.google.gwt.i18n.client.impl.DateRecord::dayOfMonth = value;
  }-*/;
  
  private static native int getDayOfWeek(com.google.gwt.i18n.client.impl.DateRecord instance) /*-{
    return instance.@com.google.gwt.i18n.client.impl.DateRecord::dayOfWeek;
  }-*/;
  
  private static native void setDayOfWeek(com.google.gwt.i18n.client.impl.DateRecord instance, int value) 
  /*-{
    instance.@com.google.gwt.i18n.client.impl.DateRecord::dayOfWeek = value;
  }-*/;
  
  private static native int getEra(com.google.gwt.i18n.client.impl.DateRecord instance) /*-{
    return instance.@com.google.gwt.i18n.client.impl.DateRecord::era;
  }-*/;
  
  private static native void setEra(com.google.gwt.i18n.client.impl.DateRecord instance, int value) 
  /*-{
    instance.@com.google.gwt.i18n.client.impl.DateRecord::era = value;
  }-*/;
  
  private static native int getHours(com.google.gwt.i18n.client.impl.DateRecord instance) /*-{
    return instance.@com.google.gwt.i18n.client.impl.DateRecord::hours;
  }-*/;
  
  private static native void setHours(com.google.gwt.i18n.client.impl.DateRecord instance, int value) 
  /*-{
    instance.@com.google.gwt.i18n.client.impl.DateRecord::hours = value;
  }-*/;
  
  private static native int getMilliseconds(com.google.gwt.i18n.client.impl.DateRecord instance) /*-{
    return instance.@com.google.gwt.i18n.client.impl.DateRecord::milliseconds;
  }-*/;
  
  private static native void setMilliseconds(com.google.gwt.i18n.client.impl.DateRecord instance, int value) 
  /*-{
    instance.@com.google.gwt.i18n.client.impl.DateRecord::milliseconds = value;
  }-*/;
  
  private static native int getMinutes(com.google.gwt.i18n.client.impl.DateRecord instance) /*-{
    return instance.@com.google.gwt.i18n.client.impl.DateRecord::minutes;
  }-*/;
  
  private static native void setMinutes(com.google.gwt.i18n.client.impl.DateRecord instance, int value) 
  /*-{
    instance.@com.google.gwt.i18n.client.impl.DateRecord::minutes = value;
  }-*/;
  
  private static native int getMonth(com.google.gwt.i18n.client.impl.DateRecord instance) /*-{
    return instance.@com.google.gwt.i18n.client.impl.DateRecord::month;
  }-*/;
  
  private static native void setMonth(com.google.gwt.i18n.client.impl.DateRecord instance, int value) 
  /*-{
    instance.@com.google.gwt.i18n.client.impl.DateRecord::month = value;
  }-*/;
  
  private static native int getSeconds(com.google.gwt.i18n.client.impl.DateRecord instance) /*-{
    return instance.@com.google.gwt.i18n.client.impl.DateRecord::seconds;
  }-*/;
  
  private static native void setSeconds(com.google.gwt.i18n.client.impl.DateRecord instance, int value) 
  /*-{
    instance.@com.google.gwt.i18n.client.impl.DateRecord::seconds = value;
  }-*/;
  
  private static native int getTzOffset(com.google.gwt.i18n.client.impl.DateRecord instance) /*-{
    return instance.@com.google.gwt.i18n.client.impl.DateRecord::tzOffset;
  }-*/;
  
  private static native void setTzOffset(com.google.gwt.i18n.client.impl.DateRecord instance, int value) 
  /*-{
    instance.@com.google.gwt.i18n.client.impl.DateRecord::tzOffset = value;
  }-*/;
  
  private static native int getYear(com.google.gwt.i18n.client.impl.DateRecord instance) /*-{
    return instance.@com.google.gwt.i18n.client.impl.DateRecord::year;
  }-*/;
  
  private static native void setYear(com.google.gwt.i18n.client.impl.DateRecord instance, int value) 
  /*-{
    instance.@com.google.gwt.i18n.client.impl.DateRecord::year = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, com.google.gwt.i18n.client.impl.DateRecord instance) throws SerializationException {
    setAmbiguousYear(instance, streamReader.readBoolean());
    setAmpm(instance, streamReader.readInt());
    setDayOfMonth(instance, streamReader.readInt());
    setDayOfWeek(instance, streamReader.readInt());
    setEra(instance, streamReader.readInt());
    setHours(instance, streamReader.readInt());
    setMilliseconds(instance, streamReader.readInt());
    setMinutes(instance, streamReader.readInt());
    setMonth(instance, streamReader.readInt());
    setSeconds(instance, streamReader.readInt());
    setTzOffset(instance, streamReader.readInt());
    setYear(instance, streamReader.readInt());
    
    com.google.gwt.user.client.rpc.core.java.util.Date_CustomFieldSerializer.deserialize(streamReader, instance);
  }
  
  public static com.google.gwt.i18n.client.impl.DateRecord instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new com.google.gwt.i18n.client.impl.DateRecord();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, com.google.gwt.i18n.client.impl.DateRecord instance) throws SerializationException {
    streamWriter.writeBoolean(getAmbiguousYear(instance));
    streamWriter.writeInt(getAmpm(instance));
    streamWriter.writeInt(getDayOfMonth(instance));
    streamWriter.writeInt(getDayOfWeek(instance));
    streamWriter.writeInt(getEra(instance));
    streamWriter.writeInt(getHours(instance));
    streamWriter.writeInt(getMilliseconds(instance));
    streamWriter.writeInt(getMinutes(instance));
    streamWriter.writeInt(getMonth(instance));
    streamWriter.writeInt(getSeconds(instance));
    streamWriter.writeInt(getTzOffset(instance));
    streamWriter.writeInt(getYear(instance));
    
    com.google.gwt.user.client.rpc.core.java.util.Date_CustomFieldSerializer.serialize(streamWriter, instance);
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return com.google.gwt.i18n.client.impl.DateRecord_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    com.google.gwt.i18n.client.impl.DateRecord_FieldSerializer.deserialize(reader, (com.google.gwt.i18n.client.impl.DateRecord)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    com.google.gwt.i18n.client.impl.DateRecord_FieldSerializer.serialize(writer, (com.google.gwt.i18n.client.impl.DateRecord)object);
  }
  
}
