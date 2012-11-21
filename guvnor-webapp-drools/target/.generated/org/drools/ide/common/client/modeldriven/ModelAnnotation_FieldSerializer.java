package org.drools.ide.common.client.modeldriven;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;
import com.google.gwt.user.client.rpc.impl.ReflectionHelper;

@SuppressWarnings("deprecation")
public class ModelAnnotation_FieldSerializer implements com.google.gwt.user.client.rpc.impl.TypeHandler {
  private static native java.lang.String getAnnotationName(org.drools.ide.common.client.modeldriven.ModelAnnotation instance) /*-{
    return instance.@org.drools.ide.common.client.modeldriven.ModelAnnotation::annotationName;
  }-*/;
  
  private static native void setAnnotationName(org.drools.ide.common.client.modeldriven.ModelAnnotation instance, java.lang.String value) 
  /*-{
    instance.@org.drools.ide.common.client.modeldriven.ModelAnnotation::annotationName = value;
  }-*/;
  
  private static native java.util.Map getAnnotationValues(org.drools.ide.common.client.modeldriven.ModelAnnotation instance) /*-{
    return instance.@org.drools.ide.common.client.modeldriven.ModelAnnotation::annotationValues;
  }-*/;
  
  private static native void setAnnotationValues(org.drools.ide.common.client.modeldriven.ModelAnnotation instance, java.util.Map value) 
  /*-{
    instance.@org.drools.ide.common.client.modeldriven.ModelAnnotation::annotationValues = value;
  }-*/;
  
  public static void deserialize(SerializationStreamReader streamReader, org.drools.ide.common.client.modeldriven.ModelAnnotation instance) throws SerializationException {
    setAnnotationName(instance, streamReader.readString());
    setAnnotationValues(instance, (java.util.Map) streamReader.readObject());
    
  }
  
  public static org.drools.ide.common.client.modeldriven.ModelAnnotation instantiate(SerializationStreamReader streamReader) throws SerializationException {
    return new org.drools.ide.common.client.modeldriven.ModelAnnotation();
  }
  
  public static void serialize(SerializationStreamWriter streamWriter, org.drools.ide.common.client.modeldriven.ModelAnnotation instance) throws SerializationException {
    streamWriter.writeString(getAnnotationName(instance));
    streamWriter.writeObject(getAnnotationValues(instance));
    
  }
  
  public Object create(SerializationStreamReader reader) throws SerializationException {
    return org.drools.ide.common.client.modeldriven.ModelAnnotation_FieldSerializer.instantiate(reader);
  }
  
  public void deserial(SerializationStreamReader reader, Object object) throws SerializationException {
    org.drools.ide.common.client.modeldriven.ModelAnnotation_FieldSerializer.deserialize(reader, (org.drools.ide.common.client.modeldriven.ModelAnnotation)object);
  }
  
  public void serial(SerializationStreamWriter writer, Object object) throws SerializationException {
    org.drools.ide.common.client.modeldriven.ModelAnnotation_FieldSerializer.serialize(writer, (org.drools.ide.common.client.modeldriven.ModelAnnotation)object);
  }
  
}
