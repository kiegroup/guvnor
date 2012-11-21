package org.drools.guvnor.client.rpc;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class PageResponse_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native boolean getLastPage(org.drools.guvnor.client.rpc.PageResponse instance) /*-{
    return instance.@org.drools.guvnor.client.rpc.PageResponse::lastPage;
  }-*/;
  
  private static native void setLastPage(org.drools.guvnor.client.rpc.PageResponse instance, boolean value) 
  /*-{
    instance.@org.drools.guvnor.client.rpc.PageResponse::lastPage = value;
  }-*/;
  
  private static native java.util.List getPageRowList(org.drools.guvnor.client.rpc.PageResponse instance) /*-{
    return instance.@org.drools.guvnor.client.rpc.PageResponse::pageRowList;
  }-*/;
  
  private static native void setPageRowList(org.drools.guvnor.client.rpc.PageResponse instance, java.util.List value) 
  /*-{
    instance.@org.drools.guvnor.client.rpc.PageResponse::pageRowList = value;
  }-*/;
  
  private static native int getStartRowIndex(org.drools.guvnor.client.rpc.PageResponse instance) /*-{
    return instance.@org.drools.guvnor.client.rpc.PageResponse::startRowIndex;
  }-*/;
  
  private static native void setStartRowIndex(org.drools.guvnor.client.rpc.PageResponse instance, int value) 
  /*-{
    instance.@org.drools.guvnor.client.rpc.PageResponse::startRowIndex = value;
  }-*/;
  
  private static native int getTotalRowSize(org.drools.guvnor.client.rpc.PageResponse instance) /*-{
    return instance.@org.drools.guvnor.client.rpc.PageResponse::totalRowSize;
  }-*/;
  
  private static native void setTotalRowSize(org.drools.guvnor.client.rpc.PageResponse instance, int value) 
  /*-{
    instance.@org.drools.guvnor.client.rpc.PageResponse::totalRowSize = value;
  }-*/;
  
  private static native boolean getTotalRowSizeExact(org.drools.guvnor.client.rpc.PageResponse instance) /*-{
    return instance.@org.drools.guvnor.client.rpc.PageResponse::totalRowSizeExact;
  }-*/;
  
  private static native void setTotalRowSizeExact(org.drools.guvnor.client.rpc.PageResponse instance, boolean value) 
  /*-{
    instance.@org.drools.guvnor.client.rpc.PageResponse::totalRowSizeExact = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, org.drools.guvnor.client.rpc.PageResponse instance) throws SerializationException {
    setLastPage(instance, streamReader.readBoolean());
    setPageRowList(instance, (java.util.List) streamReader.readObject());
    setStartRowIndex(instance, streamReader.readInt());
    setTotalRowSize(instance, streamReader.readInt());
    setTotalRowSizeExact(instance, streamReader.readBoolean());
    
  }
  
  public static org.drools.guvnor.client.rpc.PageResponse instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new org.drools.guvnor.client.rpc.PageResponse();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.guvnor.client.rpc.PageResponse instance) throws SerializationException {
    streamWriter.writeBoolean(getLastPage(instance));
    streamWriter.writeObject(getPageRowList(instance));
    streamWriter.writeInt(getStartRowIndex(instance));
    streamWriter.writeInt(getTotalRowSize(instance));
    streamWriter.writeBoolean(getTotalRowSizeExact(instance));
    
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.guvnor.client.rpc.PageResponse_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.guvnor.client.rpc.PageResponse_FieldSerializer.deserialize(reader, (org.drools.guvnor.client.rpc.PageResponse)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.guvnor.client.rpc.PageResponse_FieldSerializer.serialize(writer, (org.drools.guvnor.client.rpc.PageResponse)object);
  }
  
}
